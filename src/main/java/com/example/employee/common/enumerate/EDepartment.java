package com.example.employee.common.enumerate;

public enum EDepartment {

    DEPARTMENT_ID("departmentId"),
    DEPARTMENT_NAME("name"),
    DESCRIPTION("description"),
    MEMBERS("members"),
    LEADER("leader")
    ;

    private final String value;

    EDepartment(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EDepartment getByValue(String value) {
        for (EDepartment eDepartment : EDepartment.values()) {
            if (eDepartment.getValue().equalsIgnoreCase(value)) {
                return eDepartment;
            }
        }
        return null;
    }

}
