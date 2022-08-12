package com.example.employee.model.dto;

import lombok.Data;

@Data
public class DepartmentDto {

    private Long departmentId;

    private String name;

    private Long member;

    private String description;

    private String leader;

}
