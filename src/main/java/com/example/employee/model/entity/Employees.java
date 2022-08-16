package com.example.employee.model.entity;

import com.example.employee.model.payload.EmployeeRequest;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "employee")
@Data
public class Employees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "address")
    private String address;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    public Employees(){

    }

    public Employees(
            Long departmentId,
            String firstName,
            String lastName,
            Date dateOfBirth,
            String address,
            String email,
            String phoneNumber
    ) {
        this.departmentId = departmentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }


    public Employees getUpdateEmployee(EmployeeRequest employeeRequest){

        if(employeeRequest.getDepartmentId() != null) this.departmentId = Long.valueOf(employeeRequest.getDepartmentId());

        if(employeeRequest.getFirstName()!=null) this.firstName = employeeRequest.getFirstName();

        if(employeeRequest.getLastName()!=null) this.lastName = employeeRequest.getLastName();

        if(employeeRequest.getDateOfBirth()!=null) this.dateOfBirth = employeeRequest.getDateOfBirth();

        if(employeeRequest.getAddress()!=null) this.address = employeeRequest.getAddress();

        if(employeeRequest.getEmail()!=null) this.email = employeeRequest.getEmail();

        if(employeeRequest.getPhoneNumber()!=null) this.phoneNumber = employeeRequest.getPhoneNumber();

        return this;
    }



}
