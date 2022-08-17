package com.example.employee.model.payload;

import com.example.employee.common.Constant;
import com.example.employee.model.dto.ProjectInfo;
import com.example.employee.model.entity.Employees;
import com.example.employee.model.exception.ValidationException;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class EmployeeRequest {

    private String employeeId;

    private String departmentId;

    private List<ProjectInfo> projects;

    private String firstName;

    private String lastName;

    private String dateOfBirth;

    private String address;

    private String email;

    private String phoneNumber;


    public Employees convertToEmployeeEntity(EmployeeRequest employeeRequest) {
        try{
            Employees employee = new Employees();
            if(employeeRequest.getDepartmentId()!=null) employee.setDepartmentId(Long.valueOf(departmentId));
            if(employeeRequest.getFirstName()!=null) employee.setFirstName(firstName);
            if(employeeRequest.getLastName()!=null) employee.setLastName(lastName);
            if(employeeRequest.getDateOfBirth()!=null) employee.setDateOfBirth(LocalDate.parse(dateOfBirth));
            if(employeeRequest.getAddress()!=null) employee.setAddress(address);
            if(employeeRequest.getEmail()!=null) employee.setEmail(email);
            if(employeeRequest.getPhoneNumber()!=null) employee.setPhoneNumber(phoneNumber);
            return employee;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValidateCreateEmployeeRequest(EmployeeRequest employeeRequest) throws ValidationException {

        return validateDepartmentId(employeeRequest)
                && validateFirstName(employeeRequest)
                && validateLastName(employeeRequest)
                && validateDateOfBirth(employeeRequest)
                && validateAddress(employeeRequest)
                && validateEmail(employeeRequest)
                && validatePhoneNumber(employeeRequest)
                && validateProjects(employeeRequest)
                ;
    }

    public boolean isValidateUpdateEmployee(EmployeeRequest employeeRequest) throws ValidationException {

        if(employeeRequest.getDepartmentId() != null)  validateDepartmentId(employeeRequest);
        if(employeeRequest.getFirstName() != null)   validateFirstName(employeeRequest);
        if(employeeRequest.getLastName() != null)  validateLastName(employeeRequest);
        if(employeeRequest.getDateOfBirth() != null)  validateDateOfBirth(employeeRequest);
        if(employeeRequest.getAddress() != null)  validateAddress(employeeRequest);
        if(employeeRequest.getEmail() != null)  validateEmail(employeeRequest);
        if(employeeRequest.getPhoneNumber() != null)  validatePhoneNumber(employeeRequest);
        if(employeeRequest.getProjects()!= null)  validateProjects(employeeRequest);

        return true;
    }

    private boolean validateDepartmentId(EmployeeRequest employeeRequest) throws ValidationException {
        if(employeeRequest.getDepartmentId() == null || !isNumber(employeeRequest.getDepartmentId())) {
            throw new ValidationException(Collections.singletonList("Invalid departmentId"));
        }
        return true;
    }

    private boolean validateFirstName(EmployeeRequest employeeRequest) throws ValidationException {
        if(employeeRequest.getFirstName() == null) {
            throw new ValidationException(Collections.singletonList("FirstName is mandatory"));
        }
        return true;
    }

    private boolean validateLastName(EmployeeRequest employeeRequest) throws ValidationException {
        if(employeeRequest.getLastName() == null) {
            throw new ValidationException(Collections.singletonList("LastName is mandatory"));
        }
        return true;
    }

    private boolean validateDateOfBirth(EmployeeRequest employeeRequest) throws ValidationException {
        if(employeeRequest.getDateOfBirth() == null || !isDate(employeeRequest.getDateOfBirth())) {
            throw new ValidationException(Collections.singletonList("Invalid DateOfBirth. DateOfBirth should be format \"yyyy-MM-dd\""));
        }
        return true;
    }

    private boolean validateAddress(EmployeeRequest employeeRequest) throws ValidationException {
        if(employeeRequest.getAddress() == null) {
            throw new ValidationException(Collections.singletonList("Address is mandatory"));
        }
        return true;
    }

    private boolean validateEmail(EmployeeRequest employeeRequest) throws ValidationException {
        if(employeeRequest.getEmail() == null || !isValidRegex(employeeRequest.getEmail())) {
            throw new ValidationException(Collections.singletonList("Invalid email. Email should be a valid email address such as \"simple@example.com\""));
        }
        return true;
    }

    private boolean validatePhoneNumber(EmployeeRequest employeeRequest) throws ValidationException {
        if(employeeRequest.getPhoneNumber() == null) {
            throw new ValidationException(Collections.singletonList("PhoneNumber is mandatory"));
        }
        return true;
    }

    public boolean validateProjects(EmployeeRequest employeeRequest) throws ValidationException {
        Optional<List<ProjectInfo>> projects = Optional.ofNullable(employeeRequest.getProjects());

        if(projects.isPresent() && projects.get().size() > 0) {
            for(ProjectInfo projectInfo : projects.get()) {
                if(projectInfo.getProjectId() == null || !isNumber(projectInfo.getProjectId())) {
                    throw new ValidationException(Collections.singletonList("ProjectId is invalid"));
                }
            }
        }
        return true;
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
            LocalDate.parse(date);
            return true;
        }catch(DateTimeParseException exception){
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
