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
import com.oracle.svm.hosted.phases.CustomInlineBeforeAnalysisGraphDecoderImpl;
import com.oracle.svm.util.LogUtils;
import jdk.graal.compiler.nodes.StructuredGraph;
import org.graalvm.nativeimage.ImageSingletons;

import jdk.graal.compiler.util.json.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Provides a decoder that allows for forced inlining of specific target call paths. */
public class CustomIBADecoderProviderImpl implements IBADecoderProvider {
    private static final String REPORT_PREFIX = "custom_inlining_report_";
    private static final String REPORT_EXTENSION = ".txt";
    List<TargetPath> targetPaths;

    /**
     * {@linkplain CustomIBADecoderProviderImpl#createDecoder } is called for each method found to
     * be reachable.
     */
    @Override
    public InlineBeforeAnalysisGraphDecoder createDecoder(BigBang bb, InlineBeforeAnalysisPolicy policy, StructuredGraph graph, HostedProviders providers) {
        return new CustomInlineBeforeAnalysisGraphDecoderImpl(bb, policy, graph, providers, getTargetPaths());
    }

    private List<TargetPath> getTargetPaths() {
        if (targetPaths != null) {
            return targetPaths;
        }
        targetPaths = new ArrayList<>();

        File configFile = new File(SubstrateOptions.CustomForcedInlining.getValue());
        if (configFile.exists()) {
            try {
                JsonParser parser = new JsonParser(new FileReader(configFile));
                List<List<String>> pathList = (List<List<String>>) parser.parse();

                // Quick sanity checks
                assert pathList != null && pathList.size() > 0 && pathList.getFirst().size() > 1;

                for (List<String> path : pathList) {
                    targetPaths.add(new TargetPath(path));
                }
            } catch (IOException e) {
                LogUtils.warning("Custom inlining configuration file could not be read. Proceeding without target paths.");
            }
        } else {
            LogUtils.warning("Custom inlining configuration file does not exist. Proceeding without target paths.");
        }
        return targetPaths;
    }

    public void printDiagnostics() {
        StringBuilder sb = new StringBuilder("\n\n----------------------\n");
        sb.append("Custom Inlining Report\n");
        sb.append("----------------------\n");
        sb.append("Target path configuration file: " + SubstrateOptions.CustomForcedInlining.getValue() + "\n");
        sb.append("The following target paths were not found: \n\n");
        int count = 0;
        for (TargetPath targetPath : targetPaths) {
            if (!targetPath.isFound()) {
                sb.append("----------------------\n");
                sb.append(targetPath).append("\n\n");
                sb.append(">>> Divergence point: ").append(targetPath.getDivergencePoint()).append("\n\n");
                count++;
            }
        }
        sb.append(count).append(" paths not found\n");
        sb.append(targetPaths.size()).append(" total paths\n");
        LogUtils.info(sb.toString());

        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        String formattedDate = myDateObj.format(formatter);

        try {
            Files.writeString(Paths.get(REPORT_PREFIX + formattedDate + REPORT_EXTENSION), sb.toString());
        } catch (IOException e) {
            LogUtils.warning("Could not write custom inlining report.");
        }
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
