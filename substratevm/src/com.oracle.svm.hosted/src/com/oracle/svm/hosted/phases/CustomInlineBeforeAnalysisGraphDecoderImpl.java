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

package com.oracle.svm.hosted.src.com.oracle.svm.hosted.phases;

import com.oracle.graal.pointsto.BigBang;
import com.oracle.graal.pointsto.meta.HostedProviders;
import com.oracle.graal.pointsto.phases.InlineBeforeAnalysisGraphDecoder;
import com.oracle.graal.pointsto.phases.InlineBeforeAnalysisPolicy;
import jdk.graal.compiler.graph.Node;
import jdk.graal.compiler.nodes.StructuredGraph;
import jdk.graal.compiler.nodes.ValueNode;
import jdk.vm.ci.meta.ResolvedJavaMethod;

import java.util.ArrayList;
import java.util.List;

public class CustomInlineBeforeAnalysisGraphDecoderImpl extends InlineBeforeAnalysisGraphDecoder {

    public class CustomInlineBeforeAnalysisMethodScope extends InlineBeforeAnalysisGraphDecoder.InlineBeforeAnalysisMethodScope {
        boolean isOnInlinePath;

        CustomInlineBeforeAnalysisMethodScope(StructuredGraph targetGraph, PEMethodScope caller, LoopScope callerLoopScope, jdk.graal.compiler.nodes.EncodedGraph encodedGraph,
                        com.oracle.graal.pointsto.meta.AnalysisMethod method,
                        InvokeData invokeData, int inliningDepth, ValueNode[] arguments, boolean isOnInlinePath) {
            super(targetGraph, caller, callerLoopScope, encodedGraph, method, invokeData, inliningDepth, arguments);
            this.isOnInlinePath = isOnInlinePath;
        }
    }

    private final List<List<String>> inlinePaths;

    public CustomInlineBeforeAnalysisGraphDecoderImpl(BigBang bb, InlineBeforeAnalysisPolicy policy, StructuredGraph graph, HostedProviders providers) {
        super(bb, policy, graph, providers, null);
        this.inlinePaths = new ArrayList<>();
        headersMultiMapAddBenchmark();
        charAtBenchmark();
    }

    private void charAtBenchmark() {

        this.inlinePaths.add(new ArrayList<>());
        this.inlinePaths.getLast().add("Lorg/sample/Benchmarks;charAtLatin1");
        this.inlinePaths.getLast().add("Ljava/lang/String;charAt");
        this.inlinePaths.getLast().add("Ljava/lang/StringLatin1;charAt");
        this.inlinePaths.getLast().add("Ljava/lang/StringLatin1;checkIndex");

        this.inlinePaths.add(new ArrayList<>());
        this.inlinePaths.getLast().add("Lorg/sample/Benchmarks;charAtUtf16");
        this.inlinePaths.getLast().add("Ljava/lang/String;charAt");
        this.inlinePaths.getLast().add("Ljava/lang/StringUTF16;charAt");
        this.inlinePaths.getLast().add("Ljava/lang/StringUTF16;checkIndex");
    }

    private void headersMultiMapAddBenchmark() {
        // TODO read from a xml file
        // order matters. Root --> leaf
        this.inlinePaths.add(new ArrayList<>());
        this.inlinePaths.getLast().add("Lorg/sample/Benchmarks;headersMultiMapAdd");
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/headers/HeadersMultiMap;add");
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/headers/HeadersMultiMap;add0");

        this.inlinePaths.add(new ArrayList<>());
        this.inlinePaths.getLast().add("Ljava/lang/String;charAt");
        this.inlinePaths.getLast().add("Ljava/lang/StringLatin1;charAt");

        this.inlinePaths.add(new ArrayList<>());
        this.inlinePaths.getLast().add("Lio/netty/util/AsciiString;charAt");
        this.inlinePaths.getLast().add("Lio/netty/util/AsciiString;byteAt");
        this.inlinePaths.getLast().add("Lio/netty/util/internal/PlatformDependent;getByte");

        this.inlinePaths.add(new ArrayList<>());
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/HttpUtils;validateHeader");
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/HttpUtils;validateHeaderName");
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/HttpUtils;validateHeaderName");

        this.inlinePaths.add(new ArrayList<>());
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/HttpUtils;validateHeader");
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/HttpUtils;validateHeaderName");
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/HttpUtils;validateHeaderName0");

        this.inlinePaths.add(new ArrayList<>());
        this.inlinePaths.getLast().add("Lio/netty/util/AsciiString;hashCode");
        this.inlinePaths.getLast().add("Lio/netty/util/internal/PlatformDependent;hashCodeAscii");

        this.inlinePaths.add(new ArrayList<>());
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/HttpUtils;validateHeader");
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/HttpUtils;validateHeaderValue");
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/HttpUtils;validateValueChar");
    }

    private void encoderHeaderAddBenchmark() {
        this.inlinePaths.add(new ArrayList<>());
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/headers/HeadersMultiMap;encode");
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/headers/HeadersMultiMap;encoderHeader");
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/headers/HeadersMultiMap;writeAscii");
        this.inlinePaths.getLast().add("Lio/netty/buffer/AbstractByteBuf;setCharSequence");
        this.inlinePaths.getLast().add("Lio/netty/buffer/AbstractByteBuf;setCharSequence0");

        this.inlinePaths.add(new ArrayList<>());
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/headers/HeadersMultiMap;encode");
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/headers/HeadersMultiMap;encoderHeader");
        this.inlinePaths.getLast().add("Lio/vertx/core/http/impl/headers/HeadersMultiMap;writeAscii");
        this.inlinePaths.getLast().add("Lio/netty/buffer/ByteBufUtil;copy");

        this.inlinePaths.add(new ArrayList<>());
        this.inlinePaths.getLast().add("Lio/netty/buffer/ByteBufUtil;setShortBE");
        this.inlinePaths.getLast().add("Lio/netty/buffer/AbstractByteBuf;setShort");
        this.inlinePaths.getLast().add("Lio/netty/buffer/AbstractByteBuf;checkIndex");

    }

    @Override
    protected void maybeAbortInlining(MethodScope ms, @SuppressWarnings("unused") LoopScope loopScope, Node node) {
        CustomInlineBeforeAnalysisMethodScope methodScope = cast(ms);
        if (methodScope.isOnInlinePath) {
            // If The caller scope is on the inline path, do NOT abort.
            // We are evaluating whether the caller should be inlined by checking its callees.
            return;
        }
        // *** if alwaysInlineInvoke(..) is true, then it doesnt count towards the accumulative
        // counters. TODO Maybe we should override alwaysInlineInvoke??? seems like EE does that
        super.maybeAbortInlining(ms, loopScope, node);
    }

    protected static CustomInlineBeforeAnalysisMethodScope cast(MethodScope methodScope) {
        return (CustomInlineBeforeAnalysisMethodScope) methodScope;
    }

    /** Is the next callee on the target callpath? */
    private boolean onInlinePath(ResolvedJavaMethod method, PEMethodScope caller, InvokeData invokeData) {
        String calleeName = method.getDeclaringClass().getName() + method.getName();

        // First check if the next scope is the root method.
        // createMethodScope is called on on the root method of each DFS.
        if (caller == null) {
            for (List<String> path : inlinePaths) {
                if (path.getFirst().equals(calleeName)) {
                    System.out.println("++++++++++++ root: " + calleeName);
                    return true;
                }
            }
            return false;
        }

        // An optimization. If the caller is not on the path, then the callee also must not be.
        CustomInlineBeforeAnalysisMethodScope callerScope = cast(caller);
        if (!callerScope.isOnInlinePath) {
            return false;
        }

        List<String> actualPath = new ArrayList<>();
        while (callerScope != null) {
            String callerName = callerScope.method.getDeclaringClass().getName() + callerScope.method.getName();
            actualPath.addFirst(callerName);
            callerScope = cast(callerScope.caller);
        }

        for (List<String> path : inlinePaths) {
            if (comparePaths(path, actualPath, calleeName)) {
                System.out.println("------------- " + calleeName);
                return true;
            }
        }
        return false;
    }

    // TODO What if method names match but signatures do not? ResolvedJavaMethod.getParameters()
    // doesn't seem helpful...
    private static boolean comparePaths(List<String> expectedPath, List<String> actualPath, String name) {
        // Check whether the actual path aligns with the first N steps of the target path.
        int i = 0;
        while (i < actualPath.size()) {
            if (!actualPath.get(i).equals(expectedPath.get(i))) {
                return false;
            }
            i++;
        }
        // Check whether the next step also matches.
        return i < expectedPath.size() && expectedPath.get(i).equals(name);
    }

    // Similar to InlineBeforeAnalysisGraphDecoder, we take advantage of a custom MethodScope to
    // propagate data between inlining levels.
    @Override
    protected PEMethodScope createMethodScope(StructuredGraph targetGraph, PEMethodScope caller, LoopScope callerLoopScope,
                    jdk.graal.compiler.nodes.EncodedGraph encodedGraph, ResolvedJavaMethod method, InvokeData invokeData,
                    int inliningDepth, ValueNode[] arguments) {
        boolean onPath = onInlinePath(method, caller, invokeData);

        CustomInlineBeforeAnalysisMethodScope scope = new CustomInlineBeforeAnalysisMethodScope(targetGraph, caller, callerLoopScope,
                        encodedGraph, (com.oracle.graal.pointsto.meta.AnalysisMethod) method, invokeData,
                        inliningDepth, arguments, onPath);
        return scope;
    }
}
