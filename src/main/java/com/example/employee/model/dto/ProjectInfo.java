package com.example.employee.model.dto;

import lombok.Data;

@Data
public class ProjectInfo {
    Long projectId;
    String projectName;
    String customer;
    Long manDay;
}
