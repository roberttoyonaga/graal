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

package com.oracle.svm.hosted;

import java.util.ArrayList;
import java.util.List;

/** This class is mainly necessary for gathering diagnostics. */
public class TargetPath {
    List<TargetMethod> path;

    public TargetPath(List<String> path) {
        this.path = new ArrayList<>();
        for (String methodId : path) {
            this.path.add(new TargetMethod(methodId));
        }
    }

    // This should only be executed after the inlining before analysis step is complete.
    public boolean isFound() {
        return path.getLast().isFound();
    }

    public TargetMethod getDivergencePoint() {
        for (TargetMethod targetMethod : path) {
            if (!targetMethod.isFound()) {
                return targetMethod;
            }
        }
        return null;
    }

    public TargetMethod get(int i) {
        return path.get(i);
    }

    public TargetMethod getFirst() {
        return path.getFirst();
    }

    public int size() {
        return path.size();
    }

    public String getMethodId(int index) {
        return path.get(index).getMethodId();
    }
    public void setFound(int index) {
        path.get(index).setFound();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ ");

        for (TargetMethod targetMethod : path) {
            sb.append(targetMethod).append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static class TargetMethod {
        String methodId;
        boolean found;

        TargetMethod(String methodId) {
            found = false;
            this.methodId = methodId;
        }

        public boolean isFound() {
            return found;
        }

        public void setFound() {
            found = true;
        }

        public String getMethodId() {
            return methodId;
        }

        @Override
        public String toString() {
            return methodId;
        }
    }
}
