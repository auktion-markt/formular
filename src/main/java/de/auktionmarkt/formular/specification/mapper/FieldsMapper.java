package de.auktionmarkt.formular.specification.mapper;

import de.auktionmarkt.formular.specification.FieldSpecification;
import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;
import java.util.Collection;

public interface FieldsMapper {

    boolean supportsField(Class<?> model, PropertyDescriptor propertyDescriptor, TypeDescriptor typeDescriptor);

    /**
     * Create form fields for the given property. Each property may result in multiple fields, allows embedded
     * forms or complex structures (e.g. a number-type value and a select-type unit).
     *
     * @param model
     * @param propertyDescriptor
     * @param typeDescriptor
     * @return
     */
    Collection<FieldSpecification> mapFieldSpecification(FormMapper callingFormMapper, Class<?> model,
                                                         PropertyDescriptor propertyDescriptor,
                                                         TypeDescriptor typeDescriptor);
}
