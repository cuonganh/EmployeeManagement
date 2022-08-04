package com.example.employee.model.entity;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "department")
@Data
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer departmentId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "member")
    private Integer member;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "leader")
    private String leader;

}
