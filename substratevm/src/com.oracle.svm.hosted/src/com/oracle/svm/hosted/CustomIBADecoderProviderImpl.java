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
import com.oracle.svm.util.LogUtils;
import jdk.graal.compiler.nodes.StructuredGraph;
import org.graalvm.nativeimage.ImageSingletons;

import jdk.graal.compiler.util.json.JsonParser;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Provides a decoder that allows for forced inlining of specific target call paths. */
public class CustomIBADecoderProviderImpl implements IBADecoderProvider {
    // Cached to avoid parsing JSON repeatedly
    List<List<String>> targetPaths;

    /** {@linkplain CustomIBADecoderProviderImpl#createDecoder } is called for each method found to be reachable. */
    @Override
    public InlineBeforeAnalysisGraphDecoder createDecoder(BigBang bb, InlineBeforeAnalysisPolicy policy, StructuredGraph graph, HostedProviders providers) {
        return new CustomInlineBeforeAnalysisGraphDecoderImpl(bb, policy, graph, providers, getTargetPaths());
    }

    // TODO move this javadoc to a help doc
    /**
     * Target forced inline paths should be formatted as json arrays. The order is caller -> callee.
     * Paths can be specified in any order.
     * Incorrect/invalid paths will not cause errors.
     * Each method in a path must have the format "[fully qualified classname][method name](parameter1type...)".
     * Ex.  Lio/vertx/core/http/impl/headers/HeadersMultiMap;add(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)
     *
     * See the example below:
     *
     * <pre>{@code
     * [
     *   ["Ljava/lang/String;charAt(I)", "Ljava/lang/StringLatin1;charAt([BI)"],
     *   ["Lio/netty/util/AsciiString;hashCode(Ljava/lang/CharSequence;)", "Lio/netty/util/internal/PlatformDependent;hashCodeAscii(Ljava/lang/CharSequence;)"],
     * ]
     * }</pre>
     *
     * */
    private List<List<String>> getTargetPaths() {
        if (targetPaths != null) {
            return targetPaths;
        }

        File configFile = new File(SubstrateOptions.CustomForcedInlining.getValue());
        if (configFile.exists()){
            try {
                JsonParser parser = new JsonParser(new FileReader(configFile));
                targetPaths = (List<List<String>>) parser.parse();
                // Quick sanity checks
                assert targetPaths != null && targetPaths.size() > 0 && targetPaths.getFirst().size() > 1;
            } catch (IOException e) {
                LogUtils.warning("Custom inlining configuration file could not be read. Proceeding without target paths.");
                targetPaths = new ArrayList<>();
            }
        } else {
            LogUtils.warning("Custom inlining configuration file does not exist. Proceeding without target paths.");
            targetPaths = new ArrayList<>();
        }
        return targetPaths;
    }
}

@AutomaticallyRegisteredFeature
final class CustomIBADecoderFeature implements InternalFeature {

    @Override
    public boolean isInConfiguration(IsInConfigurationAccess access) {
        return !SubstrateOptions.CustomForcedInlining.getValue().isEmpty();
    }

    @Override
    public void afterRegistration(AfterRegistrationAccess access) {
        ImageSingletons.add(IBADecoderProvider.class, new CustomIBADecoderProviderImpl());
    }
}
