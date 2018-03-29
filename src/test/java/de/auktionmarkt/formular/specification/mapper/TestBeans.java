package de.auktionmarkt.formular.specification.mapper;

import de.auktionmarkt.formular.specification.mapper.support.DefaultFormMapper;
import de.auktionmarkt.formular.support.converter.DateToStringConverter;
import de.auktionmarkt.formular.support.converter.StringToDateConverter;
import de.auktionmarkt.formular.specification.mapper.support.DefaultFieldsMapperService;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;

@Configuration
class TestBeans {

    @Bean
    public ConversionService conversionService() {
        GenericConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new DateToStringConverter());
        conversionService.addConverter(new StringToDateConverter());
        return conversionService;
    }

    @Bean
    public FieldsMapperService fieldsMapperService(ConversionService conversionService) {
        DefaultFieldsMapperService bean = new DefaultFieldsMapperService(Mockito.mock(BeanFactory.class),
                conversionService);
        bean.sort();
        return bean;
    }

    @Bean
    public FormMapper formMapper(FieldsMapperService fieldsMapperService) {
        return new DefaultFormMapper(fieldsMapperService);
    }
}
