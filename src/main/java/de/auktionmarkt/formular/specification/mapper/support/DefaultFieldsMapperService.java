package de.auktionmarkt.formular.specification.mapper.support;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.convert.ConversionService;

public class DefaultFieldsMapperService extends GenericFieldsMapperService {

    public DefaultFieldsMapperService(BeanFactory beanFactory, ConversionService conversionService) {
        registerFieldsMappers(new BasicFieldsMapper(beanFactory, conversionService),
                new DateFieldsMapper(beanFactory, conversionService),
                new EnumFieldsMapper(beanFactory, conversionService));
    }
}
