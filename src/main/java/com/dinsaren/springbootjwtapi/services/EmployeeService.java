package com.dinsaren.springbootjwtapi.services;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.payload.request.CreateEmployeeRequest;
import com.dinsaren.springbootjwtapi.payload.request.UpdateEmployeeRequest;
import com.dinsaren.springbootjwtapi.payload.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {
    List<EmployeeResponse> getAllEmployees();
    EmployeeResponse getEmployeeById(Long id) throws AppException;
    EmployeeResponse createEmployee(CreateEmployeeRequest req) throws AppException;
    EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest req) throws AppException;
    void deleteEmployee(Long id) throws AppException;
}

