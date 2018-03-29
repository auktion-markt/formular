package de.auktionmarkt.formular.specification.mapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public interface FieldsMapperRegistry {

    default void registerFieldsMapper(FieldsMapper fieldsMapper) {
        registerFieldsMappers(Collections.singleton(fieldsMapper));
    }

    default void registerFieldsMappers(FieldsMapper... fieldsMappers) {
        registerFieldsMappers(Arrays.asList(fieldsMappers));
    }

    void registerFieldsMappers(Collection<FieldsMapper> fieldsMappers);
}
