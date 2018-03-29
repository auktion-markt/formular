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

package de.auktionmarkt.formular.support.converter;

import de.auktionmarkt.formular.specification.annotation.FormElement;
import de.auktionmarkt.formular.specification.annotation.FormInput;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConverterTestUtils {

    public static TypeDescriptor createTypeDescriptor(Type type) {
        return createTypeDescriptor(type, null, "", false, null);
    }

    public static TypeDescriptor createTypeDescriptor(Type type, String label, String annotatedType, boolean required, Class<?> elementType) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(annotatedType, "annotatedType");
        if (!(type instanceof Class))
            throw new IllegalArgumentException("type must be an instance of java.lang.Class");
        if (elementType == null)
            elementType = void.class;
        ResolvableType resolvableType = ResolvableType.forType(type);
        Annotation[] annotations = new Annotation[] {new TestFormInput(annotatedType, label, required, elementType),
                TestFormElement.INSTANCE};
        return new TestTypeDescriptor(resolvableType, (Class<?>) type, annotations);
    }

    private static class TestTypeDescriptor extends TypeDescriptor {

        private TestTypeDescriptor(ResolvableType resolvableType, Class<?> type, Annotation[] annotations) {
            super(resolvableType, type, annotations);
        }
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    @RequiredArgsConstructor
    private static class TestFormInput implements FormInput {

        private final String type;
        private final String label;
        private final boolean required;
        private final Class<?> elementType;

        @Override
        public String type() {
            return type;
        }

        @Override
        public String label() {
            return label;
        }

        @Override
        public String labelSupplierBean() {
            return "";
        }

        @Override
        public boolean required() {
            return required;
        }

        @Override
        public Class<?> elementType() {
            return elementType;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return FormInput.class;
        }
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    @RequiredArgsConstructor
    private static class TestFormElement implements FormElement {

        private static final TestFormElement INSTANCE = new TestFormElement();

        private final int order;

        public TestFormElement() {
            this(0);
        }

        @Override
        public int order() {
            return order;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return FormElement.class;
        }
    }
}
