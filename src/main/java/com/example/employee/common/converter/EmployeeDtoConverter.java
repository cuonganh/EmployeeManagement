package com.example.employee.common.converter;

import com.example.employee.model.dto.EmployeeDto;
import com.example.employee.model.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeDtoConverter implements Converter<Employee, EmployeeDto>{

    @Override
    public Employee convertToEntity(EmployeeDto dto){
        return null;
    }

    @Override
    public EmployeeDto convertToDto(Employee entity) {

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
