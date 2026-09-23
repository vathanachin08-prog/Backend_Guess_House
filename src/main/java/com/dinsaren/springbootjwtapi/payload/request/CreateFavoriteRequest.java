package com.dinsaren.springbootjwtapi.payload.request;

import lombok.Data;

@Data
public class CreateFavoriteRequest {
    private Long propertyId;
    private Long roomId;
}
