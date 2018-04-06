package de.auktionmarkt.formular.state.applicator;

import de.auktionmarkt.formular.state.FormState;

public interface FormStateApplicator {

    default void applyTo(FormState formState, Object target) {
        applyTo(formState, target, false);
    }

    void applyTo(FormState formState, Object target, boolean ignoreInvalid);
}
