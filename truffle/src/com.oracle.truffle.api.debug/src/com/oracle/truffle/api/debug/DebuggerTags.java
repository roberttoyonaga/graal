/*
 * Copyright (c) 2016, 2018, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.api.debug;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.Node;

/**
 * Set of debugger-specific tags. Language should {@link ProvidedTags provide} an implementation of
 * these tags in order to support specific debugging features.
 *
 * @since 0.14
 */
public final class DebuggerTags {

    private DebuggerTags() {
        // No instances
    }

    /**
     * Marks program locations where debugger should always halt like if on a breakpoint.
     * <p>
     * {@link TruffleLanguage}s that support concept similar to JavaScript's <code>debugger</code>
     * statement (program locations where execution should always halt) should make sure that
     * appropriate {@link Node}s are tagged with the {@link AlwaysHalt} tag.
     *
     * {@snippet file="com/oracle/truffle/api/debug/DebuggerTags.java"
     * region="com.oracle.truffle.api.debug.DebuggerTagsSnippets#debuggerNode"}
     *
     * All created {@link DebuggerSession debugger sessions} will suspend on these locations
     * unconditionally.
     *
     * @since 0.14
     */
    public final class AlwaysHalt extends Tag {
        private AlwaysHalt() {
            /* No instances */
        }
    }

}

class DebuggerTagsSnippets {

    @SuppressWarnings("unused")
    public static Node debuggerNode() {
        // @formatter:off // @replace regex='.*' replacement=''
        abstract
        // @start region="com.oracle.truffle.api.debug.DebuggerTagsSnippets#debuggerNode"
        class DebuggerNode extends Node implements InstrumentableNode {

            public boolean hasTag(Class<? extends Tag> tag) {
                return tag == DebuggerTags.AlwaysHalt.class;
            }
        }
        // @end region="com.oracle.truffle.api.debug.DebuggerTagsSnippets#debuggerNode"
        // @formatter:on // @replace regex='.*' replacement=''
        return null;
    }
}
