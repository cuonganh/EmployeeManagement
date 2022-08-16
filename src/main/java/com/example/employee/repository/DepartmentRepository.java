package com.example.employee.repository;

import com.example.employee.model.dto.DepartmentBean;
import com.example.employee.model.entity.Departments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface DepartmentRepository extends JpaRepository<Departments, Long>, AbstractRepository {

    default List<DepartmentBean> getAllDepartmentBeen(
            EntityManager entityManager,
            Long member,
            String name,
            Integer limit,
            Integer offset,
            String sortType,
            List<String> sortBy
    ){
        StringBuilder sqlQuery = new StringBuilder("SELECT d.department_id, d.name, d.member, " +
                "d.description, d.leader");

        sqlQuery.append(createDepartmentQuery(member, name, sortType, sortBy));

        if(limit != null) {
            sqlQuery.append(" LIMIT ").append(limit);
        }
        if(offset != null){
            sqlQuery.append(" OFFSET ").append(offset);
        }

        javax.persistence.Query queryNative = entityManager.createNativeQuery(sqlQuery.toString());

        return (List<DepartmentBean>) queryNative
                .getResultStream()
                .map(DepartmentBean::new)
                .collect(Collectors.toList());
    }

    default Long countByCondition(
            EntityManager entityManager,
            Long member,
            String name,
            String sortType,
            List<String> sortBy
    ){

        javax.persistence.Query sqlNative = entityManager.createNativeQuery(
                "SELECT COUNT(department_id) " +
                        createDepartmentQuery(member, name, sortType, sortBy)
        );

        BigInteger total = (BigInteger) sqlNative.getSingleResult();

        return total.longValue();
    }

    default StringBuilder createDepartmentQuery(
            Long member,
            String name,
            String sortType,
            List<String> sortBy
    ){
        StringBuilder sqlQuery = new StringBuilder(" FROM department d WHERE 1 =1 ");
        if(name != null) {
            sqlQuery.append(" and name LIKE '%").append(name).append("%'");
        }
        if(member != null) {
            sqlQuery.append(" AND member = ").append(member);
        }
        sqlQuery.append(createSortAndOrderQuery(sortType, sortBy));

        return sqlQuery;
    }

}
