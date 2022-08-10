package com.example.employee.model.entity;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "team")
@Data
public class Teams {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "employee_id")
    private Long employeeId;

}
