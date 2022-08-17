package com.example.employee.controller;

import com.example.employee.model.exception.ResourceNotFoundException;
import com.example.employee.model.exception.ValidationException;
import com.example.employee.model.payload.EmployeeRequest;
import com.example.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EntityManager entityManager;

    @GetMapping("/{employeeId}")
    public ResponseEntity<?> getEmployee(
            @PathVariable("employeeId") String employeeId)
            throws ResourceNotFoundException, ValidationException {
        return ResponseEntity.ok(employeeService.getEmployee(entityManager, String.valueOf(employeeId)));
    }

    @GetMapping("")
    public ResponseEntity<?> getEmployees(
            @RequestParam(value = "departmentId", required = false) String departmentId,
            @RequestParam(value = "projectId", required = false) String projectId,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "offset", required = false) String offset,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "sortBy", required = false) List<String> sortBy
    ) throws ValidationException {
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
            @RequestBody EmployeeRequest employeeRequest) throws ValidationException {
        return ResponseEntity.ok(employeeService.createEmployee(employeeRequest));
    }

    @PatchMapping(value = "/{employeeId}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable("employeeId") String employeeId,
            @RequestBody EmployeeRequest employeeRequest) throws ResourceNotFoundException, ValidationException {
        return ResponseEntity.ok(employeeService.updateEmployee(employeeRequest, employeeId));
    }


    @DeleteMapping("/{employeeId}")
    public ResponseEntity<?> deleteEmployee(
            @PathVariable("employeeId") String employeeId)
            throws ResourceNotFoundException, ValidationException {
        return ResponseEntity.ok(employeeService.deleteEmployee(employeeId));
    }


    @PostMapping("/import")
    public ResponseEntity<?> importEmployees(
            @RequestParam(value = "importFile", required = false) MultipartFile importFile
    ) {
        if(employeeService.hasCSVFormat(importFile)){
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

    @GetMapping("/export")
    public ResponseEntity<?> exportEmployees(
            @RequestParam(value = "departmentId", required = false) String departmentId,
            @RequestParam(value = "projectId", required = false) String projectId,
            @RequestParam(value = "exportFields", required = false) String[] exportFields,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "offset", required = false) String offset,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "sortBy", required = false) List<String> sortBy
    ) throws FileNotFoundException, ValidationException {
        return ResponseEntity.ok(
                employeeService.exportEmployees(
                        departmentId,
                        projectId,
                        exportFields,
                        limit,
                        offset,
                        sort,
                        sortBy
                )
        );

    }


}
