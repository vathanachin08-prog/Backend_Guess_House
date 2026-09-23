package com.dinsaren.springbootjwtapi.payload.response;

import com.dinsaren.springbootjwtapi.models.rental.GenderPreference;
import com.dinsaren.springbootjwtapi.models.rental.Room;
import com.dinsaren.springbootjwtapi.models.rental.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private Long propertyId;
    private String propertyName;
    private String roomNumber;
    private String title;
    private String description;
    private BigDecimal price;
    private RoomType roomType;
    private GenderPreference genderPreference;
    private Boolean available;
    private Integer floor;
    private Double area;
    private String images;
    private Set<FacilityResponse> facilities;
    private Date createdAt;
    private Date updatedAt;

    public static RoomResponse fromEntity(Room room) {
        if (room == null) return null;
        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        if (room.getProperty() != null) {
            response.setPropertyId(room.getProperty().getId());
            response.setPropertyName(room.getProperty().getName());
        }
        response.setRoomNumber(room.getRoomNumber());
        response.setTitle(room.getTitle());
        response.setDescription(room.getDescription());
        response.setPrice(room.getPrice());
        response.setRoomType(room.getRoomType());
        response.setGenderPreference(room.getGenderPreference());
        response.setAvailable(room.getAvailable());
        response.setFloor(room.getFloor());
        response.setArea(room.getArea());
        response.setImages(room.getImages());
        if (room.getFacilities() != null) {
            response.setFacilities(room.getFacilities().stream()
                    .map(FacilityResponse::fromEntity)
                    .collect(Collectors.toSet()));
        }
        response.setCreatedAt(room.getCreateAt());
        response.setUpdatedAt(room.getUpdateAt());
        return response;
    }
}
