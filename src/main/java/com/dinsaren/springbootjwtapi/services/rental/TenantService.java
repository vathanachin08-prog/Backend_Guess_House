package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.payload.request.CreateTenantRequest;
import com.dinsaren.springbootjwtapi.payload.request.UpdateTenantRequest;
import com.dinsaren.springbootjwtapi.payload.response.TenantResponse;

import java.util.List;

public interface TenantService {
    List<TenantResponse> getTenantsByProperty(Long propertyId) throws AppException;
    TenantResponse createTenant(Long propertyId, CreateTenantRequest req) throws AppException;
    TenantResponse updateTenant(Long id, UpdateTenantRequest req) throws AppException;
    void deleteTenant(Long id) throws AppException;
}

