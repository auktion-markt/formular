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

package de.auktionmarkt.formular.integration;

import de.auktionmarkt.formular.configuration.FormularAutoConfiguration;
import de.auktionmarkt.formular.configuration.FormularConfiguration;
import de.auktionmarkt.formular.test.CustomMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@EnableWebMvc
@SpringBootTest(classes = {TestBeans.class, TestController.class, FormularAutoConfiguration.class,
        FormularConfiguration.class, FreemarkerIntegration.class, FreeMarkerAutoConfiguration.class})
public class FormIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test() throws Exception {
        // Check general form creation
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/test_form");
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.log())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Mode: create")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("method=\"post\"")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("action=\"/test\"")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("<<label for optional int>>")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("<<label for required int>>")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("<<title for embeddedForm>>")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("<<label for anotherString>>")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("<<label for singleSelected>>")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("<<label for checkbox>>")));

        // Check if validation result is displayed properly
        requestBuilder = MockMvcRequestBuilders.post("/test_form")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("requiredInt=50&optionalInt=12");
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.log())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("<<test integer is too big>>")));

        // Check "real" submit
        requestBuilder = MockMvcRequestBuilders.post("/test_form")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("requiredInt=42&optionalInt=12&AString=HelloImTheValueOfAString&embeddedForm.anotherString=TheValueOfAnotherTestString&checkbox=on&singleSelected=MINUTES");
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.log())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("<p>successs</p>")));

        // Check predefined values from entity
        requestBuilder = MockMvcRequestBuilders.get("/test_form");
        ResultActions resultActions = mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.log())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Mode: edit")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("value=\"42\"")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("value=\"12\"")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("value=\"HelloImTheValueOfAString\"")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("value=\"TheValueOfAnotherTestString\"")))
                .andExpect(MockMvcResultMatchers.content().string(CustomMatchers.matchesPattern("input\\s+type\\=\\\"checkbox\\\"[^>]+checked")))
                .andExpect(MockMvcResultMatchers.content().string(CustomMatchers.matchesPattern("value\\=\\\"MINUTES\\\"[^>]+selected")));
        for (TimeUnit timeUnit : TimeUnit.values())
            resultActions.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(timeUnit.name())));
    }
}
