package com.dinsaren.springbootjwtapi.payload.request;

import com.dinsaren.springbootjwtapi.models.rental.PropertyType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePropertyRequest {
    @NotBlank(message = "Property name is required")
    private String name;

    private String description;

    private PropertyType propertyType = PropertyType.APARTMENT;

    private String address;

    private String city;

    private String district;

    private Double latitude;

    private Double longitude;

    private String mainImage;

    private String images;
}
