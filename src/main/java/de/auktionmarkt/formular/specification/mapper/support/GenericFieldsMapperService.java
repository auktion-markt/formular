/*
 *    Copyright 2018 Auktion & Markt AG
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.auktionmarkt.formular.specification.mapper.support;

import de.auktionmarkt.formular.specification.mapper.ConfigurableFieldsMapperRegistry;
import de.auktionmarkt.formular.specification.mapper.FieldsMapper;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * An implementation of {@link ConfigurableFieldsMapperRegistry} which preservers the order of registered elements.
 * After a new {@link FieldsMapper} is registered the implementation prevents the usage of
 * {@link #getFieldsMapper(Class, PropertyDescriptor, TypeDescriptor)} until it will gets sorted by calling
 * {@link #sort()}.
 *
 * @see AnnotationAwareOrderComparator
 */
public class GenericFieldsMapperService implements ConfigurableFieldsMapperRegistry {

    // Synchronize on this object should prevent concurrent calls to registerFieldMappers and sortFieldMappers
    private final Object lock = new Object();
    private final List<FieldsMapper> fieldsMappers = new ArrayList<>();
    private volatile boolean sorted = true;

    /**
     * {@inheritDoc}
     *
     * Callers should be aware that adding elements will sets the instance into an non-sorted state. After a external
     * finished its registration of new {@link FieldsMapper}s it should call {@link #sort()}.
     */
    @Override
    public void registerFieldsMappers(Collection<FieldsMapper> fieldsMappers) {
        Objects.requireNonNull(fieldsMappers, "fieldsMappers must not be null");
        if (!fieldsMappers.isEmpty()) {
            synchronized (lock) {
                boolean changed = this.fieldsMappers.addAll(fieldsMappers);
                if (changed)
                    sorted = false;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException Thrown when the registry instance is in a non-sorted state
     */
    @Override
    public FieldsMapper getFieldsMapper(Class<?> model, PropertyDescriptor propertyDescriptor,
                                        TypeDescriptor typeDescriptor) {
        if (!sorted)
            throw new UnsupportedOperationException("Mapper service is currently unsorted.");
        if (fieldsMappers.isEmpty())
            throw new UnsupportedOperationException("No fields mappers registered");
        for (FieldsMapper fieldsMapper : fieldsMappers) {
            if (fieldsMapper.supportsField(model, propertyDescriptor, typeDescriptor))
                return fieldsMapper;
        }
        throw new UnsupportedOperationException("No fields mappers registered");
    }

    /**
     * Sorts all the registered {@link FieldsMapper}s.
     */
    public void sort() {
        if (!sorted) {
            synchronized (lock) {
                fieldsMappers.sort(AnnotationAwareOrderComparator.INSTANCE);
                sorted = true;
            }
        }
    }

    /**
     * Gets if the registered {@link FieldsMapper}s are sorted.
     *
     * @return {@code true} if all registered {@link FieldsMapper}s are sorted
     */
    public boolean isSorted() {
        return sorted;
    }
}
