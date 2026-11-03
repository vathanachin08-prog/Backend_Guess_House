package com.dinsaren.springbootjwtapi.controllers.rest.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.res.MessageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateTenantRequest;
import com.dinsaren.springbootjwtapi.payload.request.UpdateTenantRequest;
import com.dinsaren.springbootjwtapi.payload.response.TenantResponse;
import com.dinsaren.springbootjwtapi.services.rental.TenantService;
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
@Tag(name = "Rental - Tenant", description = "Tenant management for property owners")
public class TenantController {

    private final TenantService tenantService;

    @Operation(summary = "Get tenants by property", description = "List all tenants belonging to a rental property")
    @GetMapping("/properties/{propertyId}/tenants")
    public ResponseEntity<MessageRes> getTenantsByProperty(@PathVariable Long propertyId) {
        MessageRes res = new MessageRes();
        try {
            List<TenantResponse> data = tenantService.getTenantsByProperty(propertyId);
            res.setSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error getting tenants for property " + propertyId, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Create tenant", description = "Add a new tenant to a property (owner only)")
    @PostMapping("/properties/{propertyId}/tenants")
    public ResponseEntity<MessageRes> createTenant(
            @PathVariable Long propertyId,
            @Valid @RequestBody CreateTenantRequest req
    ) {
        MessageRes res = new MessageRes();
        try {
            TenantResponse data = tenantService.createTenant(propertyId, req);
            res.setCreateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error creating tenant for property " + propertyId, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Update tenant", description = "Update tenant details (owner only)")
    @PutMapping("/tenants/{id}")
    public ResponseEntity<MessageRes> updateTenant(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTenantRequest req
    ) {
        MessageRes res = new MessageRes();
        try {
            TenantResponse data = tenantService.updateTenant(id, req);
            res.setUpdateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error updating tenant " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Delete tenant", description = "Delete a tenant (owner only)")
    @DeleteMapping("/tenants/{id}")
    public ResponseEntity<MessageRes> deleteTenant(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            tenantService.deleteTenant(id);
            res.setSuccess("Tenant deleted successfully");
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error deleting tenant " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
}

