package de.auktionmarkt.formular.state;

import lombok.Data;

import java.util.Map;

@Data
public class FormState {

    private final boolean submitted;
    private final Map<String, FieldState> fieldStates;

    public FieldState getFieldState(String path) {
        return getFieldStates().get(path);
    }
}
