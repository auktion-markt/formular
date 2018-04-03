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

package de.auktionmarkt.formular.integration;

import de.auktionmarkt.formular.specification.FieldTypes;
import de.auktionmarkt.formular.specification.annotation.FormElement;
import de.auktionmarkt.formular.specification.annotation.FormEmbedded;
import de.auktionmarkt.formular.specification.annotation.FormInput;
import lombok.Data;

import javax.validation.constraints.Max;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

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

    @FormElement
    @FormInput(label = "<<label for aString>>")
    private String aString;

    @FormElement
    @FormInput(label = "<<label for checkbox>>")
    private Boolean checkbox;

    @FormElement
    @FormInput(label = "<<label for singleSelected>>")
    private TimeUnit singleSelected;

    @FormElement
    @FormEmbedded(title = "<<title for embeddedForm>>")
    private EmbeddedForm embeddedForm;

    @Data
    public static class EmbeddedForm {

        @FormElement
        @FormInput(label = "<<label for anotherString>>")
        private String anotherString;
    }
}
