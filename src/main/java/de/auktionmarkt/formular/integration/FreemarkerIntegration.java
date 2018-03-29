package de.auktionmarkt.formular.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * Injects an additional template loader path containing the Formular Freemarker macros.
 */
@Configuration
@AutoConfigureBefore(FreeMarkerConfigurer.class)
public class FreemarkerIntegration {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreemarkerIntegration.class);
    private static final String TEMPLATE_PATH = "classpath:/formular/integration/freemarker";

    private final FreeMarkerProperties properties;

    @Autowired
    public FreemarkerIntegration(FreeMarkerProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void configure() {
        String[] templateLoaderPaths = properties.getTemplateLoaderPath();
        templateLoaderPaths = Arrays.copyOf(templateLoaderPaths, templateLoaderPaths.length + 1);
        templateLoaderPaths[templateLoaderPaths.length - 1] = TEMPLATE_PATH;
        properties.setTemplateLoaderPath(templateLoaderPaths);
        LOGGER.info("Added template loader path {} for Formular Freemarker macro integration", TEMPLATE_PATH);
    }
}
