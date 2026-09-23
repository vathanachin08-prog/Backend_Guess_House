package com.dinsaren.springbootjwtapi.controllers.rest.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.res.MessageRes;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateVisitRequest;
import com.dinsaren.springbootjwtapi.payload.response.VisitRequestResponse;
import com.dinsaren.springbootjwtapi.services.rental.VisitRequestService;
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
@Tag(name = "Rental - Visit Requests", description = "Property visit requests for students and owners")
public class VisitRequestController {

    private final VisitRequestService visitRequestService;

    @Operation(summary = "Create visit request", description = "Students can request an in-person or virtual property visit")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Visit request submitted"),
            @ApiResponse(responseCode = "400", description = "Invalid date or input")
    })
    @PostMapping("/visit-requests")
    public ResponseEntity<MessageRes> createVisitRequest(@Valid @RequestBody CreateVisitRequest req) {
        MessageRes res = new MessageRes();
        try {
            VisitRequestResponse data = visitRequestService.createVisitRequest(req);
            res.setCreateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error creating visit request", e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Get my visit requests", description = "Students can view all their submitted visit requests")
    @GetMapping("/visit-requests/my")
    public ResponseEntity<MessageRes> getMyVisitRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        MessageRes res = new MessageRes();
        try {
            PageRes<VisitRequestResponse> data = visitRequestService.getMyVisitRequests(page, size);
            res.setSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error getting my visit requests", e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Get owner visit requests", description = "Property owners can view visit requests for their properties")
    @GetMapping("/owner/visit-requests/{propertyId}")
    public ResponseEntity<MessageRes> getOwnerVisitRequests(
            @PathVariable Long propertyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        MessageRes res = new MessageRes();
        try {
            PageRes<VisitRequestResponse> data = visitRequestService.getOwnerVisitRequests(propertyId, page, size);
            res.setSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error getting owner visit requests", e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Accept visit request", description = "Property owner accepts a pending visit request")
    @PutMapping("/visit-requests/{id}/accept")
    public ResponseEntity<MessageRes> acceptVisitRequest(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            VisitRequestResponse data = visitRequestService.acceptVisitRequest(id);
            res.setUpdateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error accepting visit request " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Reject visit request", description = "Property owner rejects a visit request")
    @PutMapping("/visit-requests/{id}/reject")
    public ResponseEntity<MessageRes> rejectVisitRequest(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            VisitRequestResponse data = visitRequestService.rejectVisitRequest(id);
            res.setUpdateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error rejecting visit request " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Cancel visit request", description = "Student cancels their own pending visit request")
    @PutMapping("/visit-requests/{id}/cancel")
    public ResponseEntity<MessageRes> cancelVisitRequest(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            VisitRequestResponse data = visitRequestService.cancelVisitRequest(id);
            res.setUpdateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error cancelling visit request " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Complete visit request", description = "Owner marks an accepted visit request as completed")
    @PutMapping("/visit-requests/{id}/complete")
    public ResponseEntity<MessageRes> completeVisitRequest(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            VisitRequestResponse data = visitRequestService.completeVisitRequest(id);
            res.setUpdateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error completing visit request " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
}
