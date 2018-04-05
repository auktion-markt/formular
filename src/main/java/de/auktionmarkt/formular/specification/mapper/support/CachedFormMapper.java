package de.auktionmarkt.formular.specification.mapper.support;

import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.mapper.FormMapper;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CachedFormMapper implements FormMapper {

    private final ConcurrentMap<Class<?>, List<FieldSpecification>> fields = new ConcurrentHashMap<>();
    private final FormMapper delegate;

    public CachedFormMapper(FormMapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<FieldSpecification> mapFields(Class<?> dataClass) {
        return fields.computeIfAbsent(dataClass, delegate::mapFields);
    }
}
