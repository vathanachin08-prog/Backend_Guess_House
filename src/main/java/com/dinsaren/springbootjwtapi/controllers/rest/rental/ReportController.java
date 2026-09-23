package com.dinsaren.springbootjwtapi.controllers.rest.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.res.MessageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateReportRequest;
import com.dinsaren.springbootjwtapi.payload.response.ReportResponse;
import com.dinsaren.springbootjwtapi.services.rental.ReportService;
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
@Tag(name = "Rental - Reports", description = "Student property reporting")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Report property", description = "Students can report fraudulent, scam, or unsafe properties")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    @PostMapping("/properties/{propertyId}/reports")
    public ResponseEntity<MessageRes> createReport(
            @PathVariable Long propertyId,
            @Valid @RequestBody CreateReportRequest req
    ) {
        MessageRes res = new MessageRes();
        try {
            ReportResponse data = reportService.createReport(propertyId, req);
            res.setCreateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error creating report for property " + propertyId, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
}
