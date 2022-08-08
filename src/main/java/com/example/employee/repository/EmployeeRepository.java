package com.example.employee.repository;

import com.example.employee.model.dto.EmployeeBean;
import com.example.employee.model.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {


    default List<EmployeeBean> getAllEmployeeBeen(EntityManager entityManager){

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
                " group by e.employee_id ";
        javax.persistence.Query queryNaitive = entityManager.createNativeQuery(sqlQuery);

        return (List<EmployeeBean>) queryNaitive.getResultStream().map(e -> new EmployeeBean(e)).collect(Collectors.toList());
    }

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
        javax.persistence.Query queryNaitive = entityManager.createNativeQuery(sqlQuery);

        return (List<EmployeeBean>) queryNaitive.getResultStream().map(e -> new EmployeeBean(e)).collect(Collectors.toList());
    }

}
