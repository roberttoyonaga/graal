/*
 * Copyright (c) 2016, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.svm.hosted.code;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;

import org.graalvm.nativeimage.ImageSingletons;

import com.oracle.graal.pointsto.meta.AnalysisMethod;
import com.oracle.svm.core.heap.RestrictHeapAccess.Access;
import com.oracle.svm.core.heap.RestrictHeapAccessCallees;
import com.oracle.svm.core.option.HostedOptionKey;
import com.oracle.svm.core.util.UserError;
import com.oracle.svm.hosted.code.RestrictHeapAccessCalleesImpl.RestrictionInfo;
import com.oracle.svm.hosted.meta.HostedMethod;
import com.oracle.svm.hosted.meta.HostedUniverse;

import jdk.graal.compiler.debug.DebugContext;
import jdk.graal.compiler.graph.NodeSourcePosition;
import jdk.graal.compiler.options.Option;

public final class RestrictHeapAccessAnnotationChecker {

    /*
     * Command line options so errors are not fatal to the build.
     */
    public static class Options {
        @Option(help = "Print warnings for @RestrictHeapAccess annotations.")//
        public static final HostedOptionKey<Boolean> PrintRestrictHeapAccessWarnings = new HostedOptionKey<>(true);

        @Option(help = "Print path for @RestrictHeapAccess warnings.")//
        public static final HostedOptionKey<Boolean> PrintRestrictHeapAccessPath = new HostedOptionKey<>(true);
    }

    /** Entry point method. */
    public static void check(DebugContext debug, HostedUniverse universe, Collection<HostedMethod> methods) {
        final RestrictHeapAccessWarningVisitor visitor = new RestrictHeapAccessWarningVisitor(universe);
        for (HostedMethod method : methods) {
            visitor.visitMethod(debug, method);
        }
    }

    static CompilationGraph.AllocationInfo checkViolatingNode(CompilationGraph graph) {
        if (graph != null) {
            for (CompilationGraph.AllocationInfo node : graph.getAllocationInfos()) {
                return node;
            }
        }
        return null;
    }

    /** A HostedMethod visitor that checks for violations of heap access restrictions. */
    static class RestrictHeapAccessWarningVisitor {

        private final HostedUniverse universe;
        private final RestrictHeapAccessCalleesImpl restrictHeapAccessCallees;

        RestrictHeapAccessWarningVisitor(HostedUniverse universe) {
            this.universe = universe;
            this.restrictHeapAccessCallees = (RestrictHeapAccessCalleesImpl) ImageSingletons.lookup(RestrictHeapAccessCallees.class);
        }

        @SuppressWarnings("try")
        public void visitMethod(DebugContext debug, HostedMethod method) {
            /* If this is not a method that must not allocate, then everything is fine. */
            RestrictionInfo info = restrictHeapAccessCallees.getRestrictionInfo(method);
            if (info == null || info.getAccess() == Access.UNRESTRICTED) {
                return;
            }
            /* Look through the graph for this method and see if it allocates. */
            final CompilationGraph graph = method.compilationInfo.getCompilationGraph();
            if (RestrictHeapAccessAnnotationChecker.checkViolatingNode(graph) != null) {
                try (DebugContext.Scope s = debug.scope("RestrictHeapAccessAnnotationChecker", graph, method, this)) {
                    postRestrictHeapAccessWarning(method.getWrapped(), restrictHeapAccessCallees.getCallerMap());
                } catch (Throwable t) {
                    throw debug.handle(t);
                }
            }
        }

        private void postRestrictHeapAccessWarning(AnalysisMethod violatingCallee, Map<AnalysisMethod, RestrictionInfo> callerMap) {
            if (Options.PrintRestrictHeapAccessWarnings.getValue()) {
                Access violatedAccess = callerMap.get(violatingCallee).getAccess();
                String message = "@RestrictHeapAccess warning: ";

                /* Walk from callee to caller building a list I can walk from caller to callee. */
                final Deque<RestrictionInfo> callChain = new ArrayDeque<>();
                AnalysisMethod current = violatingCallee;
                while (current != null) {
                    final RestrictionInfo info = callerMap.get(current);
                    callChain.addFirst(info);
                    current = info.getCaller();
                }
                /* Walk from caller to callee building a list to the nearest violating method. */
                final Deque<RestrictionInfo> allocationList = new ArrayDeque<>();
                for (RestrictionInfo element : callChain) {
                    allocationList.addLast(element);
                    if (checkHostedViolatingNode(element.getMethod()) != null) {
                        break;
                    }
                }
                assert !allocationList.isEmpty();
                if (allocationList.size() == 1) {
                    final StackTraceElement allocationStackTraceElement = getViolatingStackTraceElement(violatingCallee);
                    if (allocationStackTraceElement != null) {
                        message += "Restricted method '" + allocationStackTraceElement.toString() + "' directly violates restriction " + violatedAccess + ".";
                    } else {
                        message += "Restricted method '" + violatingCallee.format("%H.%n(%p)") + "' directly violates restriction " + violatedAccess + ".";
                    }
                } else {
                    final RestrictionInfo first = allocationList.getFirst();
                    final RestrictionInfo last = allocationList.getLast();
                    message += "Restricted method: '" + first.getMethod().format("%h.%n(%p)") + "' calls '" +
                                    last.getMethod().format("%h.%n(%p)") + "' that violates restriction " + violatedAccess + ".";
                    if (Options.PrintRestrictHeapAccessPath.getValue()) {
                        message += System.lineSeparator() + "  [Path:";
                        for (RestrictionInfo element : allocationList) {
                            if (element != first) { // first element has no caller
                                message += System.lineSeparator() + "    " + element.getInvocationStackTraceElement().toString();
                            }
                        }
                        final StackTraceElement allocationStackTraceElement = getViolatingStackTraceElement(last.getMethod());
                        if (allocationStackTraceElement != null) {
                            message += System.lineSeparator() + "    " + allocationStackTraceElement.toString();
                        } else {
                            message += System.lineSeparator() + "    " + last.getMethod().format("%H.%n(%p)");
                        }
                        message += "]";
                    }
                }
                throw UserError.abort("%s", message);
            }
        }

        CompilationGraph.AllocationInfo checkHostedViolatingNode(AnalysisMethod method) {
            final HostedMethod hostedMethod = universe.optionalLookup(method);
            if (hostedMethod != null) {
                final CompilationGraph graph = hostedMethod.compilationInfo.getCompilationGraph();
                return checkViolatingNode(graph);
            }
            return null;
        }

        /**
         * Look through the graph of the corresponding HostedMethod to see if it allocates.
         */
        private StackTraceElement getViolatingStackTraceElement(AnalysisMethod method) {
            final HostedMethod hostedMethod = universe.optionalLookup(method);
            if (hostedMethod != null) {
                final CompilationGraph graph = hostedMethod.compilationInfo.getCompilationGraph();
                CompilationGraph.AllocationInfo node = checkViolatingNode(graph);
                if (node != null) {
                    final NodeSourcePosition sourcePosition = node.getNodeSourcePosition();
                    if (sourcePosition != null && sourcePosition.getBCI() != -1) {
                        return method.asStackTraceElement(sourcePosition.getBCI());
                    }
                }
            }
            return null;
        }
    }
}
