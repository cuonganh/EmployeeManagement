package com.example.employee.controller;

import com.example.employee.model.entity.Employee;
import com.example.employee.model.payload.EmployeeRequest;
import com.example.employee.model.payload.EmployeeResponse;
import com.example.employee.repository.EmployeeRepository;
import com.example.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@RestController
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EntityManager entityManager;

    @GetMapping("/{employeeId}")
    public ResponseEntity<?> getEmployee(@PathVariable("employeeId") Long employeeId) {
        return ResponseEntity.ok(employeeService.getEmployeeBean(entityManager, employeeId));
    }

    @GetMapping("")
    public ResponseEntity<?> getEmployees(
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "projectId", required = false) Long projectId,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "sortBy", required = false) List<String> sortBy
    ) {
        return ResponseEntity.ok(employeeService.getEmployees(
                departmentId,
                projectId,
                limit,
                offset,
                sort,
                sortBy
                )
        );

    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeRequest employeeRequest) {

        return ResponseEntity.ok(employeeService.createEmployee(employeeRequest));
    }

    @PatchMapping(value = "/{employeeId}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable("employeeId") Long employeeId,
            @RequestBody EmployeeRequest employeeRequest) throws Exception {
        return ResponseEntity.ok(employeeService.updateEmployee(employeeRequest, employeeId));
    }


    @DeleteMapping("/{employeeId}")
    public ResponseEntity<?> deleteEmployee(@PathVariable("employeeId") Long employeeId) {
        //need join other table to convert value
        return ResponseEntity.ok(employeeService.deleteEmployee(employeeId));
    }


}
