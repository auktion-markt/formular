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

package de.auktionmarkt.formular.support.converter;

import de.auktionmarkt.formular.specification.annotation.FormInput;
import de.auktionmarkt.formular.internal.CollectionUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Set;

/**
 * Convert {@link Date}, {@link LocalDate}, {@link LocalTime} and {@link LocalDateTime} to strings which can be used
 * for html5 inputs of type {@code date}, {@code time} and {@code datetime-local}. The converter might be aware of
 * time zones when {@code zoneIdResolver} is set.
 */
public class DateToStringConverter implements ConditionalGenericConverter {

    public static final DateToStringConverter INSTANCE = new DateToStringConverter();
    private static final Set<GenericConverter.ConvertiblePair> CONVERTIBLE_TYPES = CollectionUtils.warpImmutableSet(
            new GenericConverter.ConvertiblePair(Date.class, String.class),
            new GenericConverter.ConvertiblePair(LocalDate.class, String.class),
            new GenericConverter.ConvertiblePair(LocalTime.class, String.class),
            new GenericConverter.ConvertiblePair(LocalDateTime.class, String.class)
    );

    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return CONVERTIBLE_TYPES;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return sourceType.hasAnnotation(FormInput.class);
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null)
            return null;
        Class<?> type = sourceType.getType();
        ZoneId zoneId = LocaleContextHolder.getTimeZone().toZoneId();
        if (type.isAssignableFrom(LocalDate.class))
            return DateTimeFormatter.ISO_LOCAL_DATE.withZone(zoneId).format((TemporalAccessor) source);
        else if (type.isAssignableFrom(LocalDateTime.class))
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(zoneId).format((TemporalAccessor) source);
        else if (type.isAssignableFrom(LocalTime.class))
            return DateTimeFormatter.ISO_LOCAL_TIME.withZone(zoneId).format((TemporalAccessor) source);
        else if (type.isAssignableFrom(Date.class))
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(zoneId).format(((Date) source).toInstant());
        throw new UnsupportedOperationException("Not supported: " + type.getName());
    }
}
