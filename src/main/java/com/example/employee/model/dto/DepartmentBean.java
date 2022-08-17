package com.example.employee.model.dto;

import lombok.Data;

@Data
public class DepartmentBean {

    private String departmentId;

    private String name;

    private String members;

    private String description;

    private String leader;

    public DepartmentBean(Object input){

        Object[] fields = (Object[]) input;

        if(fields[0] != null){
            departmentId = fields[0].toString();
        }
        if(fields[1] != null){
            name = fields[1].toString();
        }
        if(fields[2] != null){
            members = fields[2].toString();
        }
        if(fields[3] != null){
            description = fields[3].toString();
        }
        if(fields[4] != null){
            leader = fields[4].toString();
        }
    }

}
