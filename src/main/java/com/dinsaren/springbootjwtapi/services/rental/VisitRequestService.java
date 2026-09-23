package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateVisitRequest;
import com.dinsaren.springbootjwtapi.payload.response.VisitRequestResponse;

public interface VisitRequestService {
    VisitRequestResponse createVisitRequest(CreateVisitRequest req) throws AppException;
    PageRes<VisitRequestResponse> getMyVisitRequests(int page, int size) throws AppException;
    PageRes<VisitRequestResponse> getOwnerVisitRequests(Long propertyId, int page, int size) throws AppException;
    VisitRequestResponse acceptVisitRequest(Long id) throws AppException;
    VisitRequestResponse rejectVisitRequest(Long id) throws AppException;
    VisitRequestResponse cancelVisitRequest(Long id) throws AppException;
    VisitRequestResponse completeVisitRequest(Long id) throws AppException;
}
