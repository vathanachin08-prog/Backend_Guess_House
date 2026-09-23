package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.payload.request.CreateRoomRequest;
import com.dinsaren.springbootjwtapi.payload.request.UpdateRoomRequest;
import com.dinsaren.springbootjwtapi.payload.response.RoomResponse;

import java.util.List;

public interface RoomService {
    RoomResponse createRoom(Long propertyId, CreateRoomRequest req) throws AppException;
    RoomResponse getRoomById(Long id) throws AppException;
    RoomResponse updateRoom(Long id, UpdateRoomRequest req) throws AppException;
    void deleteRoom(Long id) throws AppException;
    List<RoomResponse> getRoomsByProperty(Long propertyId) throws AppException;
}
