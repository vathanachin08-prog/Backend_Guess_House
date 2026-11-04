package com.dinsaren.springbootjwtapi.services.impl;

import com.dinsaren.springbootjwtapi.constants.Constants;
import com.dinsaren.springbootjwtapi.constants.ErrorCode;
import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.Employee;
import com.dinsaren.springbootjwtapi.payload.request.CreateEmployeeRequest;
import com.dinsaren.springbootjwtapi.payload.request.UpdateEmployeeRequest;
import com.dinsaren.springbootjwtapi.payload.response.EmployeeResponse;
import com.dinsaren.springbootjwtapi.repository.EmployeeRepository;
import com.dinsaren.springbootjwtapi.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAllByOrderByIdDesc()
                .stream()
                .map(EmployeeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) throws AppException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Employee not found with id: " + id));
        return EmployeeResponse.fromEntity(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest req) throws AppException {
        String email = req.getEmail().trim().toLowerCase();
        if (employeeRepository.existsByEmail(email)) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, "Employee email already exists: " + email);
        }

        Employee employee = Employee.builder()
                .name(req.getName().trim())
                .email(email)
                .phone(req.getPhone().trim())
                .position(req.getPosition().trim())
                .department(req.getDepartment().trim())
                .salary(req.getSalary())
                .status(Constants.STATUS_ACTIVE)
                .profileImage(req.getProfileImage())
                .build();
        employee.setCreateAt(new Date());
        employee.setCreateBy(Constants.SYSTEM);

        Employee saved = employeeRepository.save(employee);
        log.info("Created new employee: id={}, name={}", saved.getId(), saved.getName());
        return EmployeeResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest req) throws AppException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Employee not found with id: " + id));

        String email = req.getEmail().trim().toLowerCase();
        if (employeeRepository.existsByEmailAndIdNot(email, id)) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, "Employee email already exists: " + email);
        }

        employee.setName(req.getName().trim());
        employee.setEmail(email);
        employee.setPhone(req.getPhone().trim());
        employee.setPosition(req.getPosition().trim());
        employee.setDepartment(req.getDepartment().trim());
        employee.setSalary(req.getSalary());
        if (req.getStatus() != null && !req.getStatus().trim().isEmpty()) {
            employee.setStatus(req.getStatus().trim());
        }
        if (req.getProfileImage() != null) {
            employee.setProfileImage(req.getProfileImage());
        }
        employee.setUpdateAt(new Date());
        employee.setUpdateBy(Constants.SYSTEM);

        Employee saved = employeeRepository.save(employee);
        log.info("Updated employee: id={}, name={}", saved.getId(), saved.getName());
        return EmployeeResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) throws AppException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Employee not found with id: " + id));

        employeeRepository.delete(employee);
        log.info("Deleted employee with id={}", id);
    }
}

