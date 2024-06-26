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
package jdk.graal.compiler.hotspot.test;

import jdk.graal.compiler.core.test.GraalCompilerTest;
import jdk.graal.compiler.hotspot.meta.HotSpotForeignCallsProviderImpl;
import jdk.graal.compiler.nodes.ValueNode;
import jdk.graal.compiler.nodes.extended.ForeignCallNode;
import jdk.graal.compiler.nodes.graphbuilderconf.GraphBuilderContext;
import jdk.graal.compiler.nodes.graphbuilderconf.InvocationPlugin;
import jdk.graal.compiler.nodes.graphbuilderconf.InvocationPlugins;
import org.junit.Test;

import jdk.vm.ci.meta.JavaKind;
import jdk.vm.ci.meta.ResolvedJavaMethod;

/**
 * Tests that deoptimization upon exception handling works.
 */
public class ForeignCallDeoptimizeTest extends GraalCompilerTest {

    @Override
    protected void registerInvocationPlugins(InvocationPlugins invocationPlugins) {
        invocationPlugins.register(ForeignCallDeoptimizeTest.class, new InvocationPlugin("testCallInt", int.class) {

            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode arg) {
                ForeignCallNode node = new ForeignCallNode(HotSpotForeignCallsProviderImpl.TEST_DEOPTIMIZE_CALL_INT, arg);
                b.addPush(JavaKind.Int, node);
                return true;
            }
        });
        super.registerInvocationPlugins(invocationPlugins);
    }

    public static int testCallInt(int value) {
        return value;
    }

    public static int testForeignCall(int value) {
        if (testCallInt(value) != value) {
            throw new InternalError();
        }
        return value;
    }

    @Test
    public void test1() {
        test("testForeignCall", 0);
    }

    @Test
    public void test2() {
        test("testForeignCall", -1);
    }
}
