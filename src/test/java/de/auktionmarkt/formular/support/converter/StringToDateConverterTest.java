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

import de.auktionmarkt.formular.internal.TypeDescriptors;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class StringToDateConverterTest {

    private static GenericConversionService conversionService;

    @BeforeClass
    public static void prepare() {
        conversionService = new GenericConversionService();
        conversionService.addConverter(new StringToDateConverter());
    }

    @Test
    public void testConvertDate() {
        // 2018-03-23 12:05:40+01:00
        TypeDescriptor typeDescriptor = ConverterTestUtils.createTypeDescriptor(Date.class);
        Assert.assertTrue(conversionService.canConvert(TypeDescriptors.STRING_TYPE, typeDescriptor));
        Object converted = conversionService.convert("2018-03-23T12:05:40", TypeDescriptors.STRING_TYPE, typeDescriptor);
        Assert.assertEquals(new Date(1521803140000L), converted);
    }

    @Test
    public void testConvertLocalDateTime() {
        // 2018-03-23 12:05:40+01:00
        TypeDescriptor typeDescriptor = ConverterTestUtils.createTypeDescriptor(LocalDateTime.class);
        Assert.assertTrue(conversionService.canConvert(TypeDescriptors.STRING_TYPE, typeDescriptor));
        Object converted = conversionService.convert("2018-03-23T12:05:40", TypeDescriptors.STRING_TYPE, typeDescriptor);
        Assert.assertEquals(LocalDateTime.of(2018, 3, 23, 12, 5, 40), converted);
    }

    @Test
    public void testConvertLocalTime() {
        // 2018-03-23 12:05:40+01:00
        TypeDescriptor typeDescriptor = ConverterTestUtils.createTypeDescriptor(LocalTime.class);
        Assert.assertTrue(conversionService.canConvert(TypeDescriptors.STRING_TYPE, typeDescriptor));
        Object converted = conversionService.convert("12:05:40", TypeDescriptors.STRING_TYPE, typeDescriptor);
        Assert.assertEquals(LocalTime.of(12, 5, 40), converted);
    }

    @Test
    public void testConvertLocalDate() {
        // 2018-03-23 12:05:40+01:00
        TypeDescriptor typeDescriptor = ConverterTestUtils.createTypeDescriptor(LocalDate.class);
        Assert.assertTrue(conversionService.canConvert(TypeDescriptors.STRING_TYPE, typeDescriptor));
        Object converted = conversionService.convert("2018-03-23", TypeDescriptors.STRING_TYPE, typeDescriptor);
        Assert.assertEquals(LocalDate.of(2018, 3, 23), converted);
    }
}
