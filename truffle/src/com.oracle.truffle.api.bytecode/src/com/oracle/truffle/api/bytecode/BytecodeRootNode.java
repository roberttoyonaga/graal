/*
 * Copyright (c) 2022, 2024, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.api.bytecode;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ControlFlowException;
import com.oracle.truffle.api.source.SourceSection;

/**
 * Base interface to be implemented by the bytecode root node of a Bytecode DSL interpreter. The
 * bytecode root node should extend {@link com.oracle.truffle.api.nodes.RootNode} and be annotated
 * with {@link GenerateBytecode @GenerateBytecode}.
 * <p>
 * The current bytecode root node can be bound inside {@link Operation operations} using
 * <code>@Bind</code>. For example, if the bytecode root node class is
 * <code>MyBytecodeRootNode</code>, it can be bound using
 * <code>@Bind MyBytecodeRootNode root</code>.
 * <p>
 * Bytecode root nodes can declare a {@link com.oracle.truffle.api.dsl.TypeSystemReference} that
 * will be inherited by all declared operations (including operation proxies). Operations can also
 * declare their own type system references to override the root type system.
 *
 * @see GenerateBytecode
 * @since 24.2
 */
@Bind.DefaultExpression("$rootNode")
public interface BytecodeRootNode {

    /**
     * Entrypoint to the root node.
     * <p>
     * This method will be generated by the Bytecode DSL. Do not override.
     *
     * @param frame the frame used for execution
     * @return the value returned by the root node
     * @since 24.2
     */
    Object execute(VirtualFrame frame);

    /**
     * Optional hook invoked when a {@link ControlFlowException} is thrown during execution. This
     * hook can do one of four things:
     *
     * <ol>
     * <li>It can return a value. The value will be returned from the root node (this can be used to
     * implement early returns).
     * <li>It can throw the same or a different {@link ControlFlowException}. The thrown exception
     * will be thrown from the root node.
     * <li>It can throw an {@link AbstractTruffleException}. The thrown exception will be forwarded
     * to the guest code for handling.
     * <li>It can throw an internal error, which will be intercepted by
     * {@link #interceptInternalException}.
     * </ol>
     *
     * @param ex the control flow exception
     * @param frame the frame at the point the exception was thrown
     * @param bytecodeNode the bytecode node executing when the exception was thrown
     * @param bytecodeIndex the bytecode index of the instruction that caused the exception
     * @since 24.2
     */
    @SuppressWarnings("unused")
    default Object interceptControlFlowException(ControlFlowException ex, VirtualFrame frame, BytecodeNode bytecodeNode, int bytecodeIndex) throws Throwable {
        throw ex;
    }

    /**
     * Optional hook invoked when an internal exception (i.e., anything other than
     * {@link AbstractTruffleException} or {@link ControlFlowException}) is thrown during execution.
     * This hook can be used to convert such exceptions into guest-language exceptions that can be
     * handled by guest code.
     * <p>
     * For example, if a Java {@link StackOverflowError} is thrown, this hook can be used to return
     * a guest-language equivalent exception that the guest code understands.
     * <p>
     * If the return value is an {@link AbstractTruffleException}, it will be forwarded to the guest
     * code for handling. The exception will also be intercepted by
     * {@link #interceptTruffleException}.
     * <p>
     * If the return value is not an {@link AbstractTruffleException}, it will be rethrown. Thus, if
     * an internal error cannot be converted to a guest exception, it can simply be returned.
     *
     * @param t the internal exception
     * @param frame the frame at the point the exception was thrown
     * @param bytecodeNode the bytecode node executing when the exception was thrown
     * @param bytecodeIndex the bytecode index of the instruction that caused the exception
     * @return an equivalent guest-language exception or an exception to be rethrown
     * @since 24.2
     */
    @SuppressWarnings("unused")
    default Throwable interceptInternalException(Throwable t, VirtualFrame frame, BytecodeNode bytecodeNode, int bytecodeIndex) {
        return t;
    }

    /**
     * Optional hook invoked when an {@link AbstractTruffleException} is thrown during execution.
     * This hook can be used to preprocess the exception or replace it with another exception before
     * it is handled.
     *
     * @param ex the Truffle exception
     * @param frame the frame at the point the exception was thrown
     * @param bytecodeNode the bytecode node executing when the exception was thrown
     * @param bytecodeIndex the bytecode index of the instruction that caused the exception
     * @return the Truffle exception to be handled by guest code
     * @since 24.2
     */
    @SuppressWarnings("unused")
    default AbstractTruffleException interceptTruffleException(AbstractTruffleException ex, VirtualFrame frame, BytecodeNode bytecodeNode, int bytecodeIndex) {
        return ex;
    }

    /**
     * Returns the current bytecode node. Note that the bytecode may change at any point in time.
     * <p>
     * This method will be generated by the Bytecode DSL. Do not override.
     *
     * @return the current bytecode node
     * @since 24.2
     */
    default BytecodeNode getBytecodeNode() {
        return null;
    }

    /**
     * Returns the {@link BytecodeRootNodes} instance associated with this root node.
     * <p>
     * This method will be generated by the Bytecode DSL. Do not override.
     *
     * @since 24.2
     */
    default BytecodeRootNodes<?> getRootNodes() {
        return null;
    }

    /**
     * Returns the {@link BytecodeLocation location} associated with the start of this root node.
     *
     * @since 24.2
     */
    default BytecodeLocation getStartLocation() {
        return new BytecodeLocation(getBytecodeNode(), 0);
    }

    /**
     * Returns the source section for this root node and materializes source information if it was
     * not yet materialized.
     *
     * @see BytecodeNode#ensureSourceInformation()
     * @since 24.2
     */
    default SourceSection ensureSourceSection() {
        return getBytecodeNode().ensureSourceInformation().getSourceSection();
    }

    /**
     * Helper method to dump the root node's bytecode.
     *
     * @return a string representation of the bytecode
     * @since 24.2
     */
    @TruffleBoundary
    default String dump() {
        return getBytecodeNode().dump();
    }

}
