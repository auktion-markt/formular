package de.auktionmarkt.formular.specification.mapper;

import de.auktionmarkt.formular.FormularException;

public class FormMappingException extends FormularException {

    public FormMappingException() {
    }

    public FormMappingException(String message) {
        super(message);
    }

    public FormMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormMappingException(Throwable cause) {
        super(cause);
    }
}
