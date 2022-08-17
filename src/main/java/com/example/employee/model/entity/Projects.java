package com.example.employee.model.entity;

import com.example.employee.common.enumerate.EPriorityLevel;
import lombok.Data;

import javax.persistence.*;


@Entity(name = "project")
@Data
public class Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "name")
    private String name;

    @Column(name = "priority_level")
    private String priorityLevel;

    @Column(name = "man_day")
    private Long manDay;

}
