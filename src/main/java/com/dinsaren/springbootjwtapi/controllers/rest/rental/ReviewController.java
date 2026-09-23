package com.dinsaren.springbootjwtapi.controllers.rest.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.res.MessageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateReviewRequest;
import com.dinsaren.springbootjwtapi.payload.response.ReviewResponse;
import com.dinsaren.springbootjwtapi.services.rental.ReviewService;
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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/app")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Rental - Reviews", description = "Student review management")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Create review", description = "Authenticated students can review a property (rating 1-5)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review posted successfully"),
            @ApiResponse(responseCode = "400", description = "Duplicate review or invalid rating")
    })
    @PostMapping("/properties/{propertyId}/reviews")
    public ResponseEntity<MessageRes> createReview(
            @PathVariable Long propertyId,
            @Valid @RequestBody CreateReviewRequest req
    ) {
        MessageRes res = new MessageRes();
        try {
            ReviewResponse data = reviewService.createReview(propertyId, req);
            res.setCreateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error creating review for property " + propertyId, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Update review", description = "Students can update their own review")
    @PutMapping("/reviews/{id}")
    public ResponseEntity<MessageRes> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody CreateReviewRequest req
    ) {
        MessageRes res = new MessageRes();
        try {
            ReviewResponse data = reviewService.updateReview(id, req);
            res.setUpdateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error updating review " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Delete review", description = "Students can delete their own review; Admin can delete any review")
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<MessageRes> deleteReview(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            reviewService.deleteReview(id);
            res.setSuccess("Review deleted successfully");
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error deleting review " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
}
