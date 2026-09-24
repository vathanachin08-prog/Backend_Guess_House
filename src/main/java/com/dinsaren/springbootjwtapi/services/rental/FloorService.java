package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.payload.request.CreateFloorRequest;
import com.dinsaren.springbootjwtapi.payload.response.FloorResponse;

import java.util.List;

public interface FloorService {
    List<FloorResponse> getFloorsByProperty(Long propertyId) throws AppException;
    FloorResponse createFloor(Long propertyId, CreateFloorRequest request) throws AppException;
    void deleteFloor(Long floorId) throws AppException;
}
