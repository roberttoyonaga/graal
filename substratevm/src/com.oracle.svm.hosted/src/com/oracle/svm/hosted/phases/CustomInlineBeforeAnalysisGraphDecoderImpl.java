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
import com.oracle.svm.hosted.Cutoff;
import jdk.graal.compiler.graph.Node;
import jdk.graal.compiler.nodes.StructuredGraph;
import jdk.graal.compiler.nodes.ValueNode;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import com.oracle.svm.hosted.TargetPath;

import java.util.List;

public class CustomInlineBeforeAnalysisGraphDecoderImpl extends InlineBeforeAnalysisGraphDecoderImpl {

    public class CustomInlineBeforeAnalysisMethodScope extends InlineBeforeAnalysisGraphDecoder.InlineBeforeAnalysisMethodScope {
        boolean isOnInlinePath;
        boolean isBeyondCutoff;
        int depth;

        CustomInlineBeforeAnalysisMethodScope(StructuredGraph targetGraph, PEMethodScope caller, LoopScope callerLoopScope, jdk.graal.compiler.nodes.EncodedGraph encodedGraph,
                        com.oracle.graal.pointsto.meta.AnalysisMethod method,
                        InvokeData invokeData, int inliningDepth, ValueNode[] arguments, boolean isOnInlinePath, boolean isBeyondCutoff, int depth) {
            super(targetGraph, caller, callerLoopScope, encodedGraph, method, invokeData, inliningDepth, arguments);
            this.isOnInlinePath = isOnInlinePath;
            this.isBeyondCutoff = isBeyondCutoff;
            this.depth = depth;
        }
    }

    private final List<TargetPath> inlinePaths;
    private final List<Cutoff> cutoffs;
    // Track how much of each path has been matched. Each value represents the next step to be matched.
    private final int[] pathProgress;
    // Each value represents the depth we expect the next match to be at. One value for each path.
    private final int[] pathdepth;

    public CustomInlineBeforeAnalysisGraphDecoderImpl(BigBang bb, InlineBeforeAnalysisPolicy policy, StructuredGraph graph, HostedProviders providers, List<TargetPath> paths,
                    List<Cutoff> cutoffs) {
        super(bb, policy, graph, providers);
        this.inlinePaths = paths;
        this.cutoffs = cutoffs;
        this.pathProgress = new int[inlinePaths.size()];
        this.pathdepth = new int[inlinePaths.size()];
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
     *
     * We do not allow recursion in target paths. The part of the graph we encounter during the DFS
     * is directed-acyclic.
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

        // At this point it is known the callee is not a root.
        // Determine whether the callee is the next step on any target path.
        int currentDepth = getDepth(caller);
        for (int targetPathIdx = 0; targetPathIdx < inlinePaths.size(); targetPathIdx++) {
            TargetPath targetPath = inlinePaths.get(targetPathIdx);
            int currentProgressIdx = pathProgress[targetPathIdx];
            int targetDepth = pathdepth[targetPathIdx];

            // Skip if path is already complete or wrong depth. targetDepth==0 means any depth
            if (currentProgressIdx == targetPath.size() || (targetDepth != 0 && currentDepth != targetDepth)) {
                continue;
            }

            // Determine whether the callee matches the next step on this target path.
            String targetMethodId = targetPath.getMethodId(currentProgressIdx);
            if (targetMethodId.equals(calleeId)) {
                // Handle callsites if they exist. Caller is non-null since callee is not a root.
                if (currentProgressIdx == 0 && targetPath.getCallsite() != null) {
                    String callerId = getMethodId(cast(caller).method);
                    String callsiteId = targetPath.getCallsite().getMethodId();
                    if (!callsiteId.equals(callerId)) {
                        continue;
                    }
                    targetPath.getCallsite().setFound();
                }
                targetPath.setFound(currentProgressIdx);
                pathProgress[targetPathIdx]++;
                pathdepth[targetPathIdx] = currentDepth + 1;
                result = true;
            }
            // If the current callee does not match, that doesn't necessarily mean we've diverged
            // from this target path.
            // There may be other callees at this depth we will evaluate later.
        }
        return result;
    }

    /** The cutoff itself also gets inlined. The cutoff should generally be small. */
    private boolean isBeyondCutoff(ResolvedJavaMethod method, PEMethodScope caller) {
        boolean result = false;
        String calleeId = getMethodId(method);

        // First check the special/starting case when root method's scope is being created.
        if (caller == null) {
            for (Cutoff cutoff : cutoffs) {
                if (cutoff.getCallsite() != null && cutoff.getCallsite().getMethodId().equals(calleeId)) {
                    cutoff.getCallsite().setFound();
                } else if (!cutoff.isInclusive() && calleeId.equals(cutoff.getMethodId())){
                    cutoff.setFound();
                    result = true;
                }
            }
            return result;
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
        for (Cutoff cutoff : cutoffs) {
            if (!cutoff.getMethodId().equals(calleeId)) {
                continue;
            }
            // The cutoff matches the current callee. Does the callsite match?
            if (cutoff.getCallsite() == null) {
                // The callee is a cutoff that we must force inline from all locations.
                cutoff.setFound();
                return true;
            } else if (getMethodId(callerScope.method).equals(cutoff.getCallsite().getMethodId())) {
                // The callee is a cutoff and its caller matches the corresponding target callsite.
                cutoff.setFound();
                return true;
            }
            // The callsites do not match, continue searching the cutoff list.
        }
        return false;
    }

    private static int getDepth(PEMethodScope caller) {
        if (caller == null) {
            return 0;
        }
        CustomInlineBeforeAnalysisMethodScope callerScope = cast(caller);
        return callerScope.depth + 1;
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
                        inliningDepth, arguments, onInlinePath(method, caller), isBeyondCutoff(method, caller), getDepth(caller));
        return scope;
    }
}
