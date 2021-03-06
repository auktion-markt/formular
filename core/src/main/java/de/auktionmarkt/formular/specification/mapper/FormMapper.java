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
