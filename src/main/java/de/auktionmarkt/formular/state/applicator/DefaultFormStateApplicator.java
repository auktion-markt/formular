package de.auktionmarkt.formular.state.applicator;

import de.auktionmarkt.formular.state.FieldState;
import de.auktionmarkt.formular.state.FormState;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Map;

public class DefaultFormStateApplicator implements FormStateApplicator {

    private final ConversionService conversionService;

    public DefaultFormStateApplicator(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public void applyTo(FormState formState, Object target, boolean ignoreInvalid) {
        BeanWrapper wrapper = new BeanWrapperImpl(target);
        for (Map.Entry<String, FieldState> fieldState : formState.getFieldStates().entrySet()) {
            if (!fieldState.getValue().isValueSet())
                continue;
            String path = fieldState.getKey();
            TypeDescriptor propertyTypeDescriptor = wrapper.getPropertyTypeDescriptor(path);
            if (propertyTypeDescriptor == null && !ignoreInvalid)
                throw new IllegalArgumentException("Missing property: " + path);
            Object source = fieldState.getValue().getValue();
            TypeDescriptor sourceType = TypeDescriptor.forObject(source);
            Object value = conversionService.convert(source, sourceType, propertyTypeDescriptor);
            wrapper.setPropertyValue(path, value);
        }
    }
}
