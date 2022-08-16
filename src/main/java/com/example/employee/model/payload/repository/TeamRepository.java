package com.example.employee.model.payload.repository;

import com.example.employee.model.entity.Teams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Teams, Long> {

    void deleteByEmployeeId(Long employeeId);

}
