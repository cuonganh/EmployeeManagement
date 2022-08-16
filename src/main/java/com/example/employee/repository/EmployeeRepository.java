package com.example.employee.repository;

import com.example.employee.model.dto.EmployeeBean;
import com.example.employee.model.entity.Employees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface EmployeeRepository extends JpaRepository<Employees, Long>, AbstractRepository {


    Optional<Employees> findByEmail(String email);

    default List<EmployeeBean> getEmployeeBeen(EntityManager entityManager, Long employeeId){

        String sqlQuery = "select " +
                " e.employee_id as employeeId, d.name as department," +
                " e.first_name as firstName, e.last_name as lastName," +
                " e.date_of_birth as dateOfBirth, e.address as address," +
                " e.email as email, e.phone_number as phoneNumber," +
                " GROUP_CONCAT(CONCAT(p.project_id,',', p.name,',', c.name,',', p.man_day) separator '|')  as projects" +
                " from employee e " +
                " inner join team t on t.employee_id = e.employee_id " +
                " inner JOIN project p on p.project_id = t.project_id " +
                " inner JOIN department d on d.department_id = e.department_id " +
                " inner join customer c on c.customer_id = p.customer_id" +
                " where e.employee_id = " + employeeId;
        javax.persistence.Query queryNative = entityManager.createNativeQuery(sqlQuery);

        return (List<EmployeeBean>) queryNative.getResultStream().map(e -> new EmployeeBean(e)).collect(Collectors.toList());
    }

    default List<EmployeeBean> getAllEmployeeBeen(
            EntityManager entityManager,
            Long departmentId,
            Long projectId,
            Integer limit,
            Integer offset,
            String sortType,
            List<String> sortBy
    ){
        StringBuilder sqlQuery = new StringBuilder("SELECT" +
                " e.employee_id as employeeId, d.name as department," +
                " e.first_name as firstName, e.last_name as lastName," +
                " e.date_of_birth as dateOfBirth, e.address as address," +
                " e.email as email, e.phone_number as phoneNumber," +
                " GROUP_CONCAT(CONCAT(p.project_id,',', p.name,',', c.name,',', p.man_day) separator '|')  as projects" +
                " FROM employee e " +
                " LEFT JOIN team t on t.employee_id = e.employee_id " +
                " LEFT JOIN project p on p.project_id = t.project_id " +
                " LEFT JOIN department d on d.department_id = e.department_id " +
                " LEFT JOIN customer c on c.customer_id = p.customer_id" +
                " WHERE 1 = 1 ")
                ;
        if(departmentId != null) {
            sqlQuery.append(" and d.department_id = ").append(departmentId);
        }
        if(projectId != null) {
            sqlQuery.append(" and p.project_id = ").append(projectId);
        }
        sqlQuery.append(" GROUP BY e.employee_id");

        sqlQuery.append(createSortAndOrderQuery(sortType, sortBy));

        sqlQuery.append(" LIMIT ").append(limit);
        sqlQuery.append(" OFFSET ").append(offset);

        javax.persistence.Query queryNative = entityManager.createNativeQuery(sqlQuery.toString());

        return (List<EmployeeBean>) queryNative
                .getResultStream()
                .map(EmployeeBean::new)
                .collect(Collectors.toList());
    }

    default Long countByCondition(EntityManager entityManager, Long departmentId, Long projectId){

        StringBuilder sql = new StringBuilder("SELECT count(distinct e.employee_id)" +
                " FROM employee e" +
                " LEFT JOIN team t on t.employee_id = e.employee_id " +
                " LEFT JOIN project p on p.project_id = t.project_id " +
                " LEFT JOIN department d on d.department_id = e.department_id " +
                " LEFT JOIN customer c on c.customer_id = p.customer_id" +
                " WHERE 1 = 1 ");
        if(departmentId != null){
            sql.append(" and e.department_id = ").append(departmentId);
        }
        if(projectId != null){
            sql.append(" and t.project_id = ").append(projectId);
        }

        javax.persistence.Query sqlNative = entityManager.createNativeQuery(sql.toString());

        BigInteger total = (BigInteger) sqlNative.getSingleResult();

        return total.longValue();
    }

}
