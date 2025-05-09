/*
 * Copyright (c) 2018, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.truffle.api.dsl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.GenerateInline;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystem;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.CachedLegacyTestNodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.NodeChildTest0NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.NodeChildTest1NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.TestNonStaticSpecializationNodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.Uncached10NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.Uncached11NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.Uncached12NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.Uncached1NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.Uncached2NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.Uncached3NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.Uncached4NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.Uncached5NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.Uncached6NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.Uncached7NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.Uncached8NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.Uncached9NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.UncachedTrivial1NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.UncachedTrivial2NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.UncachedTrivial3NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.UncachedTrivial4NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.UncachedTrivial5NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.UncachedTrivial6NodeGen;
import com.oracle.truffle.api.dsl.test.GenerateUncachedTestFactory.UncachedTrivial7NodeGen;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

@SuppressWarnings({"truffle-inlining", "truffle-neverdefault", "truffle-sharing", "unused"})
public class GenerateUncachedTest {

    @GenerateUncached
    abstract static class Uncached1Node extends Node {

        abstract Object execute(Object arg);

        @Specialization(guards = "v == cachedV", limit = "3")
        static String s1(int v, @Cached("v") int cachedV) {
            return "s1";
        }

        @Specialization
        static String s2(double v) {
            return "s2";
        }

    }

    @Test
    public void testUncached1() {
        Uncached1Node node = Uncached1NodeGen.getUncached();
        assertEquals("s1", node.execute(42));
        assertEquals("s2", node.execute(42d));
        try {
            node.execute(42L);
            fail();
        } catch (UnsupportedSpecializationException e) {
        }
    }

    @GenerateUncached
    abstract static class Uncached2Node extends Node {

        abstract Object execute(Object arg);

        @Specialization(guards = "v == cachedV", limit = "3")
        static String s1(int v,
                        @Cached("v") int cachedV) {

            return "s1";
        }

        @Specialization(replaces = "s1")
        static String s2(int v) {
            return "s2";
        }
    }

    @Test
    public void testUncached2() {
        Uncached2Node node = Uncached2NodeGen.getUncached();
        assertEquals("s2", node.execute(42));
        assertEquals("s2", node.execute(43));
    }

    @GenerateUncached
    abstract static class Uncached3Node extends Node {

        static boolean guard;

        abstract Object execute(Object arg);

        @Specialization(guards = "guard")
        static String s1(int v) {
            return "s1";
        }

        @Specialization
        static String s2(int v) {
            return "s2";
        }
    }

    @Test
    public void testUncached3() {
        Uncached3Node node = Uncached3NodeGen.getUncached();
        assertEquals("s2", node.execute(42));
        Uncached3Node.guard = true;
        assertEquals("s1", node.execute(42));
    }

    @GenerateUncached
    abstract static class Uncached4Node extends Node {

        abstract Object execute(Object arg);

        @Specialization(rewriteOn = ArithmeticException.class)
        static String s1(int v) {
            if (v == 42) {
                throw new ArithmeticException();
            }
            return "s1";
        }

        @Specialization(replaces = "s1")
        static String s2(int v) {
            return "s2";
        }
    }

    @Test
    public void testUncached4() {
        Uncached4Node node = Uncached4NodeGen.getUncached();
        assertEquals("s2", node.execute(42));
    }

    @GenerateUncached
    abstract static class Uncached5Node extends Node {

        abstract Object execute(Object arg);

        static Assumption testAssumption = Truffle.getRuntime().createAssumption();

        @SuppressWarnings("truffle-assumption")
        @Specialization(assumptions = "testAssumption")
        static String s1(int v) {
            return "s1";
        }

        @Specialization
        static String s2(int v) {
            return "s2";
        }
    }

    @Test
    public void testUncached5() {
        Uncached5Node node = Uncached5NodeGen.getUncached();
        assertEquals("s1", node.execute(42));
        Uncached5Node.testAssumption.invalidate();
        assertEquals("s2", node.execute(42));
        Uncached5Node.testAssumption = null;
        assertEquals("s2", node.execute(42));
    }

    @TypeSystem
    static class Uncached6TypeSystem {

        @ImplicitCast
        public static long fromInt(int l) {
            return l;
        }

    }

    @TypeSystemReference(Uncached6TypeSystem.class)
    @GenerateUncached
    abstract static class Uncached6Node extends Node {

        abstract Object execute(Object arg);

        @Specialization
        static String s1(long v) {
            return "s1";
        }
    }

    @Test
    public void testUncached6() {
        Uncached6Node node = Uncached6NodeGen.getUncached();
        assertEquals("s1", node.execute(42));
        assertEquals("s1", node.execute(42L));
        assertEquals("s1", node.execute(42L));
    }

    @GenerateUncached
    abstract static class Uncached7Node extends Node {

        abstract Object execute(Object arg);

        @Specialization
        static String s1(int v) {
            return "s1";
        }

        @Specialization
        static String s2(double v) {
            return "s2";
        }

        @Fallback
        static String fallback(Object v) {
            return "fallback";
        }
    }

    @Test
    public void testUncached7() {
        Uncached7Node node = Uncached7NodeGen.getUncached();
        assertEquals("s1", node.execute(42));
        assertEquals("s2", node.execute(42d));
        assertEquals("fallback", node.execute(42f));
    }

    @GenerateUncached
    abstract static class Uncached8Node extends Node {

        abstract Object execute(Object arg);

        @Specialization(guards = "v == cachedV", limit = "3", excludeForUncached = false)
        static String s1(int v, @Cached("v") int cachedV) {
            return "s1";
        }

        @Specialization
        static String s2(int v) { // effectively unreachable for uncached
            return "s2";
        }

    }

    @Test
    public void testUncached8() {
        Uncached8Node node = Uncached8NodeGen.getUncached();
        assertEquals("s1", node.execute(42));
        assertEquals("s1", node.execute(44));
        assertEquals("s1", node.execute(45));
        assertEquals("s1", node.execute(46));
    }

    @GenerateUncached
    abstract static class Uncached9Node extends Node {

        abstract String execute();

        static Assumption getAssumption() {
            return Assumption.ALWAYS_VALID;
        }

        @Specialization(assumptions = "getAssumption()")
        static String s0() {
            return "s0";
        }

        @Specialization(replaces = "s0")
        static String s1() throws ArithmeticException {
            return "s1";
        }

    }

    @Test
    public void testUncached9() {
        Uncached9Node node = Uncached9NodeGen.getUncached();
        assertEquals("s0", node.execute());
    }

    @GenerateUncached
    abstract static class Uncached10Node extends Node {

        abstract String execute();

        static Assumption getAssumption() {
            return Assumption.NEVER_VALID;
        }

        @Specialization(assumptions = "getAssumption()")
        static String s0() {
            return "s0";
        }

        @Specialization(replaces = "s0")
        static String s1() throws ArithmeticException {
            return "s1";
        }
    }

    @Test
    public void testUncached10() {
        Uncached10Node node = Uncached10NodeGen.getUncached();
        assertEquals("s1", node.execute());
    }

    @GenerateUncached
    abstract static class Uncached11Node extends Node {

        abstract String execute();

        @Specialization(excludeForUncached = true)
        static String s0() {
            return "s0";
        }

        @Specialization(replaces = "s0")
        static String s1() {
            return "s1";
        }
    }

    @Test
    public void testUncached11() {
        Uncached11Node node = Uncached11NodeGen.getUncached();
        assertEquals("s1", node.execute());
    }

    @GenerateUncached
    abstract static class Uncached12Node extends Node {

        abstract String execute(Object arg);

        @Specialization
        static String s0(int arg) {
            return "s0";
        }

        @Specialization(replaces = "s0", excludeForUncached = true)
        static String s1(int arg) {
            return "s1";
        }

        // specialization to make AlwaysGenerateOnlySlowPath happy
        @Specialization
        static String s2(double b) {
            return "s2";
        }
    }

    @Test
    public void testUncached12() {
        Uncached12Node node = Uncached12NodeGen.getUncached();
        assertEquals("s0", node.execute(42));
    }

    @GenerateUncached
    abstract static class UncachedTrivial1Node extends Node {

        abstract Object execute(Object arg);

        @Specialization
        static String s1(int v, @Cached("v") int cachedV) {
            return "s1_" + cachedV;
        }
    }

    @Test
    public void testUncachedTrivial1() {
        UncachedTrivial1Node node = UncachedTrivial1NodeGen.getUncached();
        assertEquals("s1_42", node.execute(42));
        assertEquals("s1_43", node.execute(43));
    }

    @GenerateUncached
    abstract static class UncachedTrivial2Node extends Node {

        abstract Object execute(Object arg);

        @Specialization
        static String s1(Object v, @Cached("v.getClass()") Class<?> cachedV) {
            return "s1_" + cachedV.getSimpleName();
        }
    }

    @Test
    public void testUncachedTrivial2() {
        UncachedTrivial2Node node = UncachedTrivial2NodeGen.getUncached();
        assertEquals("s1_Integer", node.execute(42));
        assertEquals("s1_Double", node.execute(42d));
    }

    @GenerateUncached
    abstract static class UncachedTrivial3Node extends Node {

        abstract Object execute(Object arg);

        @Specialization
        static String s1(int v, @Cached("v == 42") boolean cachedV) {
            return "s1_" + cachedV;
        }
    }

    @Test
    public void testUncachedTrivial3() {
        UncachedTrivial3Node node = UncachedTrivial3NodeGen.getUncached();
        assertEquals("s1_true", node.execute(42));
        assertEquals("s1_false", node.execute(43));
    }

    @GenerateUncached
    abstract static class UncachedTrivial4Node extends Node {

        abstract Object execute(Object arg);

        @Specialization
        static String s1(Object v, @Cached("v == null") boolean cachedV) {
            return "s1_" + cachedV;
        }
    }

    @Test
    public void testUncachedTrivial4() {
        UncachedTrivial4Node node = UncachedTrivial4NodeGen.getUncached();
        assertEquals("s1_false", node.execute(42));
        assertEquals("s1_true", node.execute(null));
    }

    @GenerateUncached
    abstract static class UncachedTrivial5Node extends Node {

        abstract Object execute(Object arg);

        @Specialization
        static String s1(Object v,
                        @Cached(value = "foo(v)", allowUncached = true) boolean cached) {
            return "s1_" + cached;
        }

        static boolean foo(Object o) {
            return o == Integer.valueOf(42);
        }

    }

    @Test
    public void testUncachedTrivial5() {
        UncachedTrivial5Node node = UncachedTrivial5NodeGen.getUncached();
        assertEquals("s1_true", node.execute(42));
        assertEquals("s1_false", node.execute(43));
    }

    @GenerateUncached
    abstract static class UncachedTrivial6Node extends Node {

        abstract Object execute(Object arg);

        @Specialization
        static String s1(Object v,
                        @Cached(value = "foo(v)", uncached = "foo(null)") boolean cached) {
            return "s1_" + cached;
        }

        static boolean foo(Object o) {
            return o == Integer.valueOf(42);
        }

    }

    @Test
    public void testUncachedTrivial6() {
        UncachedTrivial6Node node = UncachedTrivial6NodeGen.getUncached();
        assertEquals("s1_false", node.execute(42));
        assertEquals("s1_false", node.execute(43));
    }

    @GenerateUncached
    abstract static class UncachedTrivial7Node extends Node {

        abstract Object execute(Object arg);

        @Specialization
        static String s1(Object v,
                        @Cached(value = "v == null") boolean cached) {
            return "s1_" + cached;
        }

        static boolean foo(Object o) {
            return o == Integer.valueOf(42);
        }

    }

    @Test
    public void testUncachedTrivial7() {
        UncachedTrivial7Node node = UncachedTrivial7NodeGen.getUncached();
        assertEquals("s1_true", node.execute(null));
        assertEquals("s1_false", node.execute(43));
    }

    @GenerateUncached
    public abstract static class TestNonStaticSpecializationNode extends Node {

        public abstract Object execute(Object arg);

        @Specialization
        protected String s0(int arg) {
            return "s0";
        }

    }

    @Test
    public void testNonStaticSpecialization() {
        TestNonStaticSpecializationNode node = TestNonStaticSpecializationNodeGen.getUncached();
        assertEquals("s0", node.execute(42));
        assertFalse(node.isAdoptable());
    }

    @GenerateUncached
    public abstract static class UnachedArgumentNode extends Node {

        public abstract Object execute(Object arg);

        @Specialization
        protected String s0(int arg) {
            return "s0";
        }

    }

    @GenerateUncached
    @ExpectError("Failed to generate code for @GenerateUncached: The node must not declare any instance variables. Found instance variable ErrorNode1.field. Remove instance variable to resolve this.")
    abstract static class ErrorNode1 extends Node {

        Object field;

        abstract Object execute(Object arg);

        @Specialization
        static int f0(int v) {
            return v;
        }

    }

    @GenerateUncached
    @ExpectError("Failed to generate code for @GenerateUncached: The node must not declare any instance variables. Found instance variable ErrorNode1.field. Remove instance variable to resolve this.")
    abstract static class ErrorNode1_Sub extends ErrorNode1 {

        @Specialization
        static double f1(double v) {
            return v;
        }

    }

    @GenerateUncached
    abstract static class ErrorNode2 extends Node {

        abstract Object execute(Object arg);

        @Specialization
        static int f0(int v,
                        @ExpectError("Failed to generate code for @GenerateUncached: The specialization uses @Cached without valid uncached expression. " +
                                        "Error parsing expression 'getUncached()': The method getUncached is undefined for the enclosing scope. " +
                                        "To resolve this specify the uncached or allowUncached attribute in @Cached or exclude the specialization from @GenerateUncached using @Specialization(excludeForUncached=true).") //
                        @Cached("nonTrivialCache(v)") int cachedV) {
            return v;
        }

        int nonTrivialCache(int v) {
            // we cannot know this is trivial
            return v;
        }

    }

    @ExpectError("Failed to generate code for @GenerateUncached: The node must not declare any instance variables. Found instance variable ErrorNode5.guard. Remove instance variable to resolve this.")
    @GenerateUncached
    abstract static class ErrorNode5 extends Node {

        abstract Object execute(Object arg);

        boolean guard;

        @Specialization
        static int f0(int v) {
            return v;
        }

    }

    abstract static class BaseNode extends Node {

        abstract Object execute();

    }

    @ExpectError("Failed to generate code for @GenerateUncached: " +
                    "The node does not declare any execute method with 1 evaluated argument(s). " +
                    "The generated uncached node does not declare an execute method that can be generated by the DSL. " +
                    "Declare a non-final method that starts with 'execute' and takes 1 argument(s) or variable arguments to resolve this.")
    @GenerateUncached
    @NodeChild(type = BaseNode.class)
    abstract static class ErrorNode6 extends Node {

        abstract Object execute();

        @Specialization
        static int f0(int v) {
            return v;
        }

    }

    @GenerateUncached
    abstract static class ErrorNonTrivialNode1 extends Node {

        abstract Object execute(Object arg);

        @Specialization
        static String s1(Object v,
                        @ExpectError("Failed to generate code for @GenerateUncached: The specialization uses @Cached without valid uncached expression. " +
                                        "Error parsing expression 'getUncached()': The method getUncached is undefined for the enclosing scope. " +
                                        "To resolve this specify the uncached or allowUncached attribute in @Cached or exclude the specialization from @GenerateUncached using @Specialization(excludeForUncached=true).") //
                        @Cached("foo(v)") Object cached) {
            return "s1";
        }

        static Object foo(Object o) {
            return o;
        }

    }

    @GenerateUncached
    abstract static class ErrorNonTrivialNode2 extends Node {

        abstract Object execute(Object arg);

        @Specialization
        static String s1(Object v,
                        @ExpectError("The attributes 'allowUncached' and 'uncached' are mutually exclusive. Remove one of the attributes to resolve this.") //
                        @Cached(value = "foo(v)", allowUncached = true, uncached = "foo(v)") Object cached) {
            return "s1";
        }

        static Object foo(Object o) {
            return o;
        }

    }

    abstract static class ErrorBaseNode extends Node {

        @Child private Node errorField;

        abstract Object execute();

    }

    @GenerateUncached
    @ExpectError("Failed to generate code for @GenerateUncached: The node must not declare any instance variables. Found instance variable ErrorBaseNode.errorField. Remove instance variable to resolve this.")
    abstract static class ErrorFieldInParent1Node extends ErrorBaseNode {

        @Specialization
        public Object doDefault() {
            return null;
        }

    }

    @GenerateUncached
    public abstract static class SpecializedExecuteTest1Node extends Node {

        public abstract long execute(Object o);

        public abstract long executeInt(int o);

        public abstract long executeLong(long o);

        public abstract long executeLong(Number o);

        @Specialization
        long s0(int i) {
            return i;
        }

        @Specialization
        long s1(long l) {
            return l;
        }

        @Specialization
        long s2Number(Number l) {
            return l.longValue();
        }

        @Specialization
        long s3(Object l) {
            if (l instanceof Number) {
                return ((Number) l).longValue();
            }
            throw new IllegalArgumentException();
        }
    }

    @GenerateUncached
    public abstract static class SpecializedExecuteTest2Node extends Node {

        public abstract long execute(Object o);

        public abstract long executeInt(int o);

        public abstract long executeLong(long o);

        public abstract long executeLong(Number o);

        @Specialization(guards = "i == 0")
        long s0(int i) {
            return i;
        }

        @Specialization(guards = "l == 0")
        long s1(long l) {
            return l;
        }

        @Specialization
        long s2Number(Number l) {
            return l.longValue();
        }

        @Specialization
        long s3(Object l) {
            if (l instanceof Number) {
                return ((Number) l).longValue();
            }
            throw new IllegalArgumentException();
        }
    }

    @GenerateUncached
    @NodeChild(type = BaseNode.class)
    public abstract static class NodeChildTest0 extends Node {

        public abstract Object execute();

        public abstract Object execute(Object o);

        @Specialization(guards = "i == 0")
        String s0(int i) {
            return "s0";
        }

        @Specialization
        String s1(int l) {
            return "s1";
        }

    }

    @Test
    public void testNodeChild0() {
        try {
            NodeChildTest0NodeGen.getUncached().execute();
            fail();
        } catch (AssertionError e) {
            assertEquals("This execute method cannot be used for uncached node versions as it requires child nodes to be present. " +
                            "Use an execute method that takes all arguments as parameters.", e.getMessage());
        }

        assertEquals("s0", NodeChildTest0NodeGen.getUncached().execute(0));
        assertEquals("s1", NodeChildTest0NodeGen.getUncached().execute(42));
    }

    @GenerateUncached
    @NodeChild(value = "child", type = BaseNode.class)
    public abstract static class NodeChildTest1 extends Node {

        public abstract Object execute();

        public abstract Object execute(VirtualFrame frame);

        public abstract Object execute(Object... o);

        public abstract BaseNode getChild();

        @Specialization(guards = "i == 0")
        String s0(int i) {
            return "s0";
        }

        @Specialization
        String s1(int l) {
            return "s1";
        }

    }

    @Test
    public void testNodeChild1() {
        try {
            NodeChildTest1NodeGen.getUncached().execute();
            fail();
        } catch (AssertionError e) {
            assertEquals("This execute method cannot be used for uncached node versions as it requires child nodes to be present. " +
                            "Use an execute method that takes all arguments as parameters.", e.getMessage());
        }
        try {
            NodeChildTest1NodeGen.getUncached().getChild();
            fail();
        } catch (AssertionError e) {
            assertEquals("This getter method cannot be used for uncached node versions as it requires child nodes to be present.",
                            e.getMessage());
        }
        try {
            NodeChildTest1NodeGen.getUncached().execute((VirtualFrame) null);
            fail();
        } catch (AssertionError e) {
            assertEquals("This execute method cannot be used for uncached node versions as it requires child nodes to be present. " +
                            "Use an execute method that takes all arguments as parameters.", e.getMessage());
        }
        try {
            NodeChildTest1NodeGen.getUncached().execute(new Object[0]);
            fail();
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        assertEquals("s0", NodeChildTest1NodeGen.getUncached().execute(0));
        assertEquals("s1", NodeChildTest1NodeGen.getUncached().execute(42));
        assertEquals("s0", NodeChildTest1NodeGen.getUncached().execute(0, 0));
        assertEquals("s1", NodeChildTest1NodeGen.getUncached().execute(42, 42));
    }

    @GenerateUncached
    @GenerateInline(false)
    abstract static class CachedLegacyTestNode extends Node {

        abstract String execute();

        @Specialization
        static String s0(
                        // actually a warning
                        @ExpectError("Failed to generate code for @GenerateUncached: The specialization uses @Cached without valid uncached expression%") //
                        @Cached("create()") Assumption assumption) {
            return "s0";
        }

        @Specialization(replaces = "s0")
        static String s1() throws ArithmeticException {
            return "s1";
        }
    }

    @Test
    public void testCachedLegacyTestNode() {
        // still executable as the error is only a warning
        assertEquals("s1", CachedLegacyTestNodeGen.getUncached().execute());
    }

    abstract static class WarningUnusedNode extends Node {

        abstract String execute();

        @ExpectError("The attribute excludeForUncached has no effect as the node is not configured for uncached generation.%")
        @Specialization(excludeForUncached = true)
        static String s0() {
            return "s0";
        }

    }

    @GenerateUncached
    @ExpectError("All specializations were excluded for uncached. At least one specialization must remain included. " +
                    "Set the excludeForUncached attribute to false for at least one specialization to resolve this problem.")
    abstract static class ErrorAllExcludedNode extends Node {

        abstract String execute();

        @Specialization(excludeForUncached = true)
        static String s0() {
            return "s0";
        }

    }

}
