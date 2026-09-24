package com.dinsaren.springbootjwtapi.payload.response;

import com.dinsaren.springbootjwtapi.models.rental.Floor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FloorResponse {
    private Long id;
    private Long propertyId;
    private String name;
    private Integer floorOrder;
    private int totalRooms;
    private int occupiedRooms;
    private String status;
    private Date createdAt;
    private Date updatedAt;

    public static FloorResponse fromEntity(Floor floor) {
        if (floor == null) return null;
        FloorResponse res = new FloorResponse();
        res.setId(floor.getId());
        if (floor.getProperty() != null) {
            res.setPropertyId(floor.getProperty().getId());
        }
        res.setName(floor.getName());
        res.setFloorOrder(floor.getFloorOrder());
        res.setStatus(floor.getStatus());
        res.setCreatedAt(floor.getCreateAt());
        res.setUpdatedAt(floor.getUpdateAt());
        return res;
    }
}
