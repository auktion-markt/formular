package de.auktionmarkt.formular.specification.mapper;

import de.auktionmarkt.formular.specification.FieldTypes;
import de.auktionmarkt.formular.specification.annotation.FormElement;
import de.auktionmarkt.formular.specification.annotation.FormInput;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
class TestForm {

    @FormElement
    @FormInput(label = "Required Integer")
    @Max(42)
    private int requiredInt;

    @FormElement
    @FormInput(label = "Optional Integer")
    private Integer optionalInt;

    @FormElement(order = 10)
    @FormInput
    private LocalDateTime localDateTime;

    @FormElement(order = -10)
    @FormInput(type = FieldTypes.PASSWORD, required = true, label = "Enter a password")
    private String password;
}
