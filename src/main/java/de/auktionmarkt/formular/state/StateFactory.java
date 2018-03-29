package de.auktionmarkt.formular.state;

import de.auktionmarkt.formular.specification.FormSpecification;
import org.springframework.validation.BindingResult;

import java.util.Map;

public interface StateFactory {

    FormState createEmptyState(FormSpecification formSpecification);

    FormState createStateFromModel(FormSpecification formSpecification, Object model);

    FormState createStateFromBindingResult(FormSpecification formSpecification, BindingResult bindingResult);
}
