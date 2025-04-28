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
        // First check if the next scope is the root method.
        // createMethodScope is called on the root method of each DFS.
        if (caller == null) {
            for (TargetPath targetPath : inlinePaths) {
                if (targetPath.getMethodId(0).equals(calleeId)) {
                    targetPath.setFound(0);
                    // Can't return immediately. May need to set found on multiple paths.
                    result = true;
                }
            }
            return result;
        }

        // An optimization. If the caller is not on the path, then the callee also must not be.
        CustomInlineBeforeAnalysisMethodScope callerScope = cast(caller);
        if (!callerScope.isOnInlinePath) {
            return false;
        }

        List<String> actualPath = new ArrayList<>();
        while (callerScope != null) {
            actualPath.addFirst(getMethodId(callerScope.method));
            assert callerScope.isOnInlinePath;
            callerScope = cast(callerScope.caller);
        }

        for (TargetPath expectedPath : inlinePaths) {
            if (comparePaths(expectedPath, actualPath)) {
                // Now check whether the next step also matches.
                int nextIdx = actualPath.size();
                if (nextIdx < expectedPath.size() && expectedPath.getMethodId(nextIdx).equals(calleeId)) {
                    expectedPath.setFound(nextIdx);
                    result = true;
                }
            }
        }
        return result;
    }

    private static boolean comparePaths(TargetPath expectedPath, List<String> actualPath) {
        // Check whether the actual path aligns with the first N steps of the target path.
        int i = 0;
        while (i < actualPath.size()) {
            if (!actualPath.get(i).equals(expectedPath.getMethodId(i))) {
                return false;
            }
            i++;
        }
        return true;
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
