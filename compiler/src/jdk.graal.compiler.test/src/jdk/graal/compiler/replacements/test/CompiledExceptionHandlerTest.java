/*
 * Copyright (c) 2011, 2025, Oracle and/or its affiliates. All rights reserved.
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
package jdk.graal.compiler.replacements.test;

import jdk.graal.compiler.core.phases.HighTier;
import jdk.graal.compiler.core.test.GraalCompilerTest;
import jdk.graal.compiler.nodes.StructuredGraph;
import jdk.graal.compiler.nodes.StructuredGraph.Builder;
import jdk.graal.compiler.nodes.ValueNode;
import jdk.graal.compiler.nodes.graphbuilderconf.GraphBuilderContext;
import jdk.graal.compiler.nodes.graphbuilderconf.InlineInvokePlugin.InlineInfo;
import jdk.graal.compiler.nodes.java.ExceptionObjectNode;
import jdk.graal.compiler.options.OptionValues;
import jdk.graal.compiler.phases.PhaseSuite;
import jdk.graal.compiler.phases.tiers.HighTierContext;
import jdk.graal.compiler.phases.tiers.Suites;
import org.junit.Assert;
import org.junit.Test;

import jdk.vm.ci.meta.ResolvedJavaMethod;

/**
 * Tests compilation of a hot exception handler.
 */
public class CompiledExceptionHandlerTest extends GraalCompilerTest {

    @Override
    protected Suites createSuites(OptionValues options) {
        return super.createSuites(new OptionValues(options, HighTier.Options.Inline, false));
    }

    @Override
    protected InlineInfo bytecodeParserShouldInlineInvoke(GraphBuilderContext b, ResolvedJavaMethod method, ValueNode[] args) {
        /*
         * We don't care whether other invokes are inlined or not, but we definitely don't want
         * another explicit exception handler in the graph.
         */
        return InlineInfo.DO_NOT_INLINE_NO_EXCEPTION;
    }

    @Override
    protected StructuredGraph parse(Builder builder, PhaseSuite<HighTierContext> graphBuilderSuite) {
        StructuredGraph graph = super.parse(builder, graphBuilderSuite);
        int handlers = graph.getNodes().filter(ExceptionObjectNode.class).count();
        Assert.assertEquals(1, handlers);
        return graph;
    }

    @BytecodeParserNeverInline(invokeWithException = true)
    private static void raiseExceptionSimple(String s) {
        throw new RuntimeException("Raising exception with message \"" + s + "\"");
    }

    @Test
    public void test1() {
        test("test1Snippet", "a string");
        test("test1Snippet", (String) null);
    }

    public static String test1Snippet(String message) {
        if (message != null) {
            try {
                raiseExceptionSimple(message);
            } catch (Exception e) {
                return message + e.getMessage();
            }
        }
        return null;
    }

    @BytecodeParserNeverInline(invokeWithException = true)
    private static void raiseException(String m1, String m2, String m3, String m4, String m5) {
        throw new RuntimeException(m1 + m2 + m3 + m4 + m5);
    }

    @Test
    public void test2() {
        test("test2Snippet", "m1", "m2", "m3", "m4", "m5");
        test("test2Snippet", null, "m2", "m3", "m4", "m5");
    }

    public static String test2Snippet(String m1, String m2, String m3, String m4, String m5) {
        if (m1 != null) {
            try {
                raiseException(m1, m2, m3, m4, m5);
            } catch (Exception e) {
                return m5 + m4 + m3 + m2 + m1;
            }
        }
        return m4 + m3;
    }
}
