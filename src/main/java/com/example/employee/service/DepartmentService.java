package com.example.employee.service;

import com.example.employee.common.Constant;
import com.example.employee.model.dto.DepartmentBean;
import com.example.employee.model.dto.DepartmentDto;
import com.example.employee.model.dto.EmployeeBean;
import com.example.employee.model.dto.PageDto;
import com.example.employee.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    private DepartmentRepository departmentRepository;



    private final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

    public PageDto<DepartmentDto> getDepartments(
            Long member,
            String name,
            Integer limit,
            Integer offset,
            String sort,
            List<String> sortBy
    ) {

        LOGGER.info(Constant.START);
        LOGGER.info("Get departments list");
        Sort.Direction direction = Sort.Direction.DESC;

        if(limit == null){
            limit = 10;
        }
        if(offset == null){
            offset = 0;
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

        List<DepartmentBean> employees = departmentRepository.getAllDepartmentBeen(
                entityManager,
                member,
                name,
                limit,
                offset,
                sort,
                sortBy
        );
        Long total =departmentRepository.countByCondition(entityManager, member, name);
        LOGGER.info(Constant.END);
        return new PageDto<>(200, "Found employees", limit, offset, total, employees);
    }


}
