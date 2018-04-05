package de.auktionmarkt.formular.integration;

import de.auktionmarkt.formular.support.converter.DateToStringConverter;
import de.auktionmarkt.formular.support.converter.StringToDateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class TestWebConfiguration extends WebMvcConfigurerAdapter {

    private final TestChooseableEntityRepository repository;

    @Autowired
    public TestWebConfiguration(TestChooseableEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new DateToStringConverter());
        registry.addConverter(new StringToDateConverter());
        registry.addConverter(new TestChooseableEntityLoader(repository));
        registry.addConverter(new TestChooseableEntityPrinter());
    }
}
