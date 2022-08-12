package com.example.employee.repository;

import com.example.employee.model.dto.DepartmentBean;
import com.example.employee.model.dto.EmployeeBean;
import com.example.employee.model.entity.Departments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Departments, Long> {

    default List<DepartmentBean> getAllDepartmentBeen(
            EntityManager entityManager,
            Long member,
            String name,
            Integer limit,
            Integer offset,
            String sort,
            List<String> sortBy
    ){
        StringBuilder sqlQuery = new StringBuilder();

        return null;
    }

    default Long countByCondition(EntityManager entityManager, Long member, String name){

        return null;
    }

}
