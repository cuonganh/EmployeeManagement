package com.example.employee.model.dto;

import com.example.employee.common.enumerate.EPriorityLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectInfo {

    Long projectId;

    String projectName;

    String customer;

    Long manDay;

    EPriorityLevel priorityLevel;

}
