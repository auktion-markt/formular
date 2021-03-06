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

import de.auktionmarkt.formular.internal.TypeDescriptors;
import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.FormSpecification;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Creates field states which is a unsorted {@link Map} consisting of the {@link FieldSpecification#path} as the
 * key (same as the key in {@link FormSpecification#fields}) and an instance of {@link FieldState} as the value.
 */
public class DefaultStateFactory implements StateFactory {

    private static final FieldState EMPTY_STATE = new FieldState("", true, Collections.emptySet());

    private final MessageSource messageSource;
    private final ConversionService conversionService;

    public DefaultStateFactory(MessageSource messageSource, ConversionService conversionService) {
        this.messageSource = messageSource;
        this.conversionService = conversionService;
    }

    /**
     * Creates a field state mapping for each field in {@link FormSpecification#fields} with a empty value.
     *
     * @param formSpecification The {@link FormSpecification} from which the fields will be obtained
     * @return An unordered {@link Map} consisting of field names as keys and empty value (not {@code null})
     */
    @Override
    public FormState createEmptyState(FormSpecification formSpecification) {
        Objects.requireNonNull(formSpecification, "formSpecification must not be null");
        Map<String, FieldState> fieldStates = Collections.unmodifiableMap(
                formSpecification.getFields().values().stream()
                        .collect(Collectors.toMap(FieldSpecification::getPath, s ->
                                new FieldState(createDefaultValue(s), false, Collections.emptySet()))));
        return new FormState(false, fieldStates);
    }

    @Override
    public FormState createStateFromModel(FormSpecification formSpecification, Object model) {
        Objects.requireNonNull(formSpecification, "formSpecification must not be null");
        Objects.requireNonNull(model, "model must not be null");
        Map<String, FieldSpecification> fieldSpecificationMap = formSpecification.getFields();
        Map<String, FieldState> fieldStates = new HashMap<>(fieldSpecificationMap.size());
        BeanWrapperImpl modelWrapper = new BeanWrapperImpl(model);
        // Prevent throwing org.springframework.beans.NullValueInNestedPathException on unset property
        modelWrapper.setAutoGrowNestedPaths(true);
        for (FieldSpecification fieldSpecification : fieldSpecificationMap.values()) {
            String path = fieldSpecification.getPath();
            boolean hasProperty = modelWrapper.isReadableProperty(path);
            FieldState fieldState;
            if (hasProperty) {
                Object value = modelWrapper.getPropertyValue(fieldSpecification.getPath());
                value = convertWhenNecessary(value);
                fieldState = new FieldState(value, true, Collections.emptyList());
            } else {
                fieldState = new FieldState(createDefaultValue(fieldSpecification), false, Collections.emptyList());
            }
            fieldStates.put(fieldSpecification.getPath(), fieldState);
        }
        fieldStates = Collections.unmodifiableMap(fieldStates);
        return new FormState(false, fieldStates);
    }

    @Override
    public FormState createStateFromBindingResult(FormSpecification formSpecification, BindingResult bindingResult) {
        Objects.requireNonNull(formSpecification, "formSpecification must not be null");
        Objects.requireNonNull(bindingResult, "bindingResult must not be null");
        Map<String, FieldSpecification> fieldSpecificationMap = formSpecification.getFields();
        Map<String, FieldState> fieldStates = new HashMap<>(fieldSpecificationMap.size());
        for (FieldSpecification fieldSpecification : fieldSpecificationMap.values()) {
            String path = fieldSpecification.getPath();
            FieldState fieldState;
            if (!path.isEmpty()) {
                boolean isSet = bindingResult.getRawFieldValue(path) != null;
                if (isSet) {
                    List<String> fieldErrors = bindingResult.getFieldErrors().stream()
                            .map(error -> messageSource.getMessage(error, LocaleContextHolder.getLocale()))
                            .collect(Collectors.toList());
                    Object value = bindingResult.getFieldValue(fieldSpecification.getPath());
                    value = convertWhenNecessary(value);
                    fieldState = new FieldState(value, true, fieldErrors);
                } else {
                    fieldState = new FieldState(createDefaultValue(fieldSpecification), false, Collections.emptyList());
                }
            } else {
                fieldState = EMPTY_STATE;
            }
            fieldStates.put(fieldSpecification.getPath(), fieldState);
        }
        fieldStates = Collections.unmodifiableMap(fieldStates);
        return new FormState(true, fieldStates);
    }

    private Object convertWhenNecessary(Object value) {
        if (value != null) {
            TypeDescriptor formValueType = TypeDescriptor.forObject(value);
            TypeDescriptor targetType = decideTypeDescriptor(formValueType);
            if (!TypeDescriptors.VOID.equals(targetType))
                value = conversionService.convert(value, formValueType, targetType);
        }
        return value;
    }

    private static TypeDescriptor decideTypeDescriptor(TypeDescriptor original) {
        if (original == null)
            return TypeDescriptors.VOID;
        else if (original.isCollection() || original.isArray())
            return TypeDescriptors.STRING_LIST;
        else if (TypeDescriptors.BOOLEAN_TYPE.equals(original))
            return TypeDescriptors.BOOLEAN_TYPE;
        else if (TypeDescriptors.BOOLEAN_PRIMITIVE.equals(original))
            return TypeDescriptors.BOOLEAN_PRIMITIVE;
        return TypeDescriptors.STRING_TYPE;
    }

    private static Object createDefaultValue(FieldSpecification fieldSpecification) {
        TypeDescriptor type = decideTypeDescriptor(fieldSpecification.getTypeDescriptor());
        if (type.isCollection() || type.isArray())
            return Collections.emptySet();
        else if (TypeDescriptors.BOOLEAN_TYPE.equals(type) || TypeDescriptors.BOOLEAN_PRIMITIVE.equals(type))
            return false;
        return "";
    }
}
