/*
 * Copyright (c) 2023, 2023, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2023, 2023, Red Hat Inc. All rights reserved.
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

package com.oracle.svm.core.nmt;

import com.oracle.svm.core.Uninterruptible;
import com.oracle.svm.core.jdk.UninterruptibleUtils.AtomicLong;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.word.UnsignedWord;

class VirtualMemoryInfo { // TODO track peak
    private AtomicLong reservedSize;
    private AtomicLong committedSize;

    @Platforms(Platform.HOSTED_ONLY.class)
    VirtualMemoryInfo() {
        reservedSize = new AtomicLong(0);
        committedSize = new AtomicLong(0);
    }

    @Uninterruptible(reason = "Called from uninterruptible code.", mayBeInlined = true)
    void recordReserved(UnsignedWord size) {
        reservedSize.addAndGet(size.rawValue());
    }

    @Uninterruptible(reason = "Called from uninterruptible code.", mayBeInlined = true)
    void recordCommitted(UnsignedWord size) { // *** hotspot doesnt adjust reserved when mem is
        // committed. The same block is counted as both
        // reserved and committed.
        long lastCommitted = committedSize.addAndGet(size.rawValue());
// com.oracle.svm.core.util.VMError.guarantee(lastCommitted<=reservedSize.get());// TODO this is not
// atomic enough. remove later
    }

    @Uninterruptible(reason = "Called from uninterruptible code.", mayBeInlined = true)
    void recordUncommit(UnsignedWord size) {
        long lastSize = committedSize.addAndGet(-size.rawValue());
        com.oracle.svm.core.util.VMError.guarantee(lastSize >= 0);
    }

    @Uninterruptible(reason = "Called from uninterruptible code.", mayBeInlined = true)
    void recordFree(UnsignedWord size) {
        long lastSize = reservedSize.addAndGet(-size.rawValue());
        com.oracle.svm.core.util.VMError.guarantee(lastSize >= 0);
// com.oracle.svm.core.util.VMError.guarantee(lastSize>=committedSize.get());// TODO this is not
// atomic enough. remove later
    }

    long getReservedSize() {
        return reservedSize.get();
    }

    long getCommittedSize() {
        return committedSize.get();
    }
}
