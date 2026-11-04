package com.dinsaren.springbootjwtapi.payload.response;

import com.dinsaren.springbootjwtapi.models.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String position;
    private String department;
    private double salary;
    private String status;
    private String profileImage;
    private Date createdAt;
    private Date updatedAt;

    public static EmployeeResponse fromEntity(Employee entity) {
        if (entity == null) return null;
        return EmployeeResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .position(entity.getPosition())
                .department(entity.getDepartment())
                .salary(entity.getSalary())
                .status(entity.getStatus())
                .profileImage(entity.getProfileImage())
                .createdAt(entity.getCreateAt())
                .updatedAt(entity.getUpdateAt())
                .build();
    }
}

