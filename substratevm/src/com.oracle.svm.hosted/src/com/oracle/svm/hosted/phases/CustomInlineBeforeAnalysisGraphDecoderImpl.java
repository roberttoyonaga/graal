/*
 * Copyright (c) 2025, 2025, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2025, 2025, Red Hat Inc. All rights reserved.
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

package com.oracle.svm.hosted.phases;

import com.oracle.graal.pointsto.BigBang;
import com.oracle.graal.pointsto.meta.HostedProviders;
import com.oracle.graal.pointsto.phases.InlineBeforeAnalysisGraphDecoder;
import com.oracle.graal.pointsto.phases.InlineBeforeAnalysisPolicy;
import jdk.graal.compiler.graph.Node;
import jdk.graal.compiler.nodes.StructuredGraph;
import jdk.graal.compiler.nodes.ValueNode;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import com.oracle.svm.hosted.TargetPath;

import java.util.ArrayList;
import java.util.List;

public class CustomInlineBeforeAnalysisGraphDecoderImpl extends InlineBeforeAnalysisGraphDecoderImpl {

    public class CustomInlineBeforeAnalysisMethodScope extends InlineBeforeAnalysisGraphDecoder.InlineBeforeAnalysisMethodScope {
        boolean isOnInlinePath;

        CustomInlineBeforeAnalysisMethodScope(StructuredGraph targetGraph, PEMethodScope caller, LoopScope callerLoopScope, jdk.graal.compiler.nodes.EncodedGraph encodedGraph,
                        com.oracle.graal.pointsto.meta.AnalysisMethod method,
                        InvokeData invokeData, int inliningDepth, ValueNode[] arguments, boolean isOnInlinePath) {
            super(targetGraph, caller, callerLoopScope, encodedGraph, method, invokeData, inliningDepth, arguments);
            this.isOnInlinePath = isOnInlinePath;
        }
    }

    private final List<TargetPath> inlinePaths;

    public CustomInlineBeforeAnalysisGraphDecoderImpl(BigBang bb, InlineBeforeAnalysisPolicy policy, StructuredGraph graph, HostedProviders providers, List<TargetPath> paths) {
        super(bb, policy, graph, providers);
        this.inlinePaths = paths;
    }

    @Override
    protected void maybeAbortInlining(MethodScope ms, @SuppressWarnings("unused") LoopScope loopScope, Node node) {
        CustomInlineBeforeAnalysisMethodScope methodScope = cast(ms);
        if (methodScope.isOnInlinePath) {
            // If the caller scope is on the inline path, never abort.
            // We are evaluating whether the caller should be inlined by checking its callees.
            // Similar to the alwaysInlineInvoke check, we do not update the accumulative counters.
            return;
        }
        super.maybeAbortInlining(ms, loopScope, node);
    }

    protected static CustomInlineBeforeAnalysisMethodScope cast(MethodScope methodScope) {
        return (CustomInlineBeforeAnalysisMethodScope) methodScope;
    }

    /** Is the next callee on the target callpath? */
    private boolean onInlinePath(ResolvedJavaMethod method, PEMethodScope caller) {
        boolean result = false;
        String calleeId = getMethodId(method);

        // First check the special/starting case where we're creating the scope for the root method.
        if (caller == null) {
            for (TargetPath targetPath : inlinePaths) {
                if (targetPath.getCallsite() != null && targetPath.getCallsite().getMethodId().equals(calleeId)) {
                    targetPath.getCallsite().setFound();
                    // Can't return immediately. May need to set found on multiple paths.
                    result = true;
                }
            }
            return result;
        }

        // Reconstruct the actual path taken thus far.
        CustomInlineBeforeAnalysisMethodScope callerScope = cast(caller);
        List<String> actualPath = new ArrayList<>();
        while (callerScope != null) {
            actualPath.addFirst(getMethodId(callerScope.method));
            callerScope = cast(callerScope.caller);
        }

        // Determine whether the method argument is the next step on any target path.
        for (TargetPath expectedPath : inlinePaths) {
            int nextIdx = comparePaths(expectedPath, actualPath);
            if (nextIdx >= 0) {
                // Paths match thus far. Now check whether the next step also matches.
                if (nextIdx < expectedPath.size() && expectedPath.getMethodId(nextIdx).equals(calleeId)) {
                    expectedPath.setFound(nextIdx);
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Returns the next target path index to be compared if the end portion of the actual path
     * overlaps the front portion of the target path. Otherwise -1. If a target path does not have a
     * specified callsite, we need to be able to handle them regardless of the depth they are first
     * encountered at.
     */
    private static int comparePaths(TargetPath targetPath, List<String> actualPath) {
        int targetIdx = 0; // target
        int actualIdx = 0; // actual

        // If a target callsite is specified, check whether it matches the actual root method.
        if (targetPath.getCallsite() != null) {
            if (targetPath.getCallsite().getMethodId().equals(actualPath.get(0))) {
                actualIdx++;
            } else {
                return -1;
            }
        }

        while (targetIdx < targetPath.size() && actualIdx < actualPath.size()) {
            if (actualPath.get(actualIdx).equals(targetPath.getMethodId(targetIdx))) {
                actualIdx++;
                targetIdx++;
            } else if (targetIdx > 0) {
                // paths don't match up
                return -1;
            } else {
                /*
                 * Step forwards through the actual path until we reach a potential start of the
                 * target path.
                 */
                actualIdx++;
            }
        }
        assert targetIdx <= actualIdx;
        return actualIdx == actualPath.size() ? targetIdx : -1;
    }

    /**
     * Modified from {@linkplain jdk.vm.ci.meta.Signature#toMethodDescriptor()}. The format is:
     * "[fully qualified classname][method name](parameter1type...)" Ex.
     * Lio/vertx/core/http/impl/headers/HeadersMultiMap;add(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)
     */
    private static String getMethodId(ResolvedJavaMethod method) {
        StringBuilder sb = new StringBuilder(method.getDeclaringClass().getName());
        sb.append(method.getName());
        sb.append("(");
        for (int i = 0; i < method.getSignature().getParameterCount(false); ++i) {
            sb.append(method.getSignature().getParameterType(i, null).getName());
        }
        sb.append(')');
        return sb.toString();
    }

    // Similar to InlineBeforeAnalysisGraphDecoder, we take advantage of a custom MethodScope to
    // propagate data between inlining levels.
    @Override
    protected PEMethodScope createMethodScope(StructuredGraph targetGraph, PEMethodScope caller, LoopScope callerLoopScope,
                    jdk.graal.compiler.nodes.EncodedGraph encodedGraph, ResolvedJavaMethod method, InvokeData invokeData,
                    int inliningDepth, ValueNode[] arguments) {
        CustomInlineBeforeAnalysisMethodScope scope = new CustomInlineBeforeAnalysisMethodScope(targetGraph, caller, callerLoopScope,
                        encodedGraph, (com.oracle.graal.pointsto.meta.AnalysisMethod) method, invokeData,
                        inliningDepth, arguments, onInlinePath(method, caller));
        return scope;
    }
}
