package de.auktionmarkt.formular.internal.configuration;

import de.auktionmarkt.formular.specification.mapper.support.GenericFieldsMapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnBean(GenericFieldsMapperService.class)
public class Finisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Finisher.class);

    private final GenericFieldsMapperService fieldsMapperService;

    @Autowired
    public Finisher(GenericFieldsMapperService fieldsMapperService) {
        this.fieldsMapperService = fieldsMapperService;
    }

    @PostConstruct
    public void postConstruct() {
        LOGGER.debug("Finishing fields mapper service {}...", fieldsMapperService);
        fieldsMapperService.sort();
    }
}
