/*
 *    Copyright 2018 Auktion & Markt AG
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.auktionmarkt.formular.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Shared commonly used {@link TypeDescriptor}s.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TypeDescriptors {

    public static final TypeDescriptor STRING_TYPE = TypeDescriptor.valueOf(String.class);
    public static final TypeDescriptor STRING_LIST = TypeDescriptor.collection(List.class, STRING_TYPE);
    public static final TypeDescriptor BOOLEAN_TYPE = TypeDescriptor.valueOf(Boolean.class);
    public static final TypeDescriptor BOOLEAN_PRIMITIVE = TypeDescriptor.valueOf(boolean.class);
}
