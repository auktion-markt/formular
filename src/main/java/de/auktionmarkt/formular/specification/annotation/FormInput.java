package de.auktionmarkt.formular.specification.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface FormInput {

    String type() default "";

    String label() default "";

    String labelSupplierBean() default "";

    boolean required() default false;

    Class<?> elementType() default void.class;
}
