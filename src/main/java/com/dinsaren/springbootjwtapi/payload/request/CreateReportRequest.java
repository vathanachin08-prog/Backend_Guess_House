package com.dinsaren.springbootjwtapi.payload.request;

import com.dinsaren.springbootjwtapi.models.rental.ReportReason;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReportRequest {
    @NotNull(message = "Reason is required")
    private ReportReason reason;

    private String description;
}
