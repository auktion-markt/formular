package de.auktionmarkt.formular.specification.mapper.support;

import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.FieldTypes;
import de.auktionmarkt.formular.specification.annotation.FormInput;
import de.auktionmarkt.formular.specification.mapper.AbstractAnnotatedInputFieldsMapper;
import de.auktionmarkt.formular.specification.mapper.FormMappingException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Maps selection fields of type
 */
@Component
public class EnumFieldsMapper extends AbstractAnnotatedInputFieldsMapper {

    public EnumFieldsMapper(BeanFactory beanFactory, ConversionService conversionService) {
        super(beanFactory, conversionService);
    }

    @Override
    public boolean supportsField(Class<?> model, PropertyDescriptor propertyDescriptor, TypeDescriptor typeDescriptor) {
        if (!super.supportsField(model, propertyDescriptor, typeDescriptor))
            return false;
        Class<?> elementType = FieldMapperUtils.unpackType(typeDescriptor);
        return elementType != null && elementType.isEnum();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<FieldSpecification> mapFieldSpecification(Class<?> model, PropertyDescriptor propertyDescriptor,
                                                                TypeDescriptor typeDescriptor) {
        Class<?> elementClass = FieldMapperUtils.unpackType(typeDescriptor);
        if (elementClass == null)
            throw new FormMappingException("Cannot extract element type");
        else if (!elementClass.isEnum())
            throw new FormMappingException("Not an enum");
        Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) elementClass;
        boolean ordered = enumClass.isAssignableFrom(Ordered.class);
        Supplier<Map<String, String>> valueSupplier = () -> {
            Stream<? extends Enum<?>> stream = Arrays.stream(enumClass.getEnumConstants());
            if (ordered)
                stream = stream.sorted(Comparator.comparingInt(e -> ((Ordered) e).getOrder()));
            return stream.collect(Collectors.toMap(Enum::name, v -> conversionService.convert(v, String.class),
                    (k1, k2) -> {throw new UnsupportedOperationException("Duplicate key");}, LinkedHashMap::new));
        };
        String type = typeDescriptor.getAnnotation(FormInput.class).type();
        if (type.isEmpty())
            type = typeDescriptor.isCollection() ? FieldTypes.CHECKBOX : FieldTypes.SELECT;
        return Collections.singleton(prepareMapFieldSpecification(propertyDescriptor, typeDescriptor)
                .valueSupplier(valueSupplier)
                .type(type)
                .build());
    }
}
