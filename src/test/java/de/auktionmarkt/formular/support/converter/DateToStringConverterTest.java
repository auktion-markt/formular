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

public class DateToStringConverterTest {

    private static GenericConversionService conversionService;

    @BeforeClass
    public static void prepare() {
        conversionService = new GenericConversionService();
        conversionService.addConverter(new DateToStringConverter());
    }

    @Test
    public void testConvertDate() {
        // 2018-03-23 12:05:40+01:00
        Date date = new Date(1521803140000L);
        TypeDescriptor typeDescriptor = ConverterTestUtils.createTypeDescriptor(Date.class);
        Assert.assertTrue(conversionService.canConvert(typeDescriptor, TypeDescriptors.STRING_TYPE));
        Object converted = conversionService.convert(date, typeDescriptor, TypeDescriptors.STRING_TYPE);
        Assert.assertEquals("2018-03-23T12:05:40", converted);
    }

    @Test
    public void testConvertLocalDateTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2018, 3, 23, 12, 5, 40);
        TypeDescriptor typeDescriptor = ConverterTestUtils.createTypeDescriptor(LocalDateTime.class);
        Assert.assertTrue(conversionService.canConvert(typeDescriptor, TypeDescriptors.STRING_TYPE));
        Object converted = conversionService.convert(localDateTime, typeDescriptor, TypeDescriptors.STRING_TYPE);
        Assert.assertEquals("2018-03-23T12:05:40", converted);
    }

    @Test
    public void testConvertLocalTime() {
        LocalTime localTime = LocalTime.of(12, 5, 40);
        TypeDescriptor typeDescriptor = ConverterTestUtils.createTypeDescriptor(LocalTime.class);
        Assert.assertTrue(conversionService.canConvert(typeDescriptor, TypeDescriptors.STRING_TYPE));
        Object converted = conversionService.convert(localTime, typeDescriptor, TypeDescriptors.STRING_TYPE);
        Assert.assertEquals("12:05:40", converted);
    }

    @Test
    public void testConvertLocalDate() {
        LocalDate localDate = LocalDate.of(2018, 3, 23);
        TypeDescriptor typeDescriptor = ConverterTestUtils.createTypeDescriptor(LocalDate.class);
        Assert.assertTrue(conversionService.canConvert(typeDescriptor, TypeDescriptors.STRING_TYPE));
        Object converted = conversionService.convert(localDate, typeDescriptor, TypeDescriptors.STRING_TYPE);
        Assert.assertEquals("2018-03-23", converted);
    }
}
