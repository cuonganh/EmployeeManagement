package com.example.employee.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
public class EmployeeBean {

    String employeeId;
    String department;
    String firstName;
    String lastName;
    Date dateOfBirth;
    String address;
    String email;
    String phoneNumber;
    List<ProjectInfo> projects;

    public EmployeeBean(Object input){

        Object[] fields = (Object[]) input;

        if(fields[0] != null){
            employeeId = fields[0].toString();
        }
        if(fields[1] != null){
            department = fields[1].toString();
        }
        if(fields[2] != null){
            firstName = fields[2].toString();
        }
        if(fields[3] != null){
            lastName = fields[3].toString();
        }
        if(fields[4] != null){
            dateOfBirth = (Date) fields[4];
        }
        if(fields[5] != null){
            address = fields[5].toString();
        }
        if(fields[6] != null){
            email = fields[6].toString();
        }
        if(fields[7]!= null){
            phoneNumber = fields[7].toString();
        }
        if(fields[8]!= null){
            List<String> listProjects = Arrays.asList(fields[8].toString().split("\\|"));
            projects = new ArrayList<>();
            for (String element : listProjects) {
                ProjectInfo projectInfo = new ProjectInfo();
                List<String> properties = Arrays.asList(element.split(","));
                if(properties.size() == 4){
                    projectInfo.setProjectId(Long.valueOf(properties.get(0)));
                    projectInfo.setProjectName(properties.get(1));
                    projectInfo.setCustomer(properties.get(2));
                    projectInfo.setManDay(Long.valueOf(properties.get(3)));
                    projects.add(projectInfo);
                }
            }
        }
    }

}
