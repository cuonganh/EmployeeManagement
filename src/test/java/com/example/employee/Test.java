package com.example.employee;

import com.example.employee.model.dto.ProjectInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        String input = "1,2,3|5,6,7,8";
        List<ProjectInfo> result = new ArrayList<>();
        List<String> project = Arrays.asList(input.split("\\|"));
        for (String element : project) {
            ProjectInfo projectInfo = new ProjectInfo();
            List<String> properties = Arrays.asList(element.split(","));
            projectInfo.setProjectId(Long.valueOf(properties.get(0)));
            projectInfo.setProjectName(properties.get(1));
            projectInfo.setCustomer(properties.get(2));
            projectInfo.setManDay(Long.valueOf(properties.get(3)));
            result.add(projectInfo);
        }

        System.out.println(result);
    }
}
