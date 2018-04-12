package de.auktionmarkt.formular.integration;

import org.springframework.core.convert.converter.Converter;

public class TestChooseableEntityLoader implements Converter<String, TestChoosableEntity> {

    private final TestChooseableEntityRepository repository;

    public TestChooseableEntityLoader(TestChooseableEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public TestChoosableEntity convert(String source) {
        return repository.findOne(Integer.parseInt(source));
    }
}
