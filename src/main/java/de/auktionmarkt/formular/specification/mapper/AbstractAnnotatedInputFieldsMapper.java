package de.auktionmarkt.formular.specification.mapper;

import de.auktionmarkt.formular.internal.TypeDescriptors;
import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.annotation.FormElement;
import de.auktionmarkt.formular.specification.annotation.FormInput;
import de.auktionmarkt.formular.specification.annotation.SkipJsr380;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ClassUtils;

import javax.validation.constraints.NotNull;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Base class for annotated fields. Contains utilities for integrating JSR-380 annotations. Use
 * {@link AbstractAnnotatedInputFieldsMapper#prepareMapFieldSpecification(PropertyDescriptor, TypeDescriptor)} on
 * subclasses.
 */
public abstract class AbstractAnnotatedInputFieldsMapper implements FieldsMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAnnotatedInputFieldsMapper.class);
    private static final boolean JSR380_AVAILABLE;

    static {
        JSR380_AVAILABLE = ClassUtils.isPresent("javax.validation.Validator", null);
        if (JSR380_AVAILABLE)
            LOGGER.info("JSR-380 found and partially supported for form mapping");
    }

    private final BeanFactory beanFactory;
    protected final ConversionService conversionService;

    @Autowired
    public AbstractAnnotatedInputFieldsMapper(BeanFactory beanFactory, ConversionService conversionService) {
        this.beanFactory = beanFactory;
        this.conversionService = conversionService;
    }

    @Override
    public boolean supportsField(Class<?> model, PropertyDescriptor propertyDescriptor, TypeDescriptor typeDescriptor) {
        boolean qualifies = typeDescriptor.hasAnnotation(FormElement.class) &&
                typeDescriptor.hasAnnotation(FormInput.class);
        if (qualifies && !conversionService.canConvert(TypeDescriptors.STRING_TYPE, typeDescriptor)) {
            LOGGER.trace("Field {} on {} qualifies to {} but conversion for java.lang.String -> {} is not known",
                    propertyDescriptor.getName(), typeDescriptor.getSource(), getClass().getName(), typeDescriptor);
            return false;
        }
        return true;
    }

    /**
     * Creates a new {@link FieldSpecification.Builder} and applies basic mapping on it. Please note that this may set
     * a {@link FieldSpecification.Builder#parameters} which should'nt be overwritten. Consider using
     * {@link FieldSpecification.Builder#parameter(String, Object)} instead.
     *
     * @param propertyDescriptor The {@link PropertyDescriptor} for the field
     * @param typeDescriptor The {@link TypeDescriptor} for the field
     * @return A pre-built {@link FieldSpecification.Builder}
     */
    protected FieldSpecification.Builder prepareMapFieldSpecification(PropertyDescriptor propertyDescriptor,
                                                                      TypeDescriptor typeDescriptor) {
        FieldSpecification.Builder builder = FieldSpecification.builder();
        Map<String, Object> parameters = new HashMap<>();
        FormElement formElement = typeDescriptor.getAnnotation(FormElement.class);
        FormInput formInput = typeDescriptor.getAnnotation(FormInput.class);
        addStandardProperties(parameters, formInput, typeDescriptor);
        String type = formInput.type();
        if (type.isEmpty())
            type = null;
        Supplier<String> labelSupplier = getLabelSupplier(formInput, propertyDescriptor);
        return builder
                .propertyDescriptor(propertyDescriptor)
                .typeDescriptor(typeDescriptor)
                .labelSupplier(labelSupplier)
                .path(propertyDescriptor.getName())
                .type(type)
                .parameters(parameters)
                .order(formElement.order());
    }

    @SuppressWarnings("unchecked")
    private Supplier<String> getLabelSupplier(FormInput formInput, PropertyDescriptor propertyDescriptor) {
        String label = formInput.label();
        if (label.isEmpty()) {
            String labelSupplierBean = formInput.labelSupplierBean();
            if (labelSupplierBean.isEmpty())
                return propertyDescriptor::getDisplayName;
            else
                return beanFactory.getBean(labelSupplierBean, Supplier.class);
        } else {
            return () -> label;
        }
    }

    /**
     * Checks if JSR-380 ({@code javax.validation}) annotations should be handled for the given {@link TypeDescriptor}.
     *
     * @param typeDescriptor The {@link TypeDescriptor}
     * @return {@code true} if JSR-380 annotations should be handled for the {@code typeDescriptor}, otherwise
     *         {@code false}
     */
    protected static boolean handleJsr380(TypeDescriptor typeDescriptor) {
        return JSR380_AVAILABLE && !typeDescriptor.hasAnnotation(SkipJsr380.class);
    }

    private static void addStandardProperties(Map<String, Object> parameters, FormInput formInput, TypeDescriptor typeDescriptor) {
        Class<?> type = typeDescriptor.getType();
        if ((type.isPrimitive() && type != boolean.class) ||
                formInput.required() ||
                (handleJsr380(typeDescriptor) && typeDescriptor.hasAnnotation(NotNull.class))) {
            parameters.put("required", true);
        }
    }
}
