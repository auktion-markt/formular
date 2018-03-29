package de.auktionmarkt.formular.specification.mapper;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.FieldTypes;
import de.auktionmarkt.formular.specification.FormSpecification;
import de.auktionmarkt.formular.specification.mapper.support.BasicFieldsMapper;
import de.auktionmarkt.formular.specification.mapper.support.DateFieldsMapper;
import de.auktionmarkt.formular.specification.mapper.support.DefaultFormMapper;
import de.auktionmarkt.formular.specification.mapper.support.EnumFieldsMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestBeans.class, DefaultFormMapper.class, BasicFieldsMapper.class,
        DateFieldsMapper.class, EnumFieldsMapper.class})
public class FormMappingTest {

    @Autowired
    private FormMapper formMapper;

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
        System.out.println(embeddedInteger.getParameters().keySet());
        Assert.assertNotNull(embeddedInteger);
        Assert.assertEquals(FieldTypes.NUMBER, embeddedInteger.getType());
        Assert.assertEquals("An Embedded Form", ((Supplier<String>) embeddedInteger.getParameters().get("titleSupplier")).get());
        Assert.assertEquals("embeddedForm", embeddedInteger.getParameters().get("parent"));
        Assert.assertTrue((Boolean) embeddedInteger.getParameters().get("groupStart"));

        FieldSpecification embeddedString = fieldMap.get("embeddedForm.AString");
        Assert.assertNotNull(embeddedString);
        Assert.assertEquals(FieldTypes.TEXT, embeddedString.getType());
        System.out.println(embeddedString.getParameters());
        Assert.assertTrue((Boolean) embeddedString.getParameters().get("groupEnd"));
        Assert.assertEquals("embeddedForm", embeddedString.getParameters().get("parent"));
    }
}
