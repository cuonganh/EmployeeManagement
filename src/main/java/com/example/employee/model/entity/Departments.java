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

    @Column(name = "member")
    private Long member;

    @Column(name = "description")
    private String description;

    @Column(name = "leader")
    private String leader;

}
