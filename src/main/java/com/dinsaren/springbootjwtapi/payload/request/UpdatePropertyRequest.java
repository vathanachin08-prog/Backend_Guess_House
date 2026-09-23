package com.dinsaren.springbootjwtapi.payload.request;

import com.dinsaren.springbootjwtapi.models.rental.PropertyStatus;
import com.dinsaren.springbootjwtapi.models.rental.PropertyType;
import lombok.Data;

@Data
public class UpdatePropertyRequest {
    private String name;
    private String description;
    private PropertyType propertyType;
    private String address;
    private String city;
    private String district;
    private Double latitude;
    private Double longitude;
    private PropertyStatus status;
    private String mainImage;
    private String images;
}
