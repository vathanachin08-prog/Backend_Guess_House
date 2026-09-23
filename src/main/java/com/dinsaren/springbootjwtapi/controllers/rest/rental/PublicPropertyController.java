package com.dinsaren.springbootjwtapi.controllers.rest.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.rental.RoomType;
import com.dinsaren.springbootjwtapi.models.res.MessageRes;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.PropertySearchRequest;
import com.dinsaren.springbootjwtapi.payload.response.PropertyResponse;
import com.dinsaren.springbootjwtapi.payload.response.PropertySummaryResponse;
import com.dinsaren.springbootjwtapi.payload.response.ReviewResponse;
import com.dinsaren.springbootjwtapi.payload.response.RoomResponse;
import com.dinsaren.springbootjwtapi.services.rental.PropertyService;
import com.dinsaren.springbootjwtapi.services.rental.ReviewService;
import com.dinsaren.springbootjwtapi.services.rental.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/public")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Rental - Public", description = "Public rental endpoints — no authentication required")
@SecurityRequirements
public class PublicPropertyController {

    private final PropertyService propertyService;
    private final RoomService roomService;
    private final ReviewService reviewService;

    @Operation(summary = "Search properties", description = "Public search for published and verified rental properties with multi-criteria filters and pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Properties found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/properties")
    public ResponseEntity<MessageRes> searchProperties(
            @Parameter(description = "Keyword search in property name, description, address")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "City filter (e.g. Phnom Penh)")
            @RequestParam(required = false) String city,
            @Parameter(description = "District filter (e.g. Daun Penh)")
            @RequestParam(required = false) String district,
            @Parameter(description = "Minimum room price")
            @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum room price")
            @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Room type: SINGLE, DOUBLE, SHARED")
            @RequestParam(required = false) RoomType roomType,
            @Parameter(description = "Room availability filter")
            @RequestParam(required = false) Boolean available,
            @Parameter(description = "Required facility name (e.g. WIFI, AIR_CONDITIONER)")
            @RequestParam(required = false) String facility,
            @Parameter(description = "Latitude")
            @RequestParam(required = false) Double latitude,
            @Parameter(description = "Longitude")
            @RequestParam(required = false) Double longitude,
            @Parameter(description = "Radius in km")
            @RequestParam(required = false) Double radius,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        MessageRes res = new MessageRes();
        try {
            PropertySearchRequest searchRequest = new PropertySearchRequest();
            searchRequest.setKeyword(keyword);
            searchRequest.setCity(city);
            searchRequest.setDistrict(district);
            searchRequest.setMinPrice(minPrice);
            searchRequest.setMaxPrice(maxPrice);
            searchRequest.setRoomType(roomType);
            searchRequest.setAvailable(available);
            searchRequest.setFacility(facility);
            searchRequest.setLatitude(latitude);
            searchRequest.setLongitude(longitude);
            searchRequest.setRadius(radius);
            searchRequest.setPage(page);
            searchRequest.setSize(size);

            PageRes<PropertySummaryResponse> data = propertyService.searchProperties(searchRequest);
            res.setSuccess(data);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            log.error("Error searching properties", e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Get public property details", description = "View details and rooms of a published property")
    @GetMapping("/properties/{id}")
    public ResponseEntity<MessageRes> getPublicProperty(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            PropertyResponse data = propertyService.getPropertyById(id);
            res.setSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error getting public property " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Get room details", description = "View details and facilities of a room")
    @GetMapping("/rooms/{id}")
    public ResponseEntity<MessageRes> getPublicRoom(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            RoomResponse data = roomService.getRoomById(id);
            res.setSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error getting room " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Get property reviews", description = "Returns reviews for a given property")
    @GetMapping("/properties/{propertyId}/reviews")
    public ResponseEntity<MessageRes> getPublicReviews(
            @PathVariable Long propertyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        MessageRes res = new MessageRes();
        try {
            PageRes<ReviewResponse> data = reviewService.getReviewsByProperty(propertyId, page, size);
            res.setSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error getting reviews for property " + propertyId, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
}
