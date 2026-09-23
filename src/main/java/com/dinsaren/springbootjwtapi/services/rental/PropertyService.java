package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreatePropertyRequest;
import com.dinsaren.springbootjwtapi.payload.request.PropertySearchRequest;
import com.dinsaren.springbootjwtapi.payload.request.UpdatePropertyRequest;
import com.dinsaren.springbootjwtapi.payload.request.VerifyPropertyRequest;
import com.dinsaren.springbootjwtapi.payload.response.PropertyResponse;
import com.dinsaren.springbootjwtapi.payload.response.PropertySummaryResponse;

public interface PropertyService {
    PropertyResponse createProperty(CreatePropertyRequest req) throws AppException;
    PropertyResponse getPropertyById(Long id) throws AppException;
    PropertyResponse updateProperty(Long id, UpdatePropertyRequest req) throws AppException;
    void deleteProperty(Long id) throws AppException;
    PageRes<PropertySummaryResponse> searchProperties(PropertySearchRequest req);
    PropertyResponse verifyProperty(Long id, VerifyPropertyRequest req) throws AppException;
    PageRes<PropertySummaryResponse> getMyProperties(int page, int size) throws AppException;
}
