package de.auktionmarkt.formular.specification.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FormSubmitField {

    /**
     * Returns the label of the form input.
     *
     * @return The label of the form input
     */
    String label() default "";

    /**
     * Returns the name of a bean implementing {@link java.util.function.Supplier} and returns a {@link String} as the
     * title of the field.
     *
     * @return The name of the title supplier bean
     */
    String labelSupplierBean() default "";
}
