package com.example.employee.common.converter;

import com.example.employee.model.dto.EmployeeDto;
import com.example.employee.model.entity.Employees;
import org.springframework.stereotype.Component;

@Component
public class EmployeeDtoConverter implements Converter<Employees, EmployeeDto>{

    @Override
    public Employees convertToEntity(EmployeeDto dto){
        return null;
    }

    @Override
    public EmployeeDto convertToDto(Employees entity) {

        EmployeeDto employeeDto = new EmployeeDto();

        employeeDto.setEmployeeId(employeeDto.getEmployeeId());
        employeeDto.setDepartmentId(employeeDto.getDepartmentId());
        employeeDto.setFirstName(employeeDto.getFirstName());
        employeeDto.setDateOfBirth(employeeDto.getDateOfBirth());
        employeeDto.setLastName(employeeDto.getLastName());
        employeeDto.setEmail(employeeDto.getEmail());
        employeeDto.setPhoneNumber(employeeDto.getPhoneNumber());

        return employeeDto;

    }

}
