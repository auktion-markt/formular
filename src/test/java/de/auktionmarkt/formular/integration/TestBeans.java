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

package de.auktionmarkt.formular.integration;

import de.auktionmarkt.formular.specification.FormSpecification;
import de.auktionmarkt.formular.specification.mapper.FormMapper;
import de.auktionmarkt.formular.specification.mapper.support.DefaultFormMapper;
import de.auktionmarkt.formular.support.converter.DateToStringConverter;
import de.auktionmarkt.formular.support.converter.StringToDateConverter;
import de.auktionmarkt.formular.specification.mapper.FieldsMapperService;
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
    public FormMapper formMapper(FieldsMapperService fieldsMapperService) {
        return new DefaultFormMapper(fieldsMapperService);
    }

    @Bean("formularTestSpecification")
    public FormSpecification formSpecification(FormMapper formMapper) {
        return formMapper.mapFormSpecification(TestForm.class, "post", "/test");
    }
}
