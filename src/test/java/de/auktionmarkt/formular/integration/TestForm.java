package de.auktionmarkt.formular.integration;

import de.auktionmarkt.formular.specification.FieldTypes;
import de.auktionmarkt.formular.specification.annotation.FormElement;
import de.auktionmarkt.formular.specification.annotation.FormInput;
import lombok.Data;

import javax.validation.constraints.Max;
import java.time.LocalDateTime;

@Data
class TestForm {

    @FormElement
    @FormInput(label = "<<label for required int>>")
    @Max(value = 42, message = "<<test integer is too big>>")
    private int requiredInt;

    @FormElement
    @FormInput(label = "<<label for optional int>>")
    private Integer optionalInt;

    @FormElement(order = 10)
    @FormInput
    private LocalDateTime localDateTime;

    @FormElement(order = -10)
    @FormInput(type = FieldTypes.PASSWORD, required = true, label = "Enter a password")
    private String password;
}
