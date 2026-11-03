package com.dinsaren.springbootjwtapi.payload.response;

import com.dinsaren.springbootjwtapi.models.rental.Tenant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponse {
    private Long id;
    private Long propertyId;
    private Long roomId;
    private String name;
    private String roomNumber;
    private String floor;
    private String phoneNumber;
    private String email;
    private String status;
    private LocalDateTime joinedDate;
    private Date createdAt;
    private Date updatedAt;

    public static TenantResponse fromEntity(Tenant tenant) {
        if (tenant == null) return null;
        TenantResponse res = new TenantResponse();
        res.setId(tenant.getId());
        if (tenant.getProperty() != null) {
            res.setPropertyId(tenant.getProperty().getId());
        }
        if (tenant.getRoom() != null) {
            res.setRoomId(tenant.getRoom().getId());
        }
        res.setName(tenant.getName());
        res.setRoomNumber(tenant.getRoomNumber());
        res.setFloor(tenant.getFloor());
        res.setPhoneNumber(tenant.getPhoneNumber());
        res.setEmail(tenant.getEmail());
        res.setStatus(tenant.getStatus());
        res.setJoinedDate(tenant.getJoinedDate());
        res.setCreatedAt(tenant.getCreateAt());
        res.setUpdatedAt(tenant.getUpdateAt());
        return res;
    }
}

