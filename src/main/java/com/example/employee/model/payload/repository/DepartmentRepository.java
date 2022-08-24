package com.example.employee.model.payload.repository;

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
            String members,
            String name,
            String limit,
            String offset,
            String sortType,
            List<String> sortBy
    ){
        StringBuilder sqlQuery = new StringBuilder("SELECT d.department_id, d.name, d.members, " +
                "d.description, d.leader");

        sqlQuery.append(createDepartmentQuery(members, name, sortType, sortBy));

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
            String members,
            String name,
            String sortType,
            List<String> sortBy
    ){

        javax.persistence.Query sqlNative = entityManager.createNativeQuery(
                "SELECT COUNT(department_id) " +
                        createDepartmentQuery(members, name, sortType, sortBy)
        );

        BigInteger total = (BigInteger) sqlNative.getSingleResult();

        return total.longValue();
    }

    default StringBuilder createDepartmentQuery(
            String members,
            String name,
            String sortType,
            List<String> sortBy
    ){
        StringBuilder sqlQuery = new StringBuilder(" FROM department d WHERE 1 =1 ");
        if(name != null) {
            sqlQuery.append(" and name LIKE '%").append(name).append("%'");
        }
        if(members != null) {
            sqlQuery.append(" AND members = ").append(members);
        }
        sqlQuery.append(createSortAndOrderQuery(sortType, sortBy));

        return sqlQuery;
    }

}
