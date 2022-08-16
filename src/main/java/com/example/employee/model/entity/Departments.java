package com.example.employee.model.entity;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "department")
@Data
public class Departments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;

    @Column(name = "name")
    private String name;

    @Column(name = "members")
    private Long members;

    @Column(name = "description")
    private String description;

    @Column(name = "leader")
    private String leader;

}
