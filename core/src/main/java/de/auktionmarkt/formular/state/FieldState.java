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
    public static final FieldState EMPTY = new FieldState("", false, Collections.emptyList());

    private final Object value;
    private final boolean valueSet;
    private final List<String> errors;

    /**
     * Constructs a new {@code FieldState}. The constructor will validate the given {@code value} (include walking over
     * a {@link Collection} and check each element).
     *
     * @param value The form value of the field (must be either {@code null}, a {@link String}, a {@code boolean} or
     *              an instance of {@link Collection}.
     * @param valueSet {@code true} when value was set explicitly
     * @param errors A {@link Collection} of errors
     * @throws NullPointerException Is thrown when {@code errors} is {@code null}
     * @throws IllegalArgumentException Is thrown when {@code value} is not {@code null} and not an {@link String} or
     *                                  an instance of {@link Collection}
     */
    public FieldState(Object value, boolean valueSet, Collection<String> errors) {
        validateValue(value);
        this.value = value;
        this.valueSet = valueSet;
        Objects.requireNonNull(errors, "errors must not be null");
        this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
    }

    // Utility method for use in templates
    @SuppressWarnings("unused")
    public boolean valueContains(String value) {
        if (this.value instanceof Collection<?>)
            return ((Collection) this.value).contains(value);
        else if (this.value instanceof String)
            return value.isEmpty() || value.equals(this.value);
        return false;
    }

    // Utility method for use in templates
    @SuppressWarnings("unused")
    public boolean isChecked() {
        return this.value instanceof Boolean && (Boolean) this.value;
    }

    private static void validateValue(Object value) {
        if (value != null && !(value instanceof String) && !(value instanceof Collection) &&
                !(value instanceof Boolean)) {
            throw new IllegalArgumentException("value must be either null, a string, a boolean or " +
                    "a collection but is " + value);
        }
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
