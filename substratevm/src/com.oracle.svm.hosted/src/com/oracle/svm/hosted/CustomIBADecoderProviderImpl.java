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

import com.oracle.graal.pointsto.BigBang;
import com.oracle.graal.pointsto.meta.HostedProviders;
import com.oracle.graal.pointsto.phases.InlineBeforeAnalysisGraphDecoder;
import com.oracle.graal.pointsto.phases.InlineBeforeAnalysisPolicy;
import com.oracle.svm.core.SubstrateOptions;
import com.oracle.svm.core.feature.AutomaticallyRegisteredFeature;
import com.oracle.svm.core.feature.InternalFeature;
import com.oracle.svm.hosted.src.com.oracle.svm.hosted.phases.CustomInlineBeforeAnalysisGraphDecoderImpl;
import jdk.graal.compiler.nodes.StructuredGraph;
import org.graalvm.nativeimage.ImageSingletons;

import jdk.graal.compiler.util.json.JsonParser;


import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CustomIBADecoderProviderImpl implements IBADecoderProvider {
    @Override
    public InlineBeforeAnalysisGraphDecoder createDecoder(BigBang bb, InlineBeforeAnalysisPolicy policy, StructuredGraph graph, HostedProviders providers) {
        return new CustomInlineBeforeAnalysisGraphDecoderImpl(bb, policy, graph, providers, parseTargetPaths());
    }

    private List<List<String>> parseTargetPaths() {

        try {
            JsonParser parser = new JsonParser(new FileReader(SubstrateOptions.CustomForcedInlining.getValue()));
            List<List<String>> paths = (List<List<String>>) parser.parse();
//        System.out.println(" --------------  JSON parsed ");
//        for(List<String> path : paths) {
//            for(String method: path) {
//                System.out.print(method+", ");
//            }
//            System.out.println();
//        }
            return paths;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

@AutomaticallyRegisteredFeature
final class CustomIBADecoderFeature implements InternalFeature {

    @Override
    public boolean isInConfiguration(IsInConfigurationAccess access) {
        // TODO should probably check that the provided path is valid first.
        return !SubstrateOptions.CustomForcedInlining.getValue().isEmpty();
    }

    @Override
    public void afterRegistration(AfterRegistrationAccess access) {
        ImageSingletons.add(IBADecoderProvider.class, new CustomIBADecoderProviderImpl());
    }
}
