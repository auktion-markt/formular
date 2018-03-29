package de.auktionmarkt.formular.specification.mapper.support;

import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.FieldTypes;
import de.auktionmarkt.formular.specification.mapper.AbstractAnnotatedInputFieldsMapper;
import de.auktionmarkt.formular.specification.mapper.FormMapper;
import de.auktionmarkt.formular.specification.mapper.FormMappingException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class DateFieldsMapper extends AbstractAnnotatedInputFieldsMapper {

    public DateFieldsMapper(BeanFactory beanFactory, ConversionService conversionService) {
        super(beanFactory, conversionService);
    }

    @Override
    public boolean supportsField(Class<?> model, PropertyDescriptor propertyDescriptor, TypeDescriptor typeDescriptor) {
        if (!super.supportsField(model, propertyDescriptor, typeDescriptor))
            return false;
        Class<?> type = typeDescriptor.getType();
        return Date.class.isAssignableFrom(type) || LocalDateTime.class.isAssignableFrom(type) ||
                LocalTime.class.isAssignableFrom(type) || LocalDate.class.isAssignableFrom(type);
    }

    @Override
    public Collection<FieldSpecification> mapFieldSpecification(FormMapper callingFormMapper, Class<?> model,
                                                                PropertyDescriptor propertyDescriptor,
                                                                TypeDescriptor typeDescriptor) {
        FieldSpecification.Builder builder = prepareMapFieldSpecification(propertyDescriptor, typeDescriptor);
        String type;
        Class<?> aClass = typeDescriptor.getType();
        if (Date.class.isAssignableFrom(aClass) || LocalDateTime.class.isAssignableFrom(aClass))
            type = FieldTypes.DATETIME;
        else if (LocalTime.class.isAssignableFrom(aClass))
            type = FieldTypes.TIME;
        else if (LocalDate.class.isAssignableFrom(aClass))
            type = FieldTypes.DATE;
        else
            throw new FormMappingException("Unknown type: " + aClass);
        return Collections.singleton(FieldMapperUtils.setTypeIfAbsent(builder, type).build());
    }
}
