package com.example.employee.controller;

import com.example.employee.model.exception.ExceptionResponse;
import com.example.employee.model.exception.ResourceNotFoundException;
import com.example.employee.model.exception.ValidationBindingException;
import com.example.employee.model.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ServiceExceptionHandle {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseBody
    private ExceptionResponse resourceNotFoundException(ResourceNotFoundException exception) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCode(exception.getCode());
        exceptionResponse.setMessage(exception.getMessage());
        return exceptionResponse;
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    protected Object resourceNotFoundException(HttpRequestMethodNotSupportedException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        exceptionResponse.setMessage(ex.getMessage());
        return exceptionResponse;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            ValidationException.class,
            ValidationBindingException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseBody
    private ExceptionResponse resourceNotFoundException(ValidationException exception) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCode(HttpStatus.BAD_REQUEST.value());
        exceptionResponse.setMessage(exception.getMessage());
        return exceptionResponse;
    }


}
