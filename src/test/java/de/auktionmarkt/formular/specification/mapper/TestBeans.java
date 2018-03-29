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
