package com.dinsaren.springbootjwtapi.payload.request;

import com.dinsaren.springbootjwtapi.models.rental.VisitRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateVisitRequestStatusRequest {
    @NotNull(message = "Status is required")
    private VisitRequestStatus status;
}
