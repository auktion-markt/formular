package de.auktionmarkt.formular.specification.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a {@link FormInput} is a jpa entity reference (e.g. a {@link javax.persistence.ManyToOne} relation).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface EntityReference {

    /**
     * Returns the type of the entity. It should be specified when a non-parametrized collection is annotated.
     *
     * @return The type of the entity
     */
    Class<?> entityClass() default void.class;
}
