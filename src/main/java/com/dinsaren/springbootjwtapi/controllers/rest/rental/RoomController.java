package com.dinsaren.springbootjwtapi.controllers.rest.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.res.MessageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateRoomRequest;
import com.dinsaren.springbootjwtapi.payload.request.UpdateRoomRequest;
import com.dinsaren.springbootjwtapi.payload.response.RoomResponse;
import com.dinsaren.springbootjwtapi.services.rental.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/app")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Rental - Room", description = "Room management for property owners")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Create room", description = "Add a new rental room to a property (owner only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room created successfully"),
            @ApiResponse(responseCode = "400", description = "Duplicate room number or validation error"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not own property")
    })
    @PostMapping("/properties/{propertyId}/rooms")
    public ResponseEntity<MessageRes> createRoom(
            @PathVariable Long propertyId,
            @Valid @RequestBody CreateRoomRequest req
    ) {
        MessageRes res = new MessageRes();
        try {
            RoomResponse data = roomService.createRoom(propertyId, req);
            res.setCreateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error creating room for property " + propertyId, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Get rooms by property", description = "List all rooms belonging to a property")
    @GetMapping("/properties/{propertyId}/rooms")
    public ResponseEntity<MessageRes> getRoomsByProperty(@PathVariable Long propertyId) {
        MessageRes res = new MessageRes();
        try {
            List<RoomResponse> data = roomService.getRoomsByProperty(propertyId);
            res.setSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error getting rooms for property " + propertyId, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Update room", description = "Update room information and facilities (owner only)")
    @PutMapping("/rooms/{id}")
    public ResponseEntity<MessageRes> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoomRequest req
    ) {
        MessageRes res = new MessageRes();
        try {
            RoomResponse data = roomService.updateRoom(id, req);
            res.setUpdateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error updating room " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Delete room", description = "Delete a room (owner only)")
    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<MessageRes> deleteRoom(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            roomService.deleteRoom(id);
            res.setSuccess("Room deleted successfully");
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error deleting room " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
}
