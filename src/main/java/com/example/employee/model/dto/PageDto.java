package com.example.employee.model.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageDto <T>{

    private int code;
    private String message;
    private int limit;
    private int offset;
    private long total;
    private List<?> result;

    public PageDto(int code, String message, int limit, int offset, long total, List<?> data) {
        this.code = code;
        this.message = message;
        this.limit = limit;
        this.offset = offset;
        this.total = total;
        this.result = data;
    }


}
