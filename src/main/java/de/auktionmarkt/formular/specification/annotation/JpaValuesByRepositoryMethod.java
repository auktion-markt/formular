package de.auktionmarkt.formular.specification.annotation;

import de.auktionmarkt.formular.specification.support.RepositoryParameterFiller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface JpaValuesByRepositoryMethod {

    String value() default "findAll";

    Class<?>[] parameters() default {};

    String fillerBeanName() default "";
}
