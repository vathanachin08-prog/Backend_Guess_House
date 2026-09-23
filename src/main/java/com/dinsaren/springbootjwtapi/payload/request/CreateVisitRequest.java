package com.dinsaren.springbootjwtapi.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateVisitRequest {
    @NotNull(message = "Property ID is required")
    private Long propertyId;

    private Long roomId;

    @NotNull(message = "Requested date is required")
    private LocalDate requestedDate;

    private String requestedTime;

    private String message;
}
