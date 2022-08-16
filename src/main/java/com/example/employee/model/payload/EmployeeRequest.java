package com.example.employee.model.payload;

import com.example.employee.common.Constant;
import com.example.employee.model.dto.ProjectInfo;
import com.example.employee.model.entity.Employees;
import lombok.Data;

import java.time.DateTimeException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class EmployeeRequest {

    private String employeeId;

    private String departmentId;

    private List<ProjectInfo> projects;

    private String firstName;

    private String lastName;

    private Date dateOfBirth;

    private String address;

    private String email;

    private String phoneNumber;


    public Employees convertToEmployeeEntity(EmployeeRequest employeeRequest) {
        try{
            Employees employee = new Employees();
            if(employeeRequest.getDepartmentId()!=null) employee.setDepartmentId(Long.valueOf(departmentId));
            if(employeeRequest.getFirstName()!=null) employee.setFirstName(firstName);
            if(employeeRequest.getLastName()!=null) employee.setLastName(lastName);
            if(employeeRequest.getDateOfBirth()!=null) employee.setDateOfBirth(dateOfBirth);
            if(employeeRequest.getAddress()!=null) employee.setAddress(address);
            if(employeeRequest.getEmail()!=null) employee.setEmail(email);
            if(employeeRequest.getPhoneNumber()!=null) employee.setPhoneNumber(phoneNumber);
            return employee;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Employees validateCreateEmployeeRequest(EmployeeRequest employeeRequest) {

        Employees employee = new Employees();

        validateDepartmentId(employeeRequest, employee);
        validateFirstName(employeeRequest, employee);
        validateLastName(employeeRequest, employee);
        validateDateOfBirth(employeeRequest, employee);
        validateAddress(employeeRequest, employee);
        validateEmail(employeeRequest, employee);
        validatePhoneNumber(employeeRequest, employee);

        return employee;
    }

    private void validateDepartmentId(EmployeeRequest employeeRequest, Employees employee) {
        if(isNumber(employeeRequest.getDepartmentId())) {
            employee.setDepartmentId(Long.valueOf(employeeRequest.getDepartmentId()));
        }
    }

    private void validateFirstName(EmployeeRequest employeeRequest, Employees employee) {

    }

    private void validateLastName(EmployeeRequest employeeRequest, Employees employee) {

    }

    private void validateDateOfBirth(EmployeeRequest employeeRequest, Employees employee) {

    }

    private void validateAddress(EmployeeRequest employeeRequest, Employees employee) {

    }

    private void validateEmail(EmployeeRequest employeeRequest, Employees employee) {

    }

    private void validatePhoneNumber(EmployeeRequest employeeRequest, Employees employee) {

    }



    private boolean isNumber(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDate(String date) {
        try{
            java.sql.Date.valueOf(date);
            return true;
        }catch(DateTimeException exception){
            return false;
        }
    }

    private boolean isValidRegex(String value) {
        boolean result = false;
        Pattern regexGMT = Pattern.compile(Constant.REGEX_EMAIL);
        Matcher matchFoundGMT = regexGMT.matcher(value);
        // isDate
        if (matchFoundGMT.find()) {
            result = true;
        }
        return result;
    }

}
