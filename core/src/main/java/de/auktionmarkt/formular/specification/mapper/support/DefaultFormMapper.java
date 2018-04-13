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

package de.auktionmarkt.formular.specification.mapper.support;

import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.annotation.FormElement;
import de.auktionmarkt.formular.specification.annotation.FormSubmitField;
import de.auktionmarkt.formular.specification.mapper.FieldsMapper;
import de.auktionmarkt.formular.specification.mapper.FieldsMapperService;
import de.auktionmarkt.formular.specification.mapper.FormMapper;
import de.auktionmarkt.formular.specification.mapper.FormMappingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultFormMapper implements FormMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFormMapper.class);

    private final BeanFactory beanFactory;
    private final FieldsMapperService fieldsMapperService;

    @Override
    public List<FieldSpecification> mapFields(Class<?> dataClass) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(dataClass);
        PropertyDescriptor[] propertyDescriptors = wrapper.getPropertyDescriptors();
        List<FieldSpecification> fields;
        if (propertyDescriptors.length > 0) {
            fields = new ArrayList<>(propertyDescriptors.length);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                TypeDescriptor typeDescriptor = wrapper.getPropertyTypeDescriptor(propertyDescriptor.getName());
                if (!typeDescriptor.hasAnnotation(FormElement.class))
                    continue;
                FieldsMapper fieldsMapper = fieldsMapperService.getFieldsMapper(dataClass,
                        propertyDescriptor, typeDescriptor);
                LOGGER.trace("Map specification for field {} with type {} using {}", propertyDescriptor.getName(),
                        typeDescriptor, fieldsMapper.getClass().getName());
                Collection<FieldSpecification> fieldSpecifications;
                try {
                    fieldSpecifications =
                            fieldsMapper.mapFieldSpecification(this, dataClass,
                                    propertyDescriptor, typeDescriptor);
                } catch (Throwable throwable) {
                    throw new FormMappingException("Field mapper " + fieldsMapper.getClass().getName() +
                            " has thrown an exception on property " + propertyDescriptor.getName(), throwable);
                }
                if (fieldSpecifications == null) {
                    throw new FormMappingException("Field mapper " + fieldsMapper.getClass().getName() +
                            " returned null as the mapping result for property" + propertyDescriptor.getName());
                }
                if (!fieldSpecifications.isEmpty()) {
                    if (LOGGER.isTraceEnabled()) {
                        String fieldSpecificationStr = fieldSpecifications.stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(", "));
                        LOGGER.trace("Mapped specifications for field {}: {}",
                                propertyDescriptor.getName(), fieldSpecificationStr);
                    }
                    fields.addAll(fieldSpecifications);
                }
            }
        } else {
            LOGGER.trace("No accessible fields on class {}", dataClass);
            fields = Collections.emptyList();
        }

        FormSubmitField submitField = dataClass.getAnnotation(FormSubmitField.class);
        if (submitField != null) {
            Supplier<String> labelSupplier = FieldMapperUtils.getStringSupplier(beanFactory, () -> "Submit",
                    submitField.labelSupplierBean(), submitField.label());
            fields.add(new FieldSpecification(null, null, labelSupplier, "",
                    "button", Collections.emptyMap(), Integer.MAX_VALUE, null));
        }

        return fields;
    }
}
