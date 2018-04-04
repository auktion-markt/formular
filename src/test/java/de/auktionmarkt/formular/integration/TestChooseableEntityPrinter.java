package de.auktionmarkt.formular.integration;

import org.springframework.format.Printer;

import java.util.Locale;

public class TestChooseableEntityPrinter implements Printer<TestChoosableEntity> {

    @Override
    public String print(TestChoosableEntity object, Locale locale) {
        return object.getDisplayValue();
    }
}
