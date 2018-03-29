package de.auktionmarkt.formular.integration;

import de.auktionmarkt.formular.specification.FormSpecification;
import de.auktionmarkt.formular.specification.mapper.FormMapper;
import de.auktionmarkt.formular.support.converter.DateToStringConverter;
import de.auktionmarkt.formular.support.converter.StringToDateConverter;
import de.auktionmarkt.formular.specification.mapper.FieldsMapperService;
import de.auktionmarkt.formular.specification.mapper.support.DefaultFieldsMapperService;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
class TestBeans {

    @Bean
    public ConversionService conversionService() {
        ConfigurableConversionService bean = new DefaultConversionService();
        bean.addConverter(new DateToStringConverter());
        bean.addConverter(new StringToDateConverter());
        return bean;
    }

    @Bean
    public FieldsMapperService fieldsMapperService(ConversionService conversionService) {
        DefaultFieldsMapperService bean = new DefaultFieldsMapperService(Mockito.mock(BeanFactory.class),
                conversionService);
        bean.sort();
        return bean;
    }

    @Bean("formularTestSpecification")
    public FormSpecification formSpecification(FormMapper formMapper) {
        return formMapper.mapFormSpecification(TestForm.class, "post", "/test");
    }
}
