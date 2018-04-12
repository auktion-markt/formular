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

package de.auktionmarkt.formular.specification.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a form input element.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface FormInput {

    /**
     * The type of the form input.
     *
     * @see de.auktionmarkt.formular.specification.FieldTypes
     * @return A type
     */
    String type() default "";

    /**
     * Returns the label of the form input.
     *
     * @return The label of the form input
     */
    String label() default "";

    /**
     * Returns the name of a bean implementing {@link java.util.function.Supplier} and returns a {@link String} as the
     * title of the field.
     *
     * @return The name of the title supplier bean
     */
    String labelSupplierBean() default "";

    /**
     * Returns {@code true} if this field is required, otherwise {@code false}.
     *
     * @return {@code true} if this field is required, otherwise {@code false}
     */
    boolean required() default false;

    /**
     * Returns the type of a {@link java.util.Collection}s elements. May be specified when the collection is not
     * parametrized.
     *
     * @return The type of a {@link java.util.Collection}s elements
     */
    Class<?> elementType() default void.class;
}
