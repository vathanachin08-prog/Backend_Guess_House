package com.dinsaren.springbootjwtapi.payload.request;

import com.dinsaren.springbootjwtapi.models.rental.GenderPreference;
import com.dinsaren.springbootjwtapi.models.rental.RoomType;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class UpdateRoomRequest {
    private String roomNumber;
    private String title;
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be >= 0")
    private BigDecimal price;

    private RoomType roomType;
    private GenderPreference genderPreference;
    private Boolean available;
    private Integer floor;
    private Double area;
    private String images;
    private Set<Long> facilityIds;
}
