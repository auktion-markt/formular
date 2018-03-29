package de.auktionmarkt.formular.state;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the state of a single form field. The state is either {@code null} when no value is set, a {@link String}
 * or a {@link Collection} of content type {@link String}.
 */
@Data
public class FieldState {

    /**
     * Instance of {@code FieldState} with no value and no errors set.
     */
    public static final FieldState EMPTY = new FieldState("", Collections.emptyList());

    private final Object value;
    private final List<String> errors;

    /**
     * Constructs a new {@code FieldState}. The constructor will validate the given {@code value} (include walking over
     * a {@link Collection} and check each element).
     *
     * @param value The form value of the field (must be either {@code null}, a {@link String} or an instance of
     *              {@link Collection}.
     * @param errors A {@link Collection} of errors
     * @throws NullPointerException Is thrown when {@code errors} is {@code null}
     * @throws IllegalArgumentException Is thrown when {@code value} is not {@code null} and not an {@link String} or
     *                                  an instance of {@link Collection}
     */
    public FieldState(Object value, Collection<String> errors) {
        validateValue(value);
        this.value = value;
        Objects.requireNonNull(errors, "errors must not be null");
        this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
    }

    private static void validateValue(Object value) {
        if (value != null && !(value instanceof String) && !(value instanceof Collection))
            throw new IllegalArgumentException("value must be either null, a string or a collection but is " + value);
        if (value instanceof Collection) {
            for (Object o : (Collection) value) {
                if (o == null)
                    throw new NullPointerException("Collection value contains null values");
                if (!(o instanceof String))
                    throw new IllegalArgumentException("Collection value must only consist of string values");
            }
        }
    }
}
