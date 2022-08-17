package com.example.employee.common.enumerate;

public enum EEmployee {

    EMPLOYEE_ID("employeeId"),
    DEPARTMENT_ID("departmentId"),
    DEPARTMENT("department"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    DATE_OF_BIRTH("dateOfBirth"),
    ADDRESS("address"),
    EMAIL("email"),
    PHONE_NUMBER("phoneNumber")
    ;

    private final String value;

    EEmployee(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
