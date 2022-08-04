package com.example.employee.service;

import com.example.employee.common.Constant;
import com.example.employee.common.converter.EmployeeDtoConverter;
import com.example.employee.model.dto.EmployeeDto;
import com.example.employee.model.dto.PageDto;
import com.example.employee.model.entity.Employee;
import com.example.employee.model.payload.EmployeeResponse;
import com.example.employee.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    EmployeeDtoConverter employeeDtoConverter;

    private final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

    public EmployeeResponse<Employee> getEmployee(Long employeeId) {

        LOGGER.info(Constant.START);
        LOGGER.info("Get employee by id: " + employeeId);

        //need join to convert value for some fields
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (!employee.isPresent()) {
            return new EmployeeResponse<>(404, "Resource not found");
        }

        LOGGER.info(Constant.END);
        return new EmployeeResponse<>(200, "Found Employee", employee.get());

    }

    public PageDto<EmployeeDto> getEmployees(
            Long departmentId,
            Long projectId,
            Integer limit,
            Integer offset,
            String sort,
            List<String> sortBy
    ) {
        LOGGER.info(Constant.START);
        LOGGER.info("Get employees list");
        PageRequest pageEmployeeRequest;
        Sort.Direction direction = Sort.Direction.DESC;

        if(limit == null){
            limit = 10;
        }
        if(offset == null){
            offset = 0;
        }else if(offset == 0){
            //resource not found
        }
        if(sort != null){
            if(sort.equalsIgnoreCase("asc")){
                direction = Sort.Direction.ASC;
            }
        }
        if(CollectionUtils.isEmpty(sortBy)){
            sortBy = new ArrayList<>();
            sortBy.add("departmentId");
        }

        pageEmployeeRequest = PageRequest.of(offset, limit, direction, sortBy.toArray(new String[0]));

        List<Employee> employees = employeeRepository.getEmployees(
                entityManager,
                departmentId,
                projectId,
                limit,
                offset,
                sort,
                sortBy
        );

        Integer countEmployees = employeeRepository.findAll().size();

        Page<Employee> employeePageDtoPage = new PageImpl<>(
                employees,
                pageEmployeeRequest,
                countEmployees
        );

        LOGGER.info(Constant.END);
        return this.setEmployeePageDto(employeePageDtoPage);

    }


    private PageDto<EmployeeDto> setEmployeePageDto(Page<Employee> employeePage) {
        List<Employee> employees = employeePage.getContent();
        //check why employeeDto null while employees validated
        List<EmployeeDto> employeesDto = this.employeeDtoConverter.convertToDtos(employees);
        return new PageDto<>(employeePage, 200, "Found employees", employeesDto);
    }


}
