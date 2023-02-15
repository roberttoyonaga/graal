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
package com.oracle.svm.core.jfr;

import org.graalvm.word.SignedWord;
import org.graalvm.word.WordFactory;
public class JfrMetadata {
    private volatile long currentMetadataId;
    private volatile byte[] metadataDescriptor;
    private volatile boolean isDirty;
    private SignedWord metadataPosition;
    public JfrMetadata(byte[] bytes) {
        metadataDescriptor = bytes;
        currentMetadataId = 0;
        isDirty = false;
        metadataPosition = WordFactory.signed(-1);
    }

    public void setDescriptor(byte[] bytes) {
        metadataDescriptor = bytes;
        currentMetadataId++;
        isDirty = true;
    }

    public byte[] getDescriptorAndClearDirtyFlag() {
        isDirty = false;
        return metadataDescriptor;
    }
    public boolean isDirty() {
        return isDirty;
    }
    public void setDirty() {
        isDirty = true;
    }

    public void setMetadataPosition(SignedWord metadataPosition) {
        this.metadataPosition = metadataPosition;
    }

    public SignedWord getMetadataPosition() {
        return metadataPosition;
    }

    public long getCurrentMetadataId() {
        return currentMetadataId;
    }
}