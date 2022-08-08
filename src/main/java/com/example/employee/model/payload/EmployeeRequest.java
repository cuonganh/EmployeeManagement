package com.example.employee.model.payload;

import com.example.employee.model.dto.ProjectInfo;
import com.example.employee.model.entity.Employee;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EmployeeRequest {

    private Long employeeId;

    private Long departmentId;

    private List<ProjectInfo> projects;

    private String firstName;

    private String lastName;

    private Date dateOfBirth;

    private String address;

    private String email;

    private String phoneNumber;

    public void validateValue() {
        // Validate the value of all fields

    }

    public Employee mappingPayload(EmployeeRequest employeeRequest) {
        Employee employee = new Employee();
        if(employeeRequest.getDepartmentId()!=null) employee.setDepartmentId(departmentId);
        if(employeeRequest.getFirstName()!=null) employee.setFirstName(firstName);
        if(employeeRequest.getLastName()!=null) employee.setLastName(lastName);
        if(employeeRequest.getDateOfBirth()!=null) employee.setDateOfBirth(dateOfBirth);
        if(employeeRequest.getAddress()!=null) employee.setAddress(address);
        if(employeeRequest.getEmail()!=null) employee.setEmail(email);
        if(employeeRequest.getPhoneNumber()!=null) employee.setPhoneNumber(phoneNumber);
        if(employeeRequest.getProjects()!=null) employee.setProjects(projects);
        return employee;
    }

}
