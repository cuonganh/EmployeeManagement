package com.example.employee.controller;

import com.example.employee.common.Constant;
import com.example.employee.helper.CSVHelper;
import com.example.employee.model.exception.ResourceNotFoundException;
import com.example.employee.model.payload.EmployeeRequest;
import com.example.employee.repository.EmployeeRepository;
import com.example.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.File;
import java.util.List;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

@RestController
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EntityManager entityManager;

    @GetMapping("/{employeeId}")
    public ResponseEntity<?> getEmployee(
            @PathVariable("employeeId") Long employeeId)
            throws ResourceNotFoundException {
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
        return ResponseEntity.ok(
                employeeService.getEmployees(
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
    public ResponseEntity<?> createEmployee(
            @RequestBody EmployeeRequest employeeRequest) {
        return ResponseEntity.ok(employeeService.createEmployee(employeeRequest));
    }

    @PatchMapping(value = "/{employeeId}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable("employeeId") Long employeeId,
            @RequestBody EmployeeRequest employeeRequest) {
        return ResponseEntity.ok(employeeService.updateEmployee(employeeRequest, employeeId));
    }


    @DeleteMapping("/{employeeId}")
    public ResponseEntity<?> deleteEmployee(
            @PathVariable("employeeId") Long employeeId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok(employeeService.deleteEmployee(employeeId));
    }


    @PostMapping("/import")
    public ResponseEntity<?> importEmployees(
            @RequestParam(value = "importFile", required = false) MultipartFile importFile
    ) {
        if(CSVHelper.hasCSVFormat(importFile)){
            try {
                return ResponseEntity.ok(employeeService.importEmployees(importFile));
            }catch (Exception e) {
                String message = "Could not import this file: " + importFile.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
            }
        }
        String message = "Please upload a csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }


}
