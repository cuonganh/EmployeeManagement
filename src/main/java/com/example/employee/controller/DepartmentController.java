package com.example.employee.controller;

import com.example.employee.model.exception.ValidationException;
import com.example.employee.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("")
    public ResponseEntity<?> getDepartments(
            @RequestParam(value = "members", required = false) String members,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "offset", required = false) String offset,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "sortBy", required = false) List<String> sortBy
    ) throws ValidationException {
        return ResponseEntity.ok(
                departmentService.getDepartments(
                        members,
                        name,
                        limit,
                        offset,
                        sort,
                        sortBy
                )
        );
    }


}
