package de.auktionmarkt.formular.specification.mapper.support;

import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.FormSpecification;
import de.auktionmarkt.formular.specification.annotation.FormElement;
import de.auktionmarkt.formular.specification.mapper.FieldsMapper;
import de.auktionmarkt.formular.specification.mapper.FieldsMapperService;
import de.auktionmarkt.formular.specification.mapper.FormMapper;
import de.auktionmarkt.formular.specification.mapper.FormMappingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultFormMapper implements FormMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFormMapper.class);

    private final FieldsMapperService fieldsMapperService;

    @Override
    public FormSpecification mapFormSpecification(Class<?> dataClass, String method, String actionScheme) {
        LOGGER.debug("Building form specification for class {}...", dataClass);
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
                            fieldsMapper.mapFieldSpecification(dataClass, propertyDescriptor, typeDescriptor);
                } catch (Throwable throwable) {
                    throw new FormMappingException("Field mapper " + fieldsMapper.getClass().getName() +
                            " has thrown an exception", throwable);
                }
                if (fieldSpecifications == null) {
                    throw new FormMappingException("Field mapper " + fieldsMapper.getClass().getName() +
                            " returned null as the mapping result for " + dataClass.getName() + "#" +
                            propertyDescriptor.getName());
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
        return FormSpecification.create(dataClass, method, actionScheme, fields);
    }
}
