package de.auktionmarkt.formular;

public class FormularException extends RuntimeException {

    public FormularException() {
    }

    public FormularException(String message) {
        super(message);
    }

    public FormularException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormularException(Throwable cause) {
        super(cause);
    }
}
