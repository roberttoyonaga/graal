/*
 * Copyright (c) 2019, 2024, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.svm.configure.config;

import java.util.List;

import com.oracle.svm.configure.ConfigurationTypeDescriptor;
import com.oracle.svm.configure.ReflectionConfigurationParserDelegate;
import com.oracle.svm.configure.UnresolvedConfigurationCondition;
import com.oracle.svm.configure.config.ConfigurationMemberInfo.ConfigurationMemberAccessibility;
import com.oracle.svm.configure.config.ConfigurationMemberInfo.ConfigurationMemberDeclaration;
import com.oracle.svm.util.TypeResult;

public class ParserConfigurationAdapter implements ReflectionConfigurationParserDelegate<UnresolvedConfigurationCondition, ConfigurationType> {

    private final TypeConfiguration configuration;

    public ParserConfigurationAdapter(TypeConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public TypeResult<ConfigurationType> resolveType(UnresolvedConfigurationCondition condition, ConfigurationTypeDescriptor typeDescriptor, boolean allowPrimitives, boolean jniAccessible) {
        ConfigurationType type = configuration.get(condition, typeDescriptor);
        /*
         * The type is not immediately set with all elements included. These are added afterwards
         * when parsing the correspondind fields to check for overriding values
         */
        ConfigurationType result = type != null ? type : new ConfigurationType(condition, typeDescriptor, false);
        return TypeResult.forType(typeDescriptor.toString(), result);
    }

    @Override
    public TypeResult<List<ConfigurationType>> resolveTypes(UnresolvedConfigurationCondition condition, ConfigurationTypeDescriptor typeDescriptor, boolean allowPrimitives, boolean jniAccessible) {
        TypeResult<ConfigurationType> result = resolveType(condition, typeDescriptor, allowPrimitives, jniAccessible);
        return TypeResult.toList(result);
    }

    @Override
    public void registerType(UnresolvedConfigurationCondition condition, ConfigurationType type) {
        checkArguments(condition.equals(type.getCondition()), "condition is already a part of the type");
        configuration.add(type);
    }

    @Override
    public void registerField(UnresolvedConfigurationCondition condition, ConfigurationType type, String fieldName, boolean finalButWritable, boolean jniAccessible) {
        checkArguments(condition.equals(type.getCondition()), "condition is already a part of the type");
        if (jniAccessible) {
            type.setJniAccessible();
        }
        type.addField(fieldName, ConfigurationMemberDeclaration.PRESENT, finalButWritable);
    }

    @Override
    public boolean registerAllMethodsWithName(UnresolvedConfigurationCondition condition, boolean queriedOnly, boolean jniAccessible, ConfigurationType type, String methodName) {
        checkArguments(condition.equals(type.getCondition()), "condition is already a part of the type");
        if (jniAccessible) {
            type.setJniAccessible();
        }
        type.addMethodsWithName(methodName, ConfigurationMemberDeclaration.PRESENT, queriedOnly ? ConfigurationMemberAccessibility.QUERIED : ConfigurationMemberAccessibility.ACCESSED);
        return true;
    }

    @Override
    public boolean registerAllConstructors(UnresolvedConfigurationCondition condition, boolean queriedOnly, boolean jniAccessible, ConfigurationType type) {
        checkArguments(condition.equals(type.getCondition()), "condition is already a part of the type");
        if (jniAccessible) {
            type.setJniAccessible();
        }
        type.addMethodsWithName(ConfigurationMethod.CONSTRUCTOR_NAME, ConfigurationMemberDeclaration.PRESENT,
                        queriedOnly ? ConfigurationMemberAccessibility.QUERIED : ConfigurationMemberAccessibility.ACCESSED);
        return true;
    }

    @Override
    public void registerUnsafeAllocated(UnresolvedConfigurationCondition condition, ConfigurationType type) {
        checkArguments(condition.equals(type.getCondition()), "condition is already a part of the type");
        type.setUnsafeAllocated();
    }

    @Override
    public void registerMethod(UnresolvedConfigurationCondition condition, boolean queriedOnly, ConfigurationType type, String methodName, List<ConfigurationType> methodParameterTypes,
                    boolean jniAccessible) {
        checkArguments(condition.equals(type.getCondition()), "condition is already a part of the type");
        if (jniAccessible) {
            type.setJniAccessible();
        }
        type.addMethod(methodName, ConfigurationMethod.toInternalParamsSignature(methodParameterTypes), ConfigurationMemberDeclaration.PRESENT,
                        queriedOnly ? ConfigurationMemberAccessibility.QUERIED : ConfigurationMemberAccessibility.ACCESSED);
    }

    @Override
    public void registerConstructor(UnresolvedConfigurationCondition condition, boolean queriedOnly, ConfigurationType type, List<ConfigurationType> methodParameterTypes, boolean jniAccessible) {
        checkArguments(condition.equals(type.getCondition()), "condition is already a part of the type");
        if (jniAccessible) {
            type.setJniAccessible();
        }
        type.addMethod(ConfigurationMethod.CONSTRUCTOR_NAME, ConfigurationMethod.toInternalParamsSignature(methodParameterTypes), ConfigurationMemberDeclaration.PRESENT,
                        queriedOnly ? ConfigurationMemberAccessibility.QUERIED : ConfigurationMemberAccessibility.ACCESSED);
    }

    @Override
    public void registerPublicClasses(UnresolvedConfigurationCondition condition, ConfigurationType type) {
        checkArguments(condition.isAlwaysTrue() || condition.equals(type.getCondition()), "condition is already a part of the type");
        type.setAllPublicClasses();
    }

    @Override
    public void registerDeclaredClasses(UnresolvedConfigurationCondition condition, ConfigurationType type) {
        checkArguments(condition.isAlwaysTrue() || condition.equals(type.getCondition()), "condition is already a part of the type");
        type.setAllDeclaredClasses();
    }

    @Override
    public void registerRecordComponents(UnresolvedConfigurationCondition condition, ConfigurationType type) {
        checkArguments(condition.isAlwaysTrue() || condition.equals(type.getCondition()), "condition is already a part of the type");
        type.setAllRecordComponents();
    }

    @Override
    public void registerPermittedSubclasses(UnresolvedConfigurationCondition condition, ConfigurationType type) {
        checkArguments(condition.isAlwaysTrue() || condition.equals(type.getCondition()), "condition is already a part of the type");
        type.setAllPermittedSubclasses();
    }

    @Override
    public void registerNestMembers(UnresolvedConfigurationCondition condition, ConfigurationType type) {
        checkArguments(condition.isAlwaysTrue() || condition.equals(type.getCondition()), "condition is already a part of the type");
        type.setAllNestMembers();
    }

    @Override
    public void registerSigners(UnresolvedConfigurationCondition condition, ConfigurationType type) {
        checkArguments(condition.isAlwaysTrue() || condition.equals(type.getCondition()), "condition is already a part of the type");
        type.setAllSigners();
    }

    @Override
    public void registerPublicFields(UnresolvedConfigurationCondition condition, boolean queriedOnly, boolean jniAccessible, ConfigurationType type) {
        checkArguments(condition.isAlwaysTrue() || condition.equals(type.getCondition()), "condition is already a part of the type");
        if (jniAccessible) {
            type.setJniAccessible();
        }
        type.setAllPublicFields(queriedOnly ? ConfigurationMemberAccessibility.QUERIED : ConfigurationMemberAccessibility.ACCESSED);
    }

    @Override
    public void registerDeclaredFields(UnresolvedConfigurationCondition condition, boolean queriedOnly, boolean jniAccessible, ConfigurationType type) {
        checkArguments(condition.isAlwaysTrue() || condition.equals(type.getCondition()), "condition is already a part of the type");
        if (jniAccessible) {
            type.setJniAccessible();
        }
        type.setAllDeclaredFields(queriedOnly ? ConfigurationMemberAccessibility.QUERIED : ConfigurationMemberAccessibility.ACCESSED);
    }

    @Override
    public void registerPublicMethods(UnresolvedConfigurationCondition condition, boolean queriedOnly, boolean jniAccessible, ConfigurationType type) {
        checkArguments(condition.isAlwaysTrue() || condition.equals(type.getCondition()), "condition is already a part of the type");
        if (jniAccessible) {
            type.setJniAccessible();
        }
        type.setAllPublicMethods(queriedOnly ? ConfigurationMemberAccessibility.QUERIED : ConfigurationMemberAccessibility.ACCESSED);
    }

    @Override
    public void registerDeclaredMethods(UnresolvedConfigurationCondition condition, boolean queriedOnly, boolean jniAccessible, ConfigurationType type) {
        checkArguments(condition.isAlwaysTrue() || condition.equals(type.getCondition()), "condition is already a part of the type");
        if (jniAccessible) {
            type.setJniAccessible();
        }
        type.setAllDeclaredMethods(queriedOnly ? ConfigurationMemberAccessibility.QUERIED : ConfigurationMemberAccessibility.ACCESSED);
    }

    @Override
    public void registerPublicConstructors(UnresolvedConfigurationCondition condition, boolean queriedOnly, boolean jniAccessible, ConfigurationType type) {
        checkArguments(condition.isAlwaysTrue() || condition.equals(type.getCondition()), "condition is already a part of the type");
        if (jniAccessible) {
            type.setJniAccessible();
        }
        type.setAllPublicConstructors(queriedOnly ? ConfigurationMemberAccessibility.QUERIED : ConfigurationMemberAccessibility.ACCESSED);
    }

    @Override
    public void registerDeclaredConstructors(UnresolvedConfigurationCondition condition, boolean queriedOnly, boolean jniAccessible, ConfigurationType type) {
        checkArguments(condition.isAlwaysTrue() || condition.equals(type.getCondition()), "condition is already a part of the type");
        if (jniAccessible) {
            type.setJniAccessible();
        }
        type.setAllDeclaredConstructors(queriedOnly ? ConfigurationMemberAccessibility.QUERIED : ConfigurationMemberAccessibility.ACCESSED);
    }

    @Override
    public void registerAsSerializable(UnresolvedConfigurationCondition condition, ConfigurationType type) {
        checkArguments(condition.isAlwaysTrue() || condition.equals(type.getCondition()), "condition is already a part of the type");
        type.setSerializable();
    }

    @Override
    public void registerAsJniAccessed(UnresolvedConfigurationCondition condition, ConfigurationType type) {
        checkArguments(condition.isAlwaysTrue() || condition.equals(type.getCondition()), "condition is already a part of the type");
        type.setJniAccessible();
    }

    @Override
    public String getTypeName(ConfigurationType type) {
        return type.getTypeDescriptor().toString();
    }

    @Override
    public String getSimpleName(ConfigurationType type) {
        return getTypeName(type);
    }

    private static void checkArguments(boolean condition, String msg) {
        if (!condition) {
            throw new IllegalArgumentException(msg);
        }
    }
}
