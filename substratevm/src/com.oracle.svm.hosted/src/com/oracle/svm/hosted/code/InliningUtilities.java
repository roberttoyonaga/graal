/*
 * Copyright (c) 2014, 2026, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2026, 2026, IBM Inc. All rights reserved.
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
    public static boolean isTrivialMethod(StructuredGraph graph) {
        boolean useNodeCount = SubstrateOptions.MaxNodesInTrivialLeafMethod.hasBeenSet() || SubstrateOptions.MaxNodesInTrivialMethod.hasBeenSet();
        int numInvokes = 0;
        int graphCost = 0; // Derived from either node count or size

        for (Node n : graph.getNodes()) {
            if (shouldSkipNode(n, useNodeCount)) {
                continue;
            }

            boolean isInvoke = n instanceof MethodCallTargetNode || n instanceof MethodHandleWithExceptionNode;
            if (isInvoke) {
                numInvokes++;
            }

            graphCost += computeNodeCost(n, isInvoke, useNodeCount);
            if (!isCostUnderThreshold(numInvokes, graphCost, graph, useNodeCount)) {
                return false;
            }
        }
        return true;
    }

    private static boolean shouldSkipNode(Node n, boolean useNodeCount) {
        if (n instanceof StartNode || n instanceof FullInfopointNode || n instanceof ValueProxy || n instanceof ValueAnchorNode) {
            return true;
        }
        return useNodeCount && (n instanceof ParameterNode || n instanceof FrameState);
    }

    private static int computeNodeCost(Node n, boolean isInvoke, boolean useNodeCount) {
        if (useNodeCount) {
            return isInvoke ? 0 : 1;
        } else {
            return n.estimatedNodeSize().value;
        }
    }

    private static boolean isCostUnderThreshold(int numInvokes, int value, StructuredGraph graph, boolean useNodeCount) {
        if (numInvokes == 0) {
            // This is a leaf method => we can be generous.
            return value <= (useNodeCount ? SubstrateOptions.MaxNodesInTrivialLeafMethod.getValue(graph.getOptions()) : SubstrateOptions.MaxTrivialLeafMethodSize.getValue(graph.getOptions()));
        } else if (numInvokes <= SubstrateOptions.MaxInvokesInTrivialMethod.getValue(graph.getOptions())) {
            return value <= (useNodeCount ? SubstrateOptions.MaxNodesInTrivialMethod.getValue(graph.getOptions()) : SubstrateOptions.MaxTrivialMethodSize.getValue(graph.getOptions()));
        } else {
            return false;
        }
    }
}
