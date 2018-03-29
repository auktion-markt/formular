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
