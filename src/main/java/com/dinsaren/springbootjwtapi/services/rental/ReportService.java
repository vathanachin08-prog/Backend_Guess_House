package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.rental.ReportStatus;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateReportRequest;
import com.dinsaren.springbootjwtapi.payload.request.UpdateReportRequest;
import com.dinsaren.springbootjwtapi.payload.response.ReportResponse;

public interface ReportService {
    ReportResponse createReport(Long propertyId, CreateReportRequest req) throws AppException;
    PageRes<ReportResponse> getReports(ReportStatus status, int page, int size) throws AppException;
    ReportResponse updateReportStatus(Long id, UpdateReportRequest req) throws AppException;
}
