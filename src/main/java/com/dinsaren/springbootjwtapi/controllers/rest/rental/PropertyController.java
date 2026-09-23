package com.dinsaren.springbootjwtapi.controllers.rest.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.res.MessageRes;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreatePropertyRequest;
import com.dinsaren.springbootjwtapi.payload.request.UpdatePropertyRequest;
import com.dinsaren.springbootjwtapi.payload.response.PropertyResponse;
import com.dinsaren.springbootjwtapi.payload.response.PropertySummaryResponse;
import com.dinsaren.springbootjwtapi.services.rental.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/app/properties")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Rental - Property", description = "Rental property management for owners")
public class PropertyController {

    private final PropertyService propertyService;

    @Operation(summary = "Create property", description = "Property owners can create a new rental property listing")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Property created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires owner role")
    })
    @PostMapping
    public ResponseEntity<MessageRes> create(@Valid @RequestBody CreatePropertyRequest req) {
        MessageRes res = new MessageRes();
        try {
            PropertyResponse data = propertyService.createProperty(req);
            res.setCreateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error creating property", e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Get property by ID", description = "View full details of a property")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Property found"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MessageRes> getById(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            PropertyResponse data = propertyService.getPropertyById(id);
            res.setSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error getting property " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Update property", description = "Update property information (restricted to property owner)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Property updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not own this property"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MessageRes> update(@PathVariable Long id, @Valid @RequestBody UpdatePropertyRequest req) {
        MessageRes res = new MessageRes();
        try {
            PropertyResponse data = propertyService.updateProperty(id, req);
            res.setUpdateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error updating property " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Delete property", description = "Delete a property and all associated rooms (owner only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Property deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not own this property"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageRes> delete(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            propertyService.deleteProperty(id);
            res.setSuccess("Property deleted successfully");
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error deleting property " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Get my properties", description = "Returns a paginated list of properties owned by the current authenticated owner")
    @GetMapping("/my")
    public ResponseEntity<MessageRes> getMyProperties(
            @Parameter(description = "Page index (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        MessageRes res = new MessageRes();
        try {
            PageRes<PropertySummaryResponse> data = propertyService.getMyProperties(page, size);
            res.setSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error fetching my properties", e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
}
