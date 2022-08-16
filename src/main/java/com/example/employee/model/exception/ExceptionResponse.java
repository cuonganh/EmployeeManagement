package com.example.employee.model.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExceptionResponse {
    private int code;
    private String message;

    public ExceptionResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
