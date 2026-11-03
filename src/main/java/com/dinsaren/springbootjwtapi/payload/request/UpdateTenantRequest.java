package com.dinsaren.springbootjwtapi.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTenantRequest {
    @NotBlank(message = "Tenant name is required")
    private String name;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    private String floor;

    private String phoneNumber;

    private String email;

    private String status;
}

