package com.dinsaren.springbootjwtapi.payload.response;

import com.dinsaren.springbootjwtapi.models.rental.Report;
import com.dinsaren.springbootjwtapi.models.rental.ReportReason;
import com.dinsaren.springbootjwtapi.models.rental.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private Long id;
    private int reporterId;
    private String reporterName;
    private Long propertyId;
    private String propertyName;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private Date createdAt;
    private Date reviewedAt;
    private String reviewedBy;

    public static ReportResponse fromEntity(Report report) {
        if (report == null) return null;
        ReportResponse res = new ReportResponse();
        res.setId(report.getId());
        if (report.getReporter() != null) {
            res.setReporterId(report.getReporter().getId());
            res.setReporterName((report.getReporter().getFirstName() != null ? report.getReporter().getFirstName() + " " : "")
                    + (report.getReporter().getLastName() != null ? report.getReporter().getLastName() : ""));
        }
        if (report.getProperty() != null) {
            res.setPropertyId(report.getProperty().getId());
            res.setPropertyName(report.getProperty().getName());
        }
        res.setReason(report.getReason());
        res.setDescription(report.getDescription());
        res.setStatus(report.getStatus());
        res.setCreatedAt(report.getCreatedAt());
        res.setReviewedAt(report.getReviewedAt());
        res.setReviewedBy(report.getReviewedBy());
        return res;
    }
}
