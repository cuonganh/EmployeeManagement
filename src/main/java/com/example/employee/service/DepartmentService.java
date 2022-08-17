package com.example.employee.service;

import com.example.employee.common.Constant;
import com.example.employee.model.dto.DepartmentBean;
import com.example.employee.model.dto.PageDto;
import com.example.employee.model.exception.ValidationException;
import com.example.employee.model.payload.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    private DepartmentRepository departmentRepository;


    private final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

    public PageDto<DepartmentBean> getDepartments(
            String members,
            String name,
            String limit,
            String offset,
            String sort,
            List<String> sortBy
    ) throws ValidationException {

        LOGGER.info(Constant.START);
        LOGGER.info("Get departments list");

        if(!isValidGetDepartmentsRequest(members, limit, offset)){
            throw new ValidationException(Collections.singletonList("Invalid request - number format"));
        }

        if(limit == null){
            limit = "10";
        }
        if(offset == null){
            offset = "0";
        }
        if(CollectionUtils.isEmpty(sortBy)){
            sortBy = new ArrayList<>();
            sortBy.add("department_id");
        }

        List<DepartmentBean> employees = departmentRepository.getAllDepartmentBeen(
                entityManager,
                members,
                name,
                limit,
                offset,
                sort,
                sortBy
        );
        Long total =departmentRepository.countByCondition(entityManager, members, name, sort, sortBy);
        LOGGER.info(Constant.END);
        return new PageDto<>(
                200,
                "Found employees",
                Integer.parseInt(limit),
                Integer.parseInt(offset),
                total,
                employees
        );

    }

    private boolean isValidGetDepartmentsRequest(
            String members,
            String limit,
            String offset
    ) {
        try {
            if(members != null) Long.valueOf(members);
            if(limit != null) Long.valueOf(limit);
            if(offset!= null) Long.valueOf(offset);
        }catch(NumberFormatException nfe) {
            return false;
        }

        return true;
    }


}
