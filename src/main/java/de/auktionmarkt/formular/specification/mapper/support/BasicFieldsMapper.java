package de.auktionmarkt.formular.specification.mapper.support;

import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.FieldTypes;
import de.auktionmarkt.formular.specification.mapper.AbstractAnnotatedInputFieldsMapper;
import de.auktionmarkt.formular.specification.mapper.FormMapper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles basic types (boolean, byte, short, int, long, float, double, the wrapper classes of all the previous and
 * {@link String}).
 */
public class BasicFieldsMapper extends AbstractAnnotatedInputFieldsMapper {

    private static final Map<Class<?>, Configurer> CONFIGURER;

    static {
        Map<Class<?>, Configurer> configurer = new HashMap<>();
        configurer.put(boolean.class, (builder, model, propertyDescriptor, typeDescriptor) ->
                FieldMapperUtils.setTypeIfAbsent(builder, FieldTypes.CHECKBOX));
        configurer.put(byte.class, (builder, model, propertyDescriptor, typeDescriptor) ->
                configureNumber(builder, typeDescriptor, Byte.MIN_VALUE, Byte.MAX_VALUE));
        configurer.put(Byte.class, configurer.get(byte.class));
        configurer.put(short.class, (builder, model, propertyDescriptor, typeDescriptor) ->
                configureNumber(builder, typeDescriptor, Short.MIN_VALUE, Short.MAX_VALUE));
        configurer.put(Short.class, configurer.get(short.class));
        configurer.put(int.class, (builder, model, propertyDescriptor, typeDescriptor) ->
                configureNumber(builder, typeDescriptor, Integer.MIN_VALUE, Integer.MAX_VALUE));
        configurer.put(Integer.class, configurer.get(int.class));
        configurer.put(long.class, (builder, model, propertyDescriptor, typeDescriptor) ->
                configureNumber(builder, typeDescriptor, Long.MIN_VALUE, Long.MAX_VALUE));
        configurer.put(Long.class, configurer.get(long.class));
        configurer.put(float.class, (builder, model, propertyDescriptor, typeDescriptor) ->
                configureDecimal(builder, typeDescriptor, Float.MIN_VALUE, Float.MAX_VALUE));
        configurer.put(Float.class, configurer.get(float.class));
        configurer.put(double.class, (builder, model, propertyDescriptor, typeDescriptor) ->
                configureDecimal(builder, typeDescriptor, Double.MIN_VALUE, Double.MAX_VALUE));
        configurer.put(Double.class, configurer.get(double.class));
        configurer.put(String.class, (builder, model, propertyDescriptor, typeDescriptor) ->
                FieldMapperUtils.setTypeIfAbsent(builder, FieldTypes.TEXT));
        CONFIGURER = Collections.unmodifiableMap(configurer);
    }

    public BasicFieldsMapper(BeanFactory beanFactory, ConversionService conversionService) {
        super(beanFactory, conversionService);
    }

    @Override
    public boolean supportsField(Class<?> model, PropertyDescriptor propertyDescriptor, TypeDescriptor typeDescriptor) {
        return super.supportsField(model, propertyDescriptor, typeDescriptor) &&
                CONFIGURER.containsKey(typeDescriptor.getType());
    }

    @Override
    public Collection<FieldSpecification> mapFieldSpecification(FormMapper callingFormMapper, Class<?> model,
                                                                PropertyDescriptor propertyDescriptor,
                                                                TypeDescriptor typeDescriptor) {
        FieldSpecification.Builder builder = prepareMapFieldSpecification(propertyDescriptor, typeDescriptor);
        Class<?> type = typeDescriptor.getType();
        Configurer configurer = CONFIGURER.get(type);
        return Collections.singleton(configurer.configure(builder, model, propertyDescriptor, typeDescriptor)
                .build());
    }

    private static FieldSpecification.Builder configureNumber(FieldSpecification.Builder builder,
                                                              TypeDescriptor typeDescriptor, long valueMin,
                                                              long valueMax) {
        if (handleJsr380(typeDescriptor)) {
            Min minAnnotation = typeDescriptor.getAnnotation(Min.class);
            if (minAnnotation != null)
                valueMin = Math.max(minAnnotation.value(), valueMin);
            Max maxAnnotation = typeDescriptor.getAnnotation(Max.class);
            if (maxAnnotation != null)
                valueMax = Math.min(maxAnnotation.value(), valueMax);
        }
        return setNumberTypeIfAbsent(builder)
                .parameter("minValue", valueMin)
                .parameter("maxValue", valueMax);
    }

    private static FieldSpecification.Builder configureDecimal(FieldSpecification.Builder builder,
                                                               TypeDescriptor typeDescriptor, double valueMin,
                                                               double valueMax) {
        if (handleJsr380(typeDescriptor)) {
            DecimalMin minAnnotation = typeDescriptor.getAnnotation(DecimalMin.class);
            if (minAnnotation != null)
                valueMin = Math.max(Double.parseDouble(minAnnotation.value()), valueMin);
            DecimalMax maxAnnotation = typeDescriptor.getAnnotation(DecimalMax.class);
            if (maxAnnotation != null)
                valueMax = Math.min(Double.parseDouble(maxAnnotation.value()), valueMax);
        }
        return setNumberTypeIfAbsent(builder)
                .parameter("minValue", valueMin)
                .parameter("maxValue", valueMax);
    }

    private static FieldSpecification.Builder setNumberTypeIfAbsent(FieldSpecification.Builder builder) {
        return FieldMapperUtils.setTypeIfAbsent(builder, FieldTypes.NUMBER);
    }

    @FunctionalInterface
    private interface Configurer {

        FieldSpecification.Builder configure(FieldSpecification.Builder builder, Class<?> model,
                                             PropertyDescriptor propertyDescriptor, TypeDescriptor typeDescriptor);
    }
}
