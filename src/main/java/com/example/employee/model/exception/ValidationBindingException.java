package com.example.employee.model.exception;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.stream.Collectors;

public class ValidationBindingException extends Exception {

    private final BindingResult errors;

    public ValidationBindingException(BindingResult errors) {
        this.errors = errors;
    }

    @Override
    public String getMessage() {
        return getValidationMessage(this.errors);
    }

    private static String getValidationMessage(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream().map(ValidationBindingException::getValidationMessage)
            .collect(Collectors.joining(", "));
    }

    private static String getValidationMessage(ObjectError error) {
        if (error instanceof FieldError) {
            FieldError fieldError = (FieldError) error;
            String property = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            return String.format("%s %s", property, message);
        }
        return String.format("%s: %s", error.getObjectName(), error.getDefaultMessage());
    }

}
