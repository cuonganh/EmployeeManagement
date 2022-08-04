package com.example.employee.model.entity;

import lombok.Data;

import javax.persistence.*;


@Entity(name = "project")
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "priority_level", nullable = false)
    private Integer priorityLevel;

    @Column(name = "man_day", nullable = false)
    private Integer manDay;

}
