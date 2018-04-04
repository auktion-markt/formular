package de.auktionmarkt.formular.internal.configuration;

import de.auktionmarkt.formular.configuration.FormularAutoConfiguration;
import de.auktionmarkt.formular.specification.mapper.FieldsMapperRegistry;
import de.auktionmarkt.formular.specification.mapper.support.EntityFieldsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

@Configuration
@ConditionalOnClass(LocalContainerEntityManagerFactoryBean.class)
@AutoConfigureBefore(Finisher.class)
@AutoConfigureAfter(FormularAutoConfiguration.class)
public class DataJpaAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataJpaAutoConfiguration.class);

    private final ConversionService conversionService;
    private final FieldsMapperRegistry fieldsMapperRegistry;
    private final EntityManagerFactory entityManagerFactory;
    private final ListableBeanFactory listableBeanFactory;

    @Autowired
    public DataJpaAutoConfiguration(ConversionService conversionService, FieldsMapperRegistry fieldsMapperRegistry,
                                    EntityManagerFactory entityManagerFactory,
                                    ListableBeanFactory listableBeanFactory) {
        this.conversionService = conversionService;
        this.fieldsMapperRegistry = fieldsMapperRegistry;
        this.entityManagerFactory = entityManagerFactory;
        this.listableBeanFactory = listableBeanFactory;
    }

    @PostConstruct
    public void register() {
        LOGGER.info("Spring-data-jpa integration for Formular enabled");
        fieldsMapperRegistry.registerFieldsMappers(
                new EntityFieldsMapper(conversionService, entityManagerFactory, listableBeanFactory));
    }
}
