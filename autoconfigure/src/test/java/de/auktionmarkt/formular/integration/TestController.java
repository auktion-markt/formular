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

import de.auktionmarkt.formular.specification.FormSpecification;
import de.auktionmarkt.formular.specification.mapper.FormMapper;
import de.auktionmarkt.formular.state.FormState;
import de.auktionmarkt.formular.state.StateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.concurrent.atomic.AtomicReference;

@Controller
public class TestController {

    private final AtomicReference<TestForm> form = new AtomicReference<>();
    private final FormMapper formMapper;
    private final StateFactory stateFactory;

    @Autowired
    public TestController(FormMapper formMapper, StateFactory stateFactory) {
        this.formMapper = formMapper;
        this.stateFactory = stateFactory;
    }

    @GetMapping("/test_form")
    public Object get(Model model) {
        FormSpecification formSpecification = formMapper.mapFormSpecification(TestForm.class, "post", "/test");
        model.addAttribute("form_specification", formSpecification);
        TestForm previous = form.get();
        FormState formState = previous != null ?
                stateFactory.createStateFromModel(formSpecification, previous) :
                stateFactory.createEmptyState(formSpecification);
        model.addAttribute("form_state", formState);
        model.addAttribute("available", previous != null);
        return "test_form";
    }

    @PostMapping("/test_form")
    public String post(@Valid TestForm form, BindingResult bindingResult, Model model) {
        FormSpecification formSpecification = formMapper.mapFormSpecification(TestForm.class, "post", "/test");
        if (bindingResult.hasErrors()) {
            model.addAttribute("form_specification", formSpecification);
            model.addAttribute("available", false);
            model.addAttribute("form_state",
                    stateFactory.createStateFromBindingResult(formSpecification, bindingResult));
            return "test_form";
        }
        this.form.set(form);
        return "test_form_success";
    }
}
