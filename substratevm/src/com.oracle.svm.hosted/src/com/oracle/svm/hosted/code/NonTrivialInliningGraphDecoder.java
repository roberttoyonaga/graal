/*
 * Copyright (c) 2025, 2025, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2025, 2025, IBM Inc. All rights reserved.
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

import java.util.concurrent.ConcurrentHashMap;

import com.oracle.graal.pointsto.flow.AnalysisParsedGraph;
import com.oracle.svm.core.util.VMError;
import com.oracle.svm.hosted.meta.HostedMethod;
import jdk.graal.compiler.bytecode.BytecodeProvider;
import jdk.graal.compiler.nodes.EncodedGraph;
import jdk.graal.compiler.nodes.StructuredGraph;
import jdk.graal.compiler.nodes.ValueNode;
import jdk.graal.compiler.nodes.graphbuilderconf.InlineInvokePlugin;
import jdk.graal.compiler.nodes.java.MethodCallTargetNode;
import jdk.graal.compiler.phases.contract.NodeCostUtil;
import jdk.graal.compiler.phases.util.Providers;
import jdk.graal.compiler.replacements.PEGraphDecoder;
import jdk.vm.ci.meta.ResolvedJavaMethod;

class NonTrivialInliningGraphDecoder extends PEGraphDecoder {
    boolean inlinedDuringDecoding;
    int round;

    NonTrivialInliningGraphDecoder(StructuredGraph graph, Providers providers, com.oracle.svm.hosted.code.CompileQueue.NonTrivialInliningPlugin inliningPlugin, int round) {
        super(AnalysisParsedGraph.HOST_ARCHITECTURE, graph, providers, null,
                null,
                new InlineInvokePlugin[]{inliningPlugin},
                null, null, null, null,
                new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), true, false);
        this.round = round;
    }

    @Override
    protected EncodedGraph lookupEncodedGraph(ResolvedJavaMethod method, BytecodeProvider intrinsicBytecodeProvider) {
        return ((HostedMethod) method).compilationInfo.getCompilationGraph().getEncodedGraph();
    }

    @Override
    protected LoopScope trySimplifyInvoke(PEMethodScope methodScope, LoopScope loopScope, InvokeData invokeData, MethodCallTargetNode callTarget) {
        return super.trySimplifyInvoke(methodScope, loopScope, invokeData, callTarget);
    }

    /**
     * Calculate the size before inlining. It will be used later to calculate the callee cost when
     * making inlining decisions.
     */
    @Override
    protected LoopScope doInline(PEMethodScope methodScope, LoopScope loopScope, InvokeData invokeData, InlineInvokePlugin.InlineInfo inlineInfo, ValueNode[] arguments) {
        // First, get the root method
        PEMethodScope scope = methodScope;
        while (scope.caller != null) {
            scope = scope.caller;
        }
        HostedMethod root = (HostedMethod) scope.method;
        HostedMethod callee = (HostedMethod) inlineInfo.getMethodToInline();
        // If needed, create a CalleeInfo for the current callee
        if (!root.compilationInfo.callees.containsKey(callee)) {
            // If this callee is inlined, this CalleeInfo will not survive beyond the current round.
            root.compilationInfo.callees.put(callee, new CalleeInfo(callee, round));
        }
        // Stash the graph size in the CalleeInfo. Recursion (due to multiple callsites at different
        // depths) should not be a problem since we only go one level deep per round.
        root.compilationInfo.callees.get(callee).sizeBeforeInlining = NodeCostUtil.computeGraphSize(graph);
        return super.doInline(methodScope, loopScope, invokeData, inlineInfo, arguments);
    }

    boolean canInline(PEMethodScope inlineScope, HostedMethod root, HostedMethod callee) {
        if (callee.shouldBeInlined()) {
            return true;
        }

        CalleeInfo calleeInfo = root.compilationInfo.callees.get(callee);
        VMError.guarantee(calleeInfo != null, "This should have been created in doInline");

        double currentSize = NodeCostUtil.computeGraphSize(graph);
        double calleeCost = (currentSize - calleeInfo.sizeBeforeInlining);
        // Similar to the TrivialInliningPhase, we can be a bit more lenient with leaf methods
        if (inlineScope.invokeCount == 0) {
            calleeCost = calleeCost / 4.0;
        }

        double offset = 1.0;
        double bc = (offset + inlineScope.benefit) * Math.pow(root.compilationInfo.callsites.get(), 2) / calleeCost;
        // double bc = (offset + inlineScope.benefit) / calleeCost;
        /*
         * Only inline the top method marked from previous round. On round 1 we don't inline
         * anything.
         */
        double t1 = 5;
        double t2 = 1;
        double threshold = t1 * Math.pow(2, (calleeCost / (16 * t2)));
        if (bc >= threshold) {
            return true;
        }
        /*
         * If we fail to inline, the CalleeInfo remains in the root's set, so we don't retrial it in
         * future rounds unless it changes.
         */
        return false;
    }

    @Override
    protected void finishInlining(MethodScope is) {
        PEMethodScope inlineScope = (PEMethodScope) is;
        PEMethodScope callerScope = inlineScope.caller;
        HostedMethod callee = (HostedMethod) inlineScope.method;
        LoopScope callerLoopScope = inlineScope.callerLoopScope;
        InvokeData invokeData = inlineScope.invokeData;
        HostedMethod root = (HostedMethod) callerScope.method;

        VMError.guarantee(callerScope.caller == null, "we should not be evaluating beyond the root's immediate callees");

        if (!canInline(inlineScope, root, callee)) {
            // This block is essentially the same as InlineBeforeAnalysisGraphDecoder#finishInlining
            if (invokeData.invokePredecessor.next() != null) {
                killControlFlowNodes(inlineScope, invokeData.invokePredecessor.next());
                assert invokeData.invokePredecessor.next() == null : "Successor must have been a fixed node created in the aborted scope, which is deleted now";
            }
            invokeData.invokePredecessor.setNext(invokeData.invoke.asFixedNode());
            if (inlineScope.exceptionPlaceholderNode != null) {
                assert invokeData.invoke instanceof jdk.graal.compiler.nodes.InvokeWithExceptionNode : invokeData.invoke;
                assert lookupNode(callerLoopScope, invokeData.exceptionOrderId) == inlineScope.exceptionPlaceholderNode : inlineScope;
                registerNode(callerLoopScope, invokeData.exceptionOrderId, null, true, true);
                ValueNode exceptionReplacement = makeStubNode(callerScope, callerLoopScope, invokeData.exceptionOrderId);
                inlineScope.exceptionPlaceholderNode.replaceAtUsagesAndDelete(exceptionReplacement);
            }
            handleNonInlinedInvoke(callerScope, callerLoopScope, invokeData);
            return;
        }

        /*
         * Commit the callsite count updates for 2nd level callees being copied into the root
         * scope.
         */
        for (var entry : inlineScope.newCallees.entrySet()) {
            HostedMethod hMethod = (HostedMethod) entry.getKey();
            hMethod.compilationInfo.callsites.addAndGet(entry.getValue());
        }
        // Inlining into this callsite removes it.
        callee.compilationInfo.callsites.decrementAndGet();
        // Remove callee from the "seen" set
        root.compilationInfo.callees.remove(callee);

        inlinedDuringDecoding = true;
        super.finishInlining(inlineScope);
    }
}
