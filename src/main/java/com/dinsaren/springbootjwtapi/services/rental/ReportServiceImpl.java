package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.constants.ErrorCode;
import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.User;
import com.dinsaren.springbootjwtapi.models.rental.Property;
import com.dinsaren.springbootjwtapi.models.rental.Report;
import com.dinsaren.springbootjwtapi.models.rental.ReportStatus;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateReportRequest;
import com.dinsaren.springbootjwtapi.payload.request.UpdateReportRequest;
import com.dinsaren.springbootjwtapi.payload.response.ReportResponse;
import com.dinsaren.springbootjwtapi.repository.rental.PropertyRepository;
import com.dinsaren.springbootjwtapi.repository.rental.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final PropertyRepository propertyRepository;
    private final RentalAccessService rentalAccessService;

    @Override
    @Transactional
    public ReportResponse createReport(Long propertyId, CreateReportRequest req) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found"));

        Report report = new Report();
        report.setReporter(currentUser);
        report.setProperty(property);
        report.setReason(req.getReason());
        report.setDescription(req.getDescription());
        report.setStatus(ReportStatus.PENDING);
        report.setCreatedAt(new Date());
        report.setCreatedBy(currentUser.getUsername());

        Report saved = reportRepository.save(report);
        log.info("Report {} submitted for property {} by user {}", saved.getId(), propertyId, currentUser.getUsername());
        return ReportResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageRes<ReportResponse> getReports(ReportStatus status, int page, int size) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        rentalAccessService.assertIsAdminRole(currentUser);

        int p = Math.max(0, page);
        int s = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Report> reports;
        if (status != null) {
            reports = reportRepository.findByStatus(status, pageable);
        } else {
            reports = reportRepository.findAll(pageable);
        }

        return PageRes.of(reports.map(ReportResponse::fromEntity));
    }

    @Override
    @Transactional
    public ReportResponse updateReportStatus(Long id, UpdateReportRequest req) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        rentalAccessService.assertIsAdminRole(currentUser);

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Report not found"));

        report.setStatus(req.getStatus());
        report.setReviewedAt(new Date());
        report.setReviewedBy(currentUser.getUsername());

        Report updated = reportRepository.save(report);
        log.info("Report {} updated to status {} by admin {}", id, req.getStatus(), currentUser.getUsername());
        return ReportResponse.fromEntity(updated);
    }
}
