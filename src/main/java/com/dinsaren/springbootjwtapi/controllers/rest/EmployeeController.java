package com.dinsaren.springbootjwtapi.controllers.rest;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.res.MessageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateEmployeeRequest;
import com.dinsaren.springbootjwtapi.payload.request.UpdateEmployeeRequest;
import com.dinsaren.springbootjwtapi.payload.response.EmployeeResponse;
import com.dinsaren.springbootjwtapi.services.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping({"/api/app/employees", "/api/app/employee"})
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Employee", description = "Employee management REST APIs")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "List all employees")
    @GetMapping({"", "/list"})
    public ResponseEntity<MessageRes> getAllEmployees() {
        MessageRes res = new MessageRes();
        try {
            List<EmployeeResponse> list = employeeService.getAllEmployees();
            res.setSuccess(list);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            log.error("Error retrieving employees", e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "List all employees (POST alias)")
    @PostMapping("/list")
    public ResponseEntity<MessageRes> getAllEmployeesPost() {
        return getAllEmployees();
    }

    @Operation(summary = "Get employee by ID")
    @GetMapping("/{id}")
    public ResponseEntity<MessageRes> getEmployeeById(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            EmployeeResponse data = employeeService.getEmployeeById(id);
            res.setSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error retrieving employee " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Create employee")
    @PostMapping({"", "/create"})
    public ResponseEntity<MessageRes> createEmployee(@Valid @RequestBody CreateEmployeeRequest req) {
        MessageRes res = new MessageRes();
        try {
            EmployeeResponse data = employeeService.createEmployee(req);
            res.setCreateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error creating employee", e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Update employee")
    @PutMapping("/{id}")
    public ResponseEntity<MessageRes> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest req
    ) {
        MessageRes res = new MessageRes();
        try {
            EmployeeResponse data = employeeService.updateEmployee(id, req);
            res.setUpdateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error updating employee " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Delete employee")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageRes> deleteEmployee(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            employeeService.deleteEmployee(id);
            res.setSuccess("Employee deleted successfully");
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error deleting employee " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
}

