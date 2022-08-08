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
            Integer offset,
            Integer limit,
            String sortType,
            List<String> sortBy
    ){
        String sqlJoin = "select" +
                " e.employee_id as employeeId, d.name as department," +
                " e.first_name as firstName, e.last_name as lastName," +
                " e.date_of_birth as dateOfBirth, e.address as address," +
                " e.email as email, e.phone_number as phoneNumber," +
                " GROUP_CONCAT(CONCAT(p.project_id,',', p.name,',', c.name,',', p.man_day) separator '|')  as projects" +
                " from employee e " +
                " inner join team t on t.employee_id = e.employee_id " +
                " inner JOIN project p on p.project_id = t.project_id " +
                " inner JOIN department d on d.department_id = e.department_id " +
                " inner join customer c on c.customer_id = p.customer_id"
                ;

        StringBuilder sqlWhere = new StringBuilder(" Where 1 = 1 ");
        if(departmentId != null) {
            sqlWhere.append(" AND d.department_id = " + departmentId);
        }
        if(projectId != null) {
            sqlWhere.append(" AND p.project_id = " + projectId);
        }

        if(sortType != null && sortType.equalsIgnoreCase("desc")) {
            sortType = " desc";
        }else{
            sortType = " asc";
        }
        StringBuilder sqlOrder = new StringBuilder(" group by e.employee_id ");
        if(sortBy != null) {
            sqlOrder.append(" order by ");
            for (int i = 0; i < sortBy.size(); i++) {
                if(i != 0) {
                    sqlOrder.append(", ");
                }
                sqlOrder.append(sortBy.get(i) + " " + sortType);
            }
        }
        sqlOrder.append(" limit " + limit);
        sqlOrder.append(" offset " + offset);

        String sqlQuery = sqlJoin + sqlWhere + sqlOrder;
        System.out.println(sqlQuery);
        javax.persistence.Query queryNative = entityManager.createNativeQuery(sqlQuery);

        return (List<EmployeeBean>) queryNative.getResultStream().map(e -> new EmployeeBean(e)).collect(Collectors.toList());
    }

}
