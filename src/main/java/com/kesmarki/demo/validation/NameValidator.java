package com.kesmarki.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class NameValidator implements ConstraintValidator<Name, String> {

    private boolean isOptional;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        if (isOptional) {
            return name == null || !(name.isBlank() || name.isEmpty());
        } else {
            return name != null && !(name.isBlank() || name.isEmpty());
        }
    }

    @Override
    public void initialize(Name constraintAnnotation) {
        isOptional = constraintAnnotation.isOptional();
    }
}
