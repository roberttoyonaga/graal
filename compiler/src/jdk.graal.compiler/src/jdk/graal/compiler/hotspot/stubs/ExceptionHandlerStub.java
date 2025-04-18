/*
 * Copyright (c) 2012, 2021, Oracle and/or its affiliates. All rights reserved.
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
package jdk.graal.compiler.hotspot.stubs;

import static jdk.graal.compiler.core.common.spi.ForeignCallDescriptor.CallSideEffect.NO_SIDE_EFFECT;
import static jdk.graal.compiler.hotspot.meta.HotSpotForeignCallDescriptor.Transition.SAFEPOINT;
import static jdk.graal.compiler.hotspot.stubs.StubUtil.cAssertionsEnabled;
import static jdk.graal.compiler.hotspot.stubs.StubUtil.decipher;
import static jdk.graal.compiler.hotspot.stubs.StubUtil.fatal;
import static jdk.graal.compiler.hotspot.stubs.StubUtil.newDescriptor;
import static jdk.graal.compiler.hotspot.stubs.StubUtil.printf;
import static org.graalvm.word.LocationIdentity.any;

import jdk.graal.compiler.api.replacements.Fold;
import jdk.graal.compiler.api.replacements.Fold.InjectedParameter;
import jdk.graal.compiler.api.replacements.Snippet;
import jdk.graal.compiler.api.replacements.Snippet.ConstantParameter;
import jdk.graal.compiler.core.common.spi.ForeignCallDescriptor;
import jdk.graal.compiler.debug.Assertions;
import jdk.graal.compiler.graph.Node.ConstantNodeParameter;
import jdk.graal.compiler.graph.Node.NodeIntrinsic;
import jdk.graal.compiler.hotspot.GraalHotSpotVMConfig;
import jdk.graal.compiler.hotspot.HotSpotBackend;
import jdk.graal.compiler.hotspot.HotSpotForeignCallLinkage;
import jdk.graal.compiler.hotspot.HotSpotMarkId;
import jdk.graal.compiler.hotspot.meta.HotSpotForeignCallDescriptor;
import jdk.graal.compiler.hotspot.meta.HotSpotProviders;
import jdk.graal.compiler.hotspot.nodes.JumpToExceptionHandlerNode;
import jdk.graal.compiler.hotspot.nodes.PatchReturnAddressNode;
import jdk.graal.compiler.hotspot.nodes.StubForeignCallNode;
import jdk.graal.compiler.hotspot.replacements.HotSpotReplacementsUtil;
import jdk.graal.compiler.options.OptionValues;
import jdk.graal.compiler.word.Word;
import jdk.vm.ci.code.Register;

/**
 * Stub called by the {@linkplain HotSpotMarkId#EXCEPTION_HANDLER_ENTRY exception handler entry
 * point} in a compiled method. This entry point is used when returning to a method to handle an
 * exception thrown by a callee. It is not used for routing implicit exceptions. Therefore, it does
 * not need to save any registers as HotSpot uses a caller save convention.
 * <p>
 * The descriptor for a call to this stub is {@link HotSpotBackend#EXCEPTION_HANDLER}.
 */
public class ExceptionHandlerStub extends SnippetStub {

    public ExceptionHandlerStub(OptionValues options, HotSpotProviders providers, HotSpotForeignCallLinkage linkage) {
        super("exceptionHandler", options, providers, linkage);
    }

    @Override
    protected Object getConstantParameterValue(int index, String name) {
        if (index == 2) {
            return providers.getRegisters().getThreadRegister();
        }
        assert index == 3 : index;
        return StubOptions.TraceExceptionHandlerStub.getValue(options);
    }

    @Snippet
    private static void exceptionHandler(Object exception, Word exceptionPc, @ConstantParameter Register threadRegister, @ConstantParameter boolean logging) {
        Word thread = HotSpotReplacementsUtil.registerAsWord(threadRegister);
        checkNoExceptionInThread(thread, assertionsEnabled(GraalHotSpotVMConfig.INJECTED_VMCONFIG));
        checkExceptionNotNull(assertionsEnabled(GraalHotSpotVMConfig.INJECTED_VMCONFIG), exception);
        HotSpotReplacementsUtil.writeExceptionOop(thread, exception);
        HotSpotReplacementsUtil.writeExceptionPc(thread, exceptionPc);
        if (logging) {
            printf("handling exception %p (", Word.objectToTrackedPointer(exception).rawValue());
            decipher(Word.objectToTrackedPointer(exception).rawValue());
            printf(") at %p (", exceptionPc.rawValue());
            decipher(exceptionPc.rawValue());
            printf(")\n");
        }

        // patch throwing pc into return address so that deoptimization finds the right debug info
        PatchReturnAddressNode.patchReturnAddress(exceptionPc);

        Word handlerPc = exceptionHandlerForPc(EXCEPTION_HANDLER_FOR_PC, thread);

        if (logging) {
            printf("handler for exception %p at %p is at %p (", Word.objectToTrackedPointer(exception).rawValue(), exceptionPc.rawValue(), handlerPc.rawValue());
            decipher(handlerPc.rawValue());
            printf(")\n");
        }

        // patch the return address so that this stub returns to the exception handler
        JumpToExceptionHandlerNode.jumpToExceptionHandler(handlerPc);
    }

    static void checkNoExceptionInThread(Word thread, boolean enabled) {
        if (enabled) {
            Object currentException = HotSpotReplacementsUtil.readExceptionOop(thread);
            if (currentException != null) {
                fatal("exception object in thread must be null, not %p", Word.objectToTrackedPointer(currentException).rawValue());
            }
            if (cAssertionsEnabled(GraalHotSpotVMConfig.INJECTED_VMCONFIG)) {
                // This thread-local is only cleared in DEBUG builds of the VM
                // (see OptoRuntime::generate_exception_blob)
                Word currentExceptionPc = HotSpotReplacementsUtil.readExceptionPc(thread);
                if (currentExceptionPc.notEqual(Word.zero())) {
                    fatal("exception PC in thread must be zero, not %p", currentExceptionPc.rawValue());
                }
            }
        }
    }

    static void checkExceptionNotNull(boolean enabled, Object exception) {
        if (enabled && exception == null) {
            fatal("exception must not be null");
        }
    }

    /**
     * Determines if either Java assertions are enabled for Graal or if this is a HotSpot build
     * where the ASSERT mechanism is enabled.
     */
    @Fold
    @SuppressWarnings("all")
    static boolean assertionsEnabled(@InjectedParameter GraalHotSpotVMConfig config) {
        return Assertions.assertionsEnabled() || cAssertionsEnabled(config);
    }

    public static final HotSpotForeignCallDescriptor EXCEPTION_HANDLER_FOR_PC = newDescriptor(SAFEPOINT, NO_SIDE_EFFECT, any(), ExceptionHandlerStub.class, "exceptionHandlerForPc", Word.class,
                    Word.class);

    @NodeIntrinsic(value = StubForeignCallNode.class)
    public static native Word exceptionHandlerForPc(@ConstantNodeParameter ForeignCallDescriptor exceptionHandlerForPc, Word thread);
}
