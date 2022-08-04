package com.example.employee.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class EmployeeDto {

    private Long employeeId;

    private Long departmentId;

    private Long projectId;

    private String firstName;

    private String lastName;

    private Date dateOfBirth;

    private String address;

    private String email;

    private String phoneNumber;

}
