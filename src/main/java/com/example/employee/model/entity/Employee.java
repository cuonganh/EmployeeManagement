package com.example.employee.model.entity;

import com.example.employee.model.payload.EmployeeRequest;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "employee")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private Date dateOfBirth;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;


    public Employee getCreateEmployee(EmployeeRequest employeeRequest, long departmentId, long projectId) {

        this.departmentId = employeeRequest.getDepartmentId();
        this.projectId = employeeRequest.getProjectId();
        this.firstName = employeeRequest.getFirstName();
        this.lastName = employeeRequest.getLastName();
        this.dateOfBirth = employeeRequest.getDateOfBirth();
        this.address = employeeRequest.getAddress();
        this.email = employeeRequest.getEmail();
        this.phoneNumber = employeeRequest.getPhoneNumber();

        return this;

    }

    public void getUpdateEmployee(EmployeeRequest employeeRequest, long departmentId, long projectId){

        if(employeeRequest.getDepartmentId() != null) this.departmentId = employeeRequest.getDepartmentId();

        if(employeeRequest.getProjectId()!= null) this.projectId = employeeRequest.getProjectId();

        if(employeeRequest.getFirstName()!=null) this.firstName = employeeRequest.getFirstName();

        if(employeeRequest.getLastName()!=null) this.lastName = employeeRequest.getLastName();

        if(employeeRequest.getDateOfBirth()!=null) this.dateOfBirth = employeeRequest.getDateOfBirth();

        if(employeeRequest.getAddress()!=null) this.address = employeeRequest.getAddress();

        if(employeeRequest.getEmail()!=null) this.email = employeeRequest.getEmail();

        if(employeeRequest.getPhoneNumber()!=null) this.phoneNumber = employeeRequest.getPhoneNumber();

    }


}
