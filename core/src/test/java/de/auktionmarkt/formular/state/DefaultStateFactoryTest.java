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

package de.auktionmarkt.formular.state;

import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.FieldTypes;
import de.auktionmarkt.formular.specification.FormSpecification;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Collections;
import java.util.Map;

public class DefaultStateFactoryTest {

    private static StateFactory stateFactory;

    @BeforeClass
    public static void prepare() {
        MessageSource messageSource = Mockito.mock(MessageSource.class);
        ConversionService conversionService = new DefaultConversionService();
        stateFactory = new DefaultStateFactory(messageSource, conversionService);
    }

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

        FormState formState = stateFactory.createStateFromModel(formSpecification, testForm);
        Map<String, FieldState> fieldStateMap = formState.getFieldStates();
        Assert.assertEquals(1, fieldStateMap.size());
        FieldState fieldState = fieldStateMap.get("test1");
        Assert.assertNotNull(fieldState);
        Assert.assertEquals("42", fieldState.getValue());

        // Todo: Test using BindingResult
    }
}
