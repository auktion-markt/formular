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

package de.auktionmarkt.formular.converter;

import de.auktionmarkt.formular.specification.annotation.FormInput;
import de.auktionmarkt.formular.internal.CollectionUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalQueries;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

public class StringToDateConverter implements ConditionalGenericConverter {

    public static final StringToDateConverter INSTANCE = new StringToDateConverter();
    private static final Set<ConvertiblePair> CONVERTIBLE_TYPES = CollectionUtils.warpImmutableSet(
            new ConvertiblePair(String.class, Date.class),
            new ConvertiblePair(String.class, LocalDate.class),
            new ConvertiblePair(String.class, LocalTime.class),
            new ConvertiblePair(String.class, LocalDateTime.class)
    );

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return CONVERTIBLE_TYPES;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return targetType.hasAnnotation(FormInput.class);
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null)
            return null;
        Class<?> type = targetType.getType();
        ZoneId zoneId = LocaleContextHolder.getTimeZone().toZoneId();
        try {
            if (type.isAssignableFrom(LocalDate.class)) {
                return DateTimeFormatter.ISO_LOCAL_DATE
                        .withZone(zoneId)
                        .parse(Objects.toString(source), TemporalQueries.localDate());
            } else if (type.isAssignableFrom(LocalDateTime.class)) {
                return LocalDateTime.parse(Objects.toString(source),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(zoneId));
            } else if (type.isAssignableFrom(LocalTime.class)) {
                return DateTimeFormatter.ISO_LOCAL_TIME
                        .withZone(zoneId)
                        .parse(Objects.toString(source), TemporalQueries.localTime());
            } else if (type.isAssignableFrom(Date.class)) {
                LocalDateTime parsed = LocalDateTime.parse(Objects.toString(source),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(zoneId));
                return Date.from(parsed.atZone(zoneId).toInstant());
            }
        } catch (DateTimeParseException ignore) {
            return null;
        }
        throw new UnsupportedOperationException("Not supported: " + type.getName());
    }
}
