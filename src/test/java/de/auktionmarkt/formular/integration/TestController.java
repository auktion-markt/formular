package de.auktionmarkt.formular.integration;

import de.auktionmarkt.formular.specification.FormSpecification;
import de.auktionmarkt.formular.state.FormState;
import de.auktionmarkt.formular.state.StateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final FormSpecification formSpecification;
    private final StateFactory stateFactory;

    @Autowired
    public TestController(@Qualifier("formularTestSpecification") FormSpecification formSpecification,
                          StateFactory stateFactory) {
        this.formSpecification = formSpecification;
        this.stateFactory = stateFactory;
    }

    @GetMapping("/test_form")
    public Object get(Model model) {
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
