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
        boolean isBeyondCutoff;

        CustomInlineBeforeAnalysisMethodScope(StructuredGraph targetGraph, PEMethodScope caller, LoopScope callerLoopScope, jdk.graal.compiler.nodes.EncodedGraph encodedGraph,
                        com.oracle.graal.pointsto.meta.AnalysisMethod method,
                        InvokeData invokeData, int inliningDepth, ValueNode[] arguments, boolean isOnInlinePath, boolean isBeyondCutoff) {
            super(targetGraph, caller, callerLoopScope, encodedGraph, method, invokeData, inliningDepth, arguments);
            this.isOnInlinePath = isOnInlinePath;
            this.isBeyondCutoff = isBeyondCutoff;
        }
    }

    private final List<TargetPath> inlinePaths;
    private final List<TargetPath> cutoffs;

    public CustomInlineBeforeAnalysisGraphDecoderImpl(BigBang bb, InlineBeforeAnalysisPolicy policy, StructuredGraph graph, HostedProviders providers, List<TargetPath> paths,
                    List<TargetPath> cutoffs) {
        super(bb, policy, graph, providers);
        this.inlinePaths = paths;
        this.cutoffs = cutoffs;
    }

    @Override
    protected void maybeAbortInlining(MethodScope ms, @SuppressWarnings("unused") LoopScope loopScope, Node node) {
        CustomInlineBeforeAnalysisMethodScope methodScope = cast(ms);
        if (methodScope.isOnInlinePath || methodScope.isBeyondCutoff) {
            // If the caller scope should be force inlined, do not abort.
            // We are evaluating whether the caller should be inlined by checking its callees.
            // Similar to the alwaysInlineInvoke check, we do not update the accumulative counters.
            return;
        }
        super.maybeAbortInlining(ms, loopScope, node);
    }

    protected static CustomInlineBeforeAnalysisMethodScope cast(MethodScope methodScope) {
        return (CustomInlineBeforeAnalysisMethodScope) methodScope;
    }

    /**
     * Determine whether the next callee is on a target callpath. It is possible that the actual
     * path taken overlaps multiple target paths, resulting in a single long inlining chain.
     */
    private boolean onInlinePath(ResolvedJavaMethod method, PEMethodScope caller) {
        boolean result = false;
        String calleeId = getMethodId(method);

        // First check the special/starting case when root method's scope is being created.
        if (caller == null) {
            for (TargetPath targetPath : inlinePaths) {
                if (targetPath.getCallsite() != null && targetPath.getCallsite().getMethodId().equals(calleeId)) {
                    targetPath.getCallsite().setFound();
                    // Can't return immediately. May need to flag multiple paths as found.
                    result = true;
                }
                /*
                 * If a callsite is not specified, and the root method argument matches the first
                 * step on the path, do nothing. There is no caller for the root to be inlined into.
                 */
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

        // Determine whether the callee is the next step on any target path.
        for (TargetPath targetPath : inlinePaths) {
            int nextIdx = comparePaths(targetPath, actualPath);
            if (nextIdx >= 0) {
                // Paths match thus far. Now check whether the next step also matches.
                if (nextIdx < targetPath.size() && targetPath.getMethodId(nextIdx).equals(calleeId)) {
                    targetPath.setFound(nextIdx);
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

        while (targetIdx < targetPath.size() && actualIdx < actualPath.size()) {
            if (actualPath.get(actualIdx).equals(targetPath.getMethodId(targetIdx))) {
                // Steps match between paths. Now account for possible callsites when handling roots
                if (targetIdx == 0) {
                    if (targetPath.getCallsite() != null && (actualIdx == 0 || !targetPath.getCallsite().getMethodId().equals(actualPath.get(actualIdx - 1)))) {
                        // If callsite exists, ensure it matches
                        actualIdx++;
                        continue;
                    } else if (targetPath.getCallsite() == null && actualIdx == 0) {
                        // If callsite doesn't exist, the roots of the actual path and target path
                        // should not match.
                        return -1;
                    }
                }
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

    /** The cutoff itself also gets inlined. The cutoff should generally be small. */
    private boolean isBeyondCutoff(ResolvedJavaMethod method, PEMethodScope caller) {
        String calleeId = getMethodId(method);

        // First check the special/starting case when root method's scope is being created.
        if (caller == null) {
            for (TargetPath cutoff : cutoffs) {
                if (cutoff.getCallsite() != null && cutoff.getCallsite().getMethodId().equals(calleeId)) {
                    cutoff.getCallsite().setFound();
                }
            }
            // Even if this root method matches a cutoff, there is no caller to inline it into.
            return false;
        }

        // At this point, the callee being evaluated is not a root.
        CustomInlineBeforeAnalysisMethodScope callerScope = cast(caller);

        // All methods transitively called after the cutoff (inclusive) are force inlined.
        if (callerScope.isBeyondCutoff) {
            // TODO maybe allow for node size threshold conditions
            // The problem is, unlike the IAA stage, we don't have the decoded graph yet.
            // So we need to visit all the callees to determine the number of invokes and nodes
            // (like what the default policy does).
            // The best solution is to change createAccumulativeInlineScope to accept thresholds
            // based on whether the scope is a cutoff.
            // But that requires invasive changes.
            return true;
        }

        /*
         * At this point, the callee being evaluated is not a root and is not past an encountered
         * cutoff. Determine whether the callee itself is a cutoff.
         */
        for (TargetPath cutoff : cutoffs) {
            if (!cutoff.getFirst().getMethodId().equals(calleeId)) {
                continue;
            }
            // The cutoff matches the current callee. Does the callsite match?
            if (cutoff.getCallsite() == null) {
                // The callee is a cutoff that we must force inline from all locations.
                cutoff.getFirst().setFound();
                return true;
            } else if (getMethodId(callerScope.method).equals(cutoff.getCallsite().getMethodId())) {
                // The callee is a cutoff and its caller matches the corresponding target callsite.
                cutoff.getFirst().setFound();
                return true;
            }
            // The callsites do not match, continue searching the cutoff list.
        }
        return false;
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
                        inliningDepth, arguments, onInlinePath(method, caller), isBeyondCutoff(method, caller));
        return scope;
    }
}
