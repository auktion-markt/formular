package de.auktionmarkt.formular.specification.support;

import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;

public interface RepositoryParameterFiller {

    Object[] getParameters(Class<?> entityType, Class<?> model, PropertyDescriptor propertyDescriptor,
                           TypeDescriptor typeDescriptor);
}
