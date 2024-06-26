/*
 * Copyright (c) 2015, 2017, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.svm.core.graal.thread;

import static jdk.graal.compiler.nodeinfo.NodeCycles.CYCLES_2;
import static jdk.graal.compiler.nodeinfo.NodeSize.SIZE_1;

import jdk.graal.compiler.core.common.memory.BarrierType;
import jdk.graal.compiler.core.common.memory.MemoryOrderMode;
import jdk.graal.compiler.core.common.type.StampFactory;
import jdk.graal.compiler.graph.NodeClass;
import jdk.graal.compiler.nodeinfo.NodeInfo;
import jdk.graal.compiler.nodes.AbstractStateSplit;
import jdk.graal.compiler.nodes.ConstantNode;
import jdk.graal.compiler.nodes.ValueNode;
import jdk.graal.compiler.nodes.extended.JavaWriteNode;
import jdk.graal.compiler.nodes.memory.OrderedMemoryAccess;
import jdk.graal.compiler.nodes.memory.SingleMemoryKill;
import jdk.graal.compiler.nodes.memory.address.AddressNode;
import jdk.graal.compiler.nodes.memory.address.OffsetAddressNode;
import jdk.graal.compiler.nodes.spi.Lowerable;
import jdk.graal.compiler.nodes.spi.LoweringTool;
import org.graalvm.word.LocationIdentity;

import com.oracle.svm.core.threadlocal.VMThreadLocalInfo;

@NodeInfo(cycles = CYCLES_2, size = SIZE_1)
public class StoreVMThreadLocalNode extends AbstractStateSplit implements VMThreadLocalAccess, Lowerable, SingleMemoryKill, OrderedMemoryAccess {
    public static final NodeClass<StoreVMThreadLocalNode> TYPE = NodeClass.create(StoreVMThreadLocalNode.class);

    protected final VMThreadLocalInfo threadLocalInfo;
    protected final BarrierType barrierType;
    private final MemoryOrderMode memoryOrder;
    @Input protected ValueNode holder;
    @Input protected ValueNode value;

    public StoreVMThreadLocalNode(VMThreadLocalInfo threadLocalInfo, ValueNode holder, ValueNode value, BarrierType barrierType,
                    MemoryOrderMode memoryOrder) {
        super(TYPE, StampFactory.forVoid());
        this.threadLocalInfo = threadLocalInfo;
        this.barrierType = barrierType;
        this.memoryOrder = memoryOrder;
        this.holder = holder;
        this.value = value;
    }

    public ValueNode getValue() {
        return value;
    }

    @Override
    public LocationIdentity getKilledLocationIdentity() {
        if (ordersMemoryAccesses()) {
            return LocationIdentity.any();
        }
        return threadLocalInfo.locationIdentity;
    }

    @Override
    public MemoryOrderMode getMemoryOrder() {
        return memoryOrder;
    }

    @Override
    public void lower(LoweringTool tool) {
        assert threadLocalInfo.offset >= 0;

        ConstantNode offset = ConstantNode.forLong(threadLocalInfo.offset, graph());
        AddressNode address = graph().unique(new OffsetAddressNode(holder, offset));
        JavaWriteNode write = graph().add(new JavaWriteNode(threadLocalInfo.storageKind, address, threadLocalInfo.locationIdentity, value, barrierType, true, true, memoryOrder));
        write.setStateAfter(stateAfter());
        graph().replaceFixedWithFixed(this, write);
        tool.getLowerer().lower(write, tool);
    }
}
