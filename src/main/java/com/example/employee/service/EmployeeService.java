package com.example.employee.service;

import com.example.employee.common.Constant;
import com.example.employee.common.converter.EmployeeDtoConverter;
import com.example.employee.model.dto.EmployeeBean;
import com.example.employee.model.dto.EmployeeDto;
import com.example.employee.model.dto.PageDto;
import com.example.employee.model.entity.Employee;
import com.example.employee.model.payload.EmployeeRequest;
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

    public EmployeeResponse<List<EmployeeBean>> getEmployeeBean(EntityManager entityManager, Long employeeId) {

        LOGGER.info(Constant.START);
        LOGGER.info("Get employee by id: " + employeeId);
        List<EmployeeBean> employeeBeen = employeeRepository.getEmployeeBeen(entityManager, employeeId);
        if(employeeBeen.get(0).getEmployeeId() == null) {
            return new EmployeeResponse<>(404, "Resource not found");
        }
        LOGGER.info(Constant.END);
        return new EmployeeResponse<>(200, "Found Employee", employeeBeen);

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
            sortBy.add("employeeId");
        }

        pageEmployeeRequest = PageRequest.of(offset, limit, direction, sortBy.toArray(new String[0]));
        List<EmployeeBean> employees = employeeRepository.getAllEmployeeBeen(entityManager, departmentId, projectId, sort, sortBy);

        Integer countEmployees = employeeRepository.findAll().size();

        Page<EmployeeBean> employeePageDtoPage = new PageImpl<>(
                employees,
                pageEmployeeRequest,
                countEmployees
        );

        LOGGER.info(Constant.END);
        return new PageDto<>(employeePageDtoPage, 200, "Found employees", employees);
    }


    public EmployeeResponse<Employee> addEmployee(EmployeeRequest employeeRequest) {

        //Todo: Validate value of employeeRequest (progressing)
        employeeRequest.validateValue();

        //Todo: Mapping payload to employee entity

        //Todo: save employee
//        employeeRepository.save(employeeRequest);

        //Todo: return response API spec
        return new EmployeeResponse<>(200, "Created employee");
    }

    public EmployeeResponse<Employee> deleteEmployee(Long employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if(!employee.isPresent()) {
            return new EmployeeResponse<>(404, "Employee not found");
        }
        employeeRepository.deleteById(employeeId);
        return new EmployeeResponse<>(200, "Deleted employee");
    }




}
