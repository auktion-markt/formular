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

package de.auktionmarkt.formular.specification.mapper;

import de.auktionmarkt.formular.converter.DateToStringConverter;
import de.auktionmarkt.formular.converter.StringToDateConverter;
import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.FieldTypes;
import de.auktionmarkt.formular.specification.FormSpecification;
import de.auktionmarkt.formular.specification.mapper.support.DefaultFieldsMapperService;
import de.auktionmarkt.formular.specification.mapper.support.DefaultFormMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FormMappingTest {

    private static FormMapper formMapper;

    @BeforeClass
    public static void prepare() {
        GenericConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new DateToStringConverter());
        conversionService.addConverter(new StringToDateConverter());
        DefaultFieldsMapperService fieldsMapperService =
                new DefaultFieldsMapperService(Mockito.mock(BeanFactory.class), conversionService);
        fieldsMapperService.sort();
        formMapper = new DefaultFormMapper(fieldsMapperService);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMapping() {
        FormSpecification formSpecification =
                formMapper.mapFormSpecification(TestForm.class, "post", "/test");
        Assert.assertNotNull(formSpecification);
        Assert.assertEquals(TestForm.class, formSpecification.getSpecifiedClass());
        Assert.assertEquals("post", formSpecification.getMethod());
        Assert.assertEquals("/test", formSpecification.getActionScheme());
        Map<String, FieldSpecification> fieldMap = formSpecification.getFields();
        Assert.assertEquals(6, fieldMap.size());

        // Check order
        List<FieldSpecification> fields = new ArrayList<>(fieldMap.values());
        Assert.assertEquals("password", fields.get(0).getPath());
        Assert.assertEquals("localDateTime", fields.get(5).getPath());

        FieldSpecification requiredInt = fieldMap.get("requiredInt");
        Assert.assertNotNull(requiredInt);
        Assert.assertEquals(FieldTypes.NUMBER, requiredInt.getType());
        Assert.assertEquals(Boolean.TRUE, requiredInt.getParameters().get("required"));
        Assert.assertEquals(Integer.MIN_VALUE, ((Number) requiredInt.getParameters().get("minValue")).intValue());
        Assert.assertEquals(42, ((Number) requiredInt.getParameters().get("maxValue")).intValue());
        Assert.assertEquals("Required Integer", requiredInt.getLabel());

        FieldSpecification optionalInt = fieldMap.get("optionalInt");
        Assert.assertNotNull(optionalInt);
        Assert.assertEquals(FieldTypes.NUMBER, optionalInt.getType());
        Assert.assertNotEquals(Boolean.TRUE, optionalInt.getParameters().get("required"));
        Assert.assertEquals("Optional Integer", optionalInt.getLabel());

        FieldSpecification localDateTime = fieldMap.get("localDateTime");
        Assert.assertNotNull(localDateTime);
        Assert.assertEquals(FieldTypes.DATETIME, localDateTime.getType());
        Assert.assertNotEquals(Boolean.TRUE, localDateTime.getParameters().get("required"));
        Assert.assertEquals("localDateTime", localDateTime.getLabel());

        FieldSpecification password = fieldMap.get("password");
        Assert.assertNotNull(password);
        Assert.assertEquals(FieldTypes.PASSWORD, password.getType());
        Assert.assertEquals(Boolean.TRUE, password.getParameters().get("required"));
        Assert.assertEquals("Enter a password", password.getLabel());

        FieldSpecification embeddedInteger = fieldMap.get("embeddedForm.anInteger");
        Assert.assertNotNull(embeddedInteger);
        Assert.assertEquals(FieldTypes.NUMBER, embeddedInteger.getType());
        Assert.assertEquals("An Embedded Form", ((Supplier<String>) embeddedInteger.getParameters().get("titleSupplier")).get());
        Assert.assertEquals("embeddedForm", embeddedInteger.getParameters().get("parent"));
        Assert.assertTrue((Boolean) embeddedInteger.getParameters().get("groupStart"));

        FieldSpecification embeddedString = fieldMap.get("embeddedForm.AString");
        Assert.assertNotNull(embeddedString);
        Assert.assertEquals(FieldTypes.TEXT, embeddedString.getType());
        Assert.assertTrue((Boolean) embeddedString.getParameters().get("groupEnd"));
        Assert.assertEquals("embeddedForm", embeddedString.getParameters().get("parent"));
    }
}
