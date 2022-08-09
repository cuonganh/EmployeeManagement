package com.example.employee.model.exception;

import com.example.employee.common.Constant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNotFoundException extends Exception {

    private final int code;
    private final String message;

    ResourceNotFoundException(){
        this.code = 404;
        this.message = Constant.MESSAGE_NOT_FOUND;
    }

    ResourceNotFoundException(String message){
        this.code = 404;
        this.message = message;
    }

    ResourceNotFoundException(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
