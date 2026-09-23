package com.dinsaren.springbootjwtapi.payload.request;

import com.dinsaren.springbootjwtapi.models.rental.PropertyStatus;
import com.dinsaren.springbootjwtapi.models.rental.PropertyVerificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyPropertyRequest {
    @NotNull(message = "Verification status is required")
    private PropertyVerificationStatus verificationStatus;

    private PropertyStatus status;
}
