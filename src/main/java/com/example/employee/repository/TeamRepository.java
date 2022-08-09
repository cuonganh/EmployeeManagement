package com.example.employee.repository;

import com.example.employee.model.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByEmployeeIdAndProjectId(Long employeeId, Long projectId);


}
