package de.auktionmarkt.formular.integration;

import org.springframework.core.convert.converter.Converter;

public class TestChooseableEntityPrinter implements Converter<TestChoosableEntity, String> {

    @Override
    public String convert(TestChoosableEntity source) {
        return source != null ? source.getDisplayValue() : null;
    }
}
