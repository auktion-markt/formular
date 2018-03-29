package de.auktionmarkt.formular.specification.mapper;

import de.auktionmarkt.formular.specification.FormSpecification;

public interface FormMapper {

    FormSpecification mapFormSpecification(Class<?> dataClass, String method, String actionScheme);
}
