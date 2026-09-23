package com.dinsaren.springbootjwtapi.payload.response;

import com.dinsaren.springbootjwtapi.models.rental.VisitRequest;
import com.dinsaren.springbootjwtapi.models.rental.VisitRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitRequestResponse {
    private Long id;
    private int studentId;
    private String studentName;
    private String studentPhone;
    private Long propertyId;
    private String propertyName;
    private Long roomId;
    private String roomNumber;
    private LocalDate requestedDate;
    private String requestedTime;
    private String message;
    private VisitRequestStatus status;
    private Date createdAt;
    private Date updatedAt;

    public static VisitRequestResponse fromEntity(VisitRequest vr) {
        if (vr == null) return null;
        VisitRequestResponse res = new VisitRequestResponse();
        res.setId(vr.getId());
        if (vr.getStudent() != null) {
            res.setStudentId(vr.getStudent().getId());
            res.setStudentName((vr.getStudent().getFirstName() != null ? vr.getStudent().getFirstName() + " " : "")
                    + (vr.getStudent().getLastName() != null ? vr.getStudent().getLastName() : ""));
            res.setStudentPhone(vr.getStudent().getPhoneNumber());
        }
        if (vr.getProperty() != null) {
            res.setPropertyId(vr.getProperty().getId());
            res.setPropertyName(vr.getProperty().getName());
        }
        if (vr.getRoom() != null) {
            res.setRoomId(vr.getRoom().getId());
            res.setRoomNumber(vr.getRoom().getRoomNumber());
        }
        res.setRequestedDate(vr.getRequestedDate());
        res.setRequestedTime(vr.getRequestedTime());
        res.setMessage(vr.getMessage());
        res.setStatus(vr.getStatus());
        res.setCreatedAt(vr.getCreateAt());
        res.setUpdatedAt(vr.getUpdateAt());
        return res;
    }
}
