package de.auktionmarkt.formular.specification.mapper;

import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.FormSpecification;

import java.util.List;

public interface FormMapper {

    default FormSpecification mapFormSpecification(Class<?> dataClass, String method, String actionScheme) {
        List<FieldSpecification> fields = mapFields(dataClass);
        return FormSpecification.create(dataClass, method, actionScheme, fields);
    }

    List<FieldSpecification> mapFields(Class<?> dataClass);
}
