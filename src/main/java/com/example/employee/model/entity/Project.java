package com.example.employee.model.entity;

import lombok.Data;

import javax.persistence.*;


@Entity(name = "project")
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer projectId;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "name")
    private String name;

    @Column(name = "priority_level")
    private Integer priorityLevel;

    @Column(name = "man_day")
    private Integer manDay;

}
