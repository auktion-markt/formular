package de.auktionmarkt.formular.specification.mapper;

import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;

public interface FieldsMapperService {

    FieldsMapper getFieldsMapper(Class<?> model, PropertyDescriptor propertyDescriptor, TypeDescriptor typeDescriptor);
}
