package com.dinsaren.springbootjwtapi.controllers.rest.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.res.MessageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateFloorRequest;
import com.dinsaren.springbootjwtapi.payload.response.FloorResponse;
import com.dinsaren.springbootjwtapi.services.rental.FloorService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Rental - Floor", description = "Floor management for property owners")
public class FloorController {

    private final FloorService floorService;

    @Operation(summary = "Get floors by property", description = "List all floors belonging to a rental property")
    @GetMapping("/properties/{propertyId}/floors")
    public ResponseEntity<MessageRes> getFloorsByProperty(@PathVariable Long propertyId) {
        MessageRes res = new MessageRes();
        try {
            List<FloorResponse> data = floorService.getFloorsByProperty(propertyId);
            res.setSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error getting floors for property " + propertyId, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Create floor", description = "Add a new floor to a property (owner only)")
    @PostMapping("/properties/{propertyId}/floors")
    public ResponseEntity<MessageRes> createFloor(
            @PathVariable Long propertyId,
            @Valid @RequestBody CreateFloorRequest req
    ) {
        MessageRes res = new MessageRes();
        try {
            FloorResponse data = floorService.createFloor(propertyId, req);
            res.setCreateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error creating floor for property " + propertyId, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Delete floor", description = "Soft delete / deactivate a floor (owner only)")
    @DeleteMapping("/floors/{id}")
    public ResponseEntity<MessageRes> deleteFloor(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            floorService.deleteFloor(id);
            res.setSuccess("Floor deleted successfully");
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error deleting floor " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
}
