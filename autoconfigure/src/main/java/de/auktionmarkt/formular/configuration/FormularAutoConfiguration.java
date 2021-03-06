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

package de.auktionmarkt.formular.configuration;

import de.auktionmarkt.formular.internal.configuration.Finisher;
import de.auktionmarkt.formular.specification.mapper.FieldsMapperRegistry;
import de.auktionmarkt.formular.specification.mapper.FieldsMapperService;
import de.auktionmarkt.formular.specification.mapper.FormMapper;
import de.auktionmarkt.formular.specification.mapper.support.DefaultFieldsMapperService;
import de.auktionmarkt.formular.specification.mapper.support.DefaultFormMapper;
import de.auktionmarkt.formular.specification.mapper.support.GenericFieldsMapperService;
import de.auktionmarkt.formular.state.DefaultStateFactory;
import de.auktionmarkt.formular.state.StateFactory;
import de.auktionmarkt.formular.state.applicator.DefaultFormStateApplicator;
import de.auktionmarkt.formular.state.applicator.FormStateApplicator;
import de.auktionmarkt.formular.converter.DateToStringConverter;
import de.auktionmarkt.formular.converter.StringToDateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration} for Formular.
 */
@Configuration
@AutoConfigureBefore(Finisher.class)
public class FormularAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormularAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean({FieldsMapperService.class, FieldsMapperRegistry.class})
    public GenericFieldsMapperService fieldsMapperRegistry(BeanFactory beanFactory,
                                                           ConversionService conversionService) {
        return new DefaultFieldsMapperService(beanFactory, conversionService);
    }

    @Bean
    @ConditionalOnMissingBean(FormMapper.class)
    public DefaultFormMapper formMapper(BeanFactory beanFactory, FieldsMapperService fieldsMapperService) {
        return new DefaultFormMapper(beanFactory, fieldsMapperService);
    }

    @Bean
    @ConditionalOnMissingBean(StateFactory.class)
    public StateFactory stateFactory(MessageSource messageSource, ConversionService conversionService) {
        return new DefaultStateFactory(messageSource, conversionService);
    }

    @Bean
    @ConditionalOnMissingBean(FormStateApplicator.class)
    public FormStateApplicator formStateApplicator(ConversionService conversionService) {
        return new DefaultFormStateApplicator(conversionService);
    }

    @Configuration
    @Order
    @ConditionalOnWebApplication
    public static class WebMvcConfigurer extends WebMvcConfigurerAdapter {

        private final FormularConfiguration formularConfiguration;

        @Autowired
        public WebMvcConfigurer(FormularConfiguration formularConfiguration) {
            this.formularConfiguration = formularConfiguration;
        }

        @Override
        public void addFormatters(FormatterRegistry registry) {
            if (formularConfiguration.isRegisterMissingConverters()) {
                if (!(registry instanceof ConfigurableConversionService)) {
                    LOGGER.warn("Cannot auto register missing converters but converter registry is incompatible");
                } else {
                    ConfigurableConversionService castedRegistry = (ConfigurableConversionService) registry;
                    registerMissingConverter(castedRegistry, DateToStringConverter.INSTANCE);
                    registerMissingConverter(castedRegistry, StringToDateConverter.INSTANCE);
                }
            }
        }

        private static void registerMissingConverter(ConfigurableConversionService conversionService,
                                                     GenericConverter converter) {
            Set<GenericConverter.ConvertiblePair> missingConverters = new HashSet<>();
            for (GenericConverter.ConvertiblePair convertibleType : converter.getConvertibleTypes()) {
                if (!conversionService.canConvert(convertibleType.getSourceType(), convertibleType.getTargetType()))
                    missingConverters.add(convertibleType);
            }
            if (!missingConverters.isEmpty()) {
                LOGGER.debug("Registering converter {} since there is no conversion for the following conversions: {}",
                        converter.getClass().getName(), missingConverters);
                conversionService.addConverter(converter);
            }
        }
    }
}
