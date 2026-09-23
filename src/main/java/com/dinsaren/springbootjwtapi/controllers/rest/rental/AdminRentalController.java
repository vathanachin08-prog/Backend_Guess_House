package com.dinsaren.springbootjwtapi.controllers.rest.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.rental.ReportStatus;
import com.dinsaren.springbootjwtapi.models.res.MessageRes;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.UpdateReportRequest;
import com.dinsaren.springbootjwtapi.payload.request.VerifyPropertyRequest;
import com.dinsaren.springbootjwtapi.payload.response.PropertyResponse;
import com.dinsaren.springbootjwtapi.payload.response.ReportResponse;
import com.dinsaren.springbootjwtapi.services.rental.PropertyService;
import com.dinsaren.springbootjwtapi.services.rental.ReportService;
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
@RequestMapping("/api/admin")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Rental - Admin", description = "Administration operations for rental verification and reports")
public class AdminRentalController {

    private final PropertyService propertyService;
    private final ReportService reportService;

    @Operation(summary = "Verify property", description = "Admin can verify or reject a property listing (PENDING, VERIFIED, REJECTED)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Property verification updated"),
            @ApiResponse(responseCode = "403", description = "Forbidden - admin only"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    @PutMapping("/properties/{id}/verify")
    public ResponseEntity<MessageRes> verifyProperty(
            @PathVariable Long id,
            @Valid @RequestBody VerifyPropertyRequest req
    ) {
        MessageRes res = new MessageRes();
        try {
            PropertyResponse data = propertyService.verifyProperty(id, req);
            res.setUpdateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error verifying property " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "List reports", description = "Admin can list all student reports with optional status filter")
    @GetMapping("/reports")
    public ResponseEntity<MessageRes> getReports(
            @Parameter(description = "Filter by report status: PENDING, REVIEWING, RESOLVED, REJECTED")
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        MessageRes res = new MessageRes();
        try {
            PageRes<ReportResponse> data = reportService.getReports(status, page, size);
            res.setSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error getting admin reports", e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Update report status", description = "Admin updates the processing status of a report")
    @PutMapping("/reports/{id}")
    public ResponseEntity<MessageRes> updateReportStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReportRequest req
    ) {
        MessageRes res = new MessageRes();
        try {
            ReportResponse data = reportService.updateReportStatus(id, req);
            res.setUpdateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error updating report " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
}
