package com.example.employee.model.payload;

import lombok.Getter;

@Getter
public class EmployeeResponse<T> {

    private final int code;
    private final String message;
    private final T result;

    public EmployeeResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.result = null;
    }

    public EmployeeResponse(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

}
