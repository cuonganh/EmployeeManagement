package com.example.employee.repository;

import com.example.employee.model.entity.Teams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Teams, Long> {

    Optional<Teams> findByEmployeeIdAndProjectId(Long employeeId, Long projectId);


}
