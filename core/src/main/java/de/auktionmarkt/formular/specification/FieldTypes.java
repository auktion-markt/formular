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

package de.auktionmarkt.formular.specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldTypes {

    public static final String TEXT = "text";
    public static final String PASSWORD = "password";
    public static final String NUMBER = "number";
    public static final String EMAIL = "email";
    public static final String TELEPHONE = "tel";
    public static final String URL = "url";
    public static final String DATE = "date";
    public static final String DATETIME = "datetime-local";
    public static final String TIME = "time";
    public static final String CHECKBOX = "checkbox";
    public static final String RADIO = "radio";
    public static final String SELECT = "select";
}
