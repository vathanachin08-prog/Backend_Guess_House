package com.dinsaren.springbootjwtapi.payload.request;

import com.dinsaren.springbootjwtapi.models.rental.GenderPreference;
import com.dinsaren.springbootjwtapi.models.rental.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class CreateRoomRequest {
    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotBlank(message = "Room title is required")
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be >= 0")
    private BigDecimal price;

    private RoomType roomType = RoomType.SINGLE;

    private GenderPreference genderPreference = GenderPreference.ANY;

    private Boolean available = true;

    private Integer floor;

    private Double area;

    private String images;

    private Set<Long> facilityIds;
}
