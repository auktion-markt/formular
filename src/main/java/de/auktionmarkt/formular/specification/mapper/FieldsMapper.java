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

package de.auktionmarkt.formular.specification.mapper;

import de.auktionmarkt.formular.specification.FieldSpecification;
import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;
import java.util.Collection;

public interface FieldsMapper {

    /**
     * Checks if the field is supported by this {@link FieldsMapper}.
     *
     * @param model The model class
     * @param propertyDescriptor The {@link PropertyDescriptor} of the property which might be mapped
     * @param typeDescriptor The {@link TypeDescriptor} of the property which might be mapped
     * @return {@code true} when the mapper supports this field, otherwise {@code false}
     */
    boolean supportsField(Class<?> model, PropertyDescriptor propertyDescriptor, TypeDescriptor typeDescriptor);

    /**
     * Create form fields for the given property. Each property may result in multiple fields, allows embedded
     * forms or complex structures (e.g. a number-type value and a select-type unit).
     *
     * @param callingFormMapper The {@link FormMapper} instance which is calling the method
     * @param model The model class
     * @param propertyDescriptor The {@link PropertyDescriptor} of the property which should be mapped
     * @param typeDescriptor The {@link TypeDescriptor} of the property which should be mapped
     * @return A {@link Collection} of mapped {@link FieldSpecification}s
     */
    Collection<FieldSpecification> mapFieldSpecification(FormMapper callingFormMapper, Class<?> model,
                                                         PropertyDescriptor propertyDescriptor,
                                                         TypeDescriptor typeDescriptor);
}
