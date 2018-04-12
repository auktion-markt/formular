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
import de.auktionmarkt.formular.specification.annotation.FormEmbedded;
import de.auktionmarkt.formular.specification.mapper.FieldsMapper;
import de.auktionmarkt.formular.specification.mapper.FormMapper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;

public class NestedFieldsMapper implements FieldsMapper {

    private final BeanFactory beanFactory;

    public NestedFieldsMapper(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public boolean supportsField(Class<?> model, PropertyDescriptor propertyDescriptor, TypeDescriptor typeDescriptor) {
        return typeDescriptor.hasAnnotation(FormElement.class) && typeDescriptor.hasAnnotation(FormEmbedded.class);
    }

    @Override
    public Collection<FieldSpecification> mapFieldSpecification(FormMapper callingFormMapper, Class<?> model,
                                                                PropertyDescriptor propertyDescriptor,
                                                                TypeDescriptor typeDescriptor) {
        FormElement formElement = typeDescriptor.getAnnotation(FormElement.class);
        FormEmbedded formEmbedded = typeDescriptor.getAnnotation(FormEmbedded.class);
        Supplier<String> titleSupplier = FieldMapperUtils.getStringSupplier(beanFactory, propertyDescriptor,
                formEmbedded.titleSupplierBean(), formEmbedded.title());
        List<FieldSpecification> mappedFields = callingFormMapper.mapFields(propertyDescriptor.getPropertyType());
        mappedFields.sort(Comparator.comparingInt(FieldSpecification::getOrder));
        List<FieldSpecification> result = new ArrayList<>(mappedFields.size());
        ListIterator<FieldSpecification> iterator = mappedFields.listIterator();
        while (iterator.hasNext()) {
            boolean first = !iterator.hasPrevious();
            FieldSpecification.Builder builder = iterator.next().toBuilder();
            if (first) {
                builder = builder
                        .parameter("titleSupplier", titleSupplier)
                        .parameter("groupStart", true);
            }
            builder = builder
                    .path(propertyDescriptor.getName() + '.' + builder.path())
                    .order(formElement.order())
                    .parameter("parentProperty", propertyDescriptor)
                    .parameter("parentType", typeDescriptor)
                    .parameter("parent", propertyDescriptor.getName());
            if (!iterator.hasNext())
                builder = builder.parameter("groupEnd", true);
            result.add(builder.build());
        }
        return result;
    }
}
