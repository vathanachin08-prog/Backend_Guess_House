package com.dinsaren.springbootjwtapi.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateFloorRequest {
    @NotBlank(message = "Floor name is required")
    private String name;

    private Integer floorOrder;
}
