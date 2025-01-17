/*
 * Copyright (c) 2014, 2022, Oracle and/or its affiliates. All rights reserved.
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

import com.oracle.svm.hosted.code.CompileQueue.CompileReason;
import com.oracle.svm.hosted.code.CompileQueue.DirectCallReason;
import jdk.graal.compiler.graph.Node;
import jdk.graal.compiler.nodes.FrameState;
import jdk.graal.compiler.nodes.FullInfopointNode;
import jdk.graal.compiler.nodes.ParameterNode;
import jdk.graal.compiler.nodes.StartNode;
import jdk.graal.compiler.nodes.StructuredGraph;
import jdk.graal.compiler.nodes.extended.ValueAnchorNode;
import jdk.graal.compiler.nodes.java.MethodCallTargetNode;
import jdk.graal.compiler.nodes.spi.ValueProxy;
import jdk.graal.compiler.replacements.nodes.MethodHandleWithExceptionNode;

import com.oracle.svm.core.SubstrateOptions;

public class InliningUtilities {

    public static boolean isTrivialMethod(StructuredGraph graph, CompileReason reason) {
        int numInvokes = 0;
        int numOthers = 0;
        for (Node n : graph.getNodes()) {
            if (n instanceof StartNode || n instanceof ParameterNode || n instanceof FullInfopointNode || n instanceof ValueProxy || n instanceof ValueAnchorNode || n instanceof FrameState) {
                continue;
            }
            if (n instanceof MethodCallTargetNode || n instanceof MethodHandleWithExceptionNode) {
                numInvokes++;
            } else {
                numOthers++;
            }

            if (!shouldBeTrivial(numInvokes, numOthers, graph, reason)) {
                return false;
            }
        }

        return true;
    }

    private static boolean shouldBeTrivial(int numInvokes, int numOthers, StructuredGraph graph, CompileReason reason) {
        final int maxNodesInTrivialLeafMethod = SubstrateOptions.MaxNodesInTrivialLeafMethod.getValue(graph.getOptions()).intValue();
        final int maxInvokesInTrivialMethod = SubstrateOptions.MaxInvokesInTrivialMethod.getValue(graph.getOptions()).intValue();
        final int maxNodesInTrivialMethod = SubstrateOptions.MaxNodesInTrivialMethod.getValue(graph.getOptions()).intValue();
        final String methodId = graph.method().format("%h.%n(%p)");
        if (numInvokes == 0) {
            // This is a leaf method => we can be generous.
            final boolean isTrivial = numOthers <= maxNodesInTrivialLeafMethod;
            if (isDebug(methodId, reason))
            {
                System.out.printf(
                        "[InliningUtilities.shouldBeTrivial][%s->%s] is leaf method, trivial check: number of non-invoke nodes %d <= %d? %b%n"
                        , reason == null ? "()" : reason.toString()
                        , methodId
                        , numOthers
                        , maxNodesInTrivialLeafMethod
                        , isTrivial
                );
            }
            return isTrivial;
        } else {
            if (numInvokes <= maxInvokesInTrivialMethod) {
                final boolean isTrivial = numOthers <= maxNodesInTrivialMethod;
                if (isDebug(methodId, reason))
                {
                    System.out.printf(
                            "[InliningUtilities.shouldBeTrivial][%s->%s] is not leaf method && numInvokes(%d) <= MaxInvokesInTrivialMethod(%d) && numOthers(%d) <= MaxNodesInTrivialMethod(%d)? %b%n"
                            , reason == null ? "()" : reason.toString()
                            , methodId
                            , numInvokes
                            , maxInvokesInTrivialMethod
                            , numOthers
                            , maxNodesInTrivialMethod
                            , isTrivial
                    );
                }
                return isTrivial;
            } else {
                if (isDebug(methodId, reason))
                {
                    System.out.printf(
                            "[InliningUtilities.shouldBeTrivial][%s->%s] is not leaf method and not inlined (num invokes %d, MaxInvokesInTrivialMethod(%d), num others %d, MaxNodesInTrivialMethod(%d)%n"
                            , reason == null ? "()" : reason.toString()
                            , methodId
                            , numInvokes
                            , maxInvokesInTrivialMethod
                            , numOthers
                            , maxNodesInTrivialMethod
                    );
                }
                return false;
            }
        }
    }
    public static boolean isDebug(String methodId, CompileReason reason) {
        if (reason != null) {
            final String callerMethodId = reason.toString().replace("Direct call from char ", "");
            return isCallerMethod(callerMethodId) && isTargetMethod(methodId);
        }
        return isTargetMethod(methodId);
    }

    public static boolean isCallerMethod(String caller) {
        return
                caller.contains("IBAHelper");

    }

    private static boolean isTargetMethod(String methodId){
        return
//                methodId.contains("IBAHelper") ||
                methodId.contains("f8_inlining_phase");


    }
}
