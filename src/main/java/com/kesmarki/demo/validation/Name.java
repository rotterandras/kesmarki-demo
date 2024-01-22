package com.kesmarki.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Constraint(validatedBy = NameValidator.class)
public @interface Name {

    String message() default "Name must not be blank or empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean isOptional() default false;
}
