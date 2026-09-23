package com.dinsaren.springbootjwtapi.payload.request;

import com.dinsaren.springbootjwtapi.models.rental.RoomType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertySearchRequest {
    private String keyword;
    private String city;
    private String district;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private RoomType roomType;
    private Boolean available;
    private String facility;
    private Double latitude;
    private Double longitude;
    private Double radius;
    private int page = 0;
    private int size = 10;
}
