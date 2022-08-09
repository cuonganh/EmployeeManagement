package com.example.employee.model.exception;

import java.util.List;

public class ValidationException extends Exception {

    private List<String> messages;

    public ValidationException(List<String> errorMessages) {
        this.messages = errorMessages;
    }

    @Override
    public String getMessage() {
        return getValidationMessage(this.messages);
    }

    private String getValidationMessage(List<String> bindingMessages) {
        return String.join(",", bindingMessages);
    }
}
