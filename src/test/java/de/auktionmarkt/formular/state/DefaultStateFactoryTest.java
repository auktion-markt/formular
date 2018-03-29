package de.auktionmarkt.formular.state;

import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.FieldTypes;
import de.auktionmarkt.formular.specification.FormSpecification;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import java.util.Collections;
import java.util.Map;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {DefaultStateFactory.class, TestBeans.class})
public class DefaultStateFactoryTest {

    @Autowired
    private StateFactory stateFactory;

    @Test
    public void testStateFactory() {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(TestForm.class);
        FormSpecification formSpecification = FormSpecification.create(TestForm.class, "post",
                "/test", Collections.singleton(
                        new FieldSpecification(beanWrapper.getPropertyDescriptor("test1"),
                                beanWrapper.getPropertyTypeDescriptor("test1"), () ->"Test 1",
                                "test1", FieldTypes.NUMBER, Collections.emptyMap(), 0, () -> null)
        ));

        TestForm testForm = new TestForm();
        testForm.setTest1(42);

        BindingResult bindingResult = new MapBindingResult(Collections.singletonMap("test1", 42), "test");
        FormState formState = stateFactory.createStateFromModel(formSpecification, testForm);
        Map<String, FieldState> fieldStateMap = formState.getFieldStates();
        Assert.assertEquals(1, fieldStateMap.size());
        FieldState fieldState = fieldStateMap.get("test1");
        Assert.assertNotNull(fieldState);
        Assert.assertEquals("42", fieldState.getValue());

        // Todo: Test using BindingResult
    }
}
