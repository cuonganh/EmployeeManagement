package com.example.employee.common.enumerate;

public enum EDepartment {

    DEPARTMENT_ID("departmentId"),
    DEPARTMENT_NAME("name"),
    DESCRIPTION("description"),
    MEMBERS("members"),
    LEADER("leader")
    ;

    private String value;

    EDepartment(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
