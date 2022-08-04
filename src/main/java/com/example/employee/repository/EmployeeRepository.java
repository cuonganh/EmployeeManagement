package com.example.employee.repository;

import com.example.employee.model.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    default List<Employee> getEmployees(
            EntityManager entityManager,
            Long departmentId,
            Long projectId,
            Integer limit,
            Integer offset,
            String sortType,
            List<String> sortBy
    ){
        StringBuilder sqlWhere = new StringBuilder();
        Map<String, Object> params = new HashMap<>();

        if(departmentId != null) {
            sqlWhere.append(" and department_id = :departmentId");
            params.put("departmentId", departmentId);
        }

        if(projectId != null) {
            sqlWhere.append(" and project_id = :projectId");
            params.put("projectId", projectId);
        }

        if(sortType != null && sortType.equalsIgnoreCase("desc")){
            sortType = "desc";
        }else{
            sortType = "asc";
        }

        if(!CollectionUtils.isEmpty(sortBy)){
            sqlWhere.append(" order by ");
            for(int i = 0; i < sortBy.size(); i++){
                if(i!= 0){
                    sqlWhere.append(", ");
                }
                sqlWhere.append(sortBy.get(i)).append(" ").append(sortType);
            }
        }

        String sqlQuery = "select ee from employee ee where 1=1 " + sqlWhere;

        javax.persistence.Query query = entityManager.createQuery(sqlQuery, Employee.class);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        params.forEach(query::setParameter);

        return query.getResultList();
    }

}
