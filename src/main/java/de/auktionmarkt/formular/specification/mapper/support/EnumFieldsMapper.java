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
import de.auktionmarkt.formular.specification.FieldTypes;
import de.auktionmarkt.formular.specification.annotation.FormInput;
import de.auktionmarkt.formular.specification.mapper.AbstractAnnotatedInputFieldsMapper;
import de.auktionmarkt.formular.specification.mapper.FormMapper;
import de.auktionmarkt.formular.specification.mapper.FormMappingException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Maps selection fields of type
 */
public class EnumFieldsMapper extends AbstractAnnotatedInputFieldsMapper {

    public EnumFieldsMapper(BeanFactory beanFactory, ConversionService conversionService) {
        super(beanFactory, conversionService);
    }

    @Override
    public boolean supportsField(Class<?> model, PropertyDescriptor propertyDescriptor, TypeDescriptor typeDescriptor) {
        if (!super.supportsField(model, propertyDescriptor, typeDescriptor))
            return false;
        Class<?> elementType = FieldMapperUtils.unpackType(typeDescriptor);
        return elementType != null && elementType.isEnum();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<FieldSpecification> mapFieldSpecification(FormMapper callingFormMapper, Class<?> model,
                                                                PropertyDescriptor propertyDescriptor,
                                                                TypeDescriptor typeDescriptor) {
        Class<?> elementClass = FieldMapperUtils.unpackType(typeDescriptor);
        if (elementClass == null)
            throw new FormMappingException("Cannot extract element type");
        else if (!elementClass.isEnum())
            throw new FormMappingException("Not an enum");
        Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) elementClass;
        boolean ordered = enumClass.isAssignableFrom(Ordered.class);
        Supplier<Map<String, String>> valueSupplier = () -> {
            Stream<? extends Enum<?>> stream = Arrays.stream(enumClass.getEnumConstants());
            if (ordered)
                stream = stream.sorted(Comparator.comparingInt(e -> ((Ordered) e).getOrder()));
            return stream.collect(Collectors.toMap(Enum::name, v -> conversionService.convert(v, String.class),
                    (k1, k2) -> {throw new UnsupportedOperationException("Duplicate key");}, LinkedHashMap::new));
        };
        String type = typeDescriptor.getAnnotation(FormInput.class).type();
        if (type.isEmpty())
            type = typeDescriptor.isCollection() ? FieldTypes.CHECKBOX : FieldTypes.SELECT;
        return Collections.singleton(prepareMapFieldSpecification(propertyDescriptor, typeDescriptor)
                .valuesSupplier(valueSupplier)
                .type(type)
                .build());
    }
}
