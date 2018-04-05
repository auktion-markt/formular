package de.auktionmarkt.formular.integration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Printer;

import java.util.Locale;

public class TestChooseableEntityPrinter implements Converter<TestChoosableEntity, String> {

    @Override
    public String convert(TestChoosableEntity source) {
        return source != null ? source.getDisplayValue() : null;
    }
}
