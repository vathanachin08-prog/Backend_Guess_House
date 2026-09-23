package com.dinsaren.springbootjwtapi.payload.response;

import com.dinsaren.springbootjwtapi.models.rental.Property;
import com.dinsaren.springbootjwtapi.models.rental.PropertyStatus;
import com.dinsaren.springbootjwtapi.models.rental.PropertyType;
import com.dinsaren.springbootjwtapi.models.rental.PropertyVerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponse {
    private Long id;
    private String name;
    private String description;
    private PropertyType propertyType;
    private String address;
    private String city;
    private String district;
    private Double latitude;
    private Double longitude;
    private PropertyStatus status;
    private PropertyVerificationStatus verificationStatus;
    private String mainImage;
    private String images;
    private OwnerSummaryResponse owner;
    private List<RoomResponse> rooms = new ArrayList<>();
    private Date createdAt;
    private Date updatedAt;

    public static PropertyResponse fromEntity(Property property) {
        if (property == null) return null;
        PropertyResponse res = new PropertyResponse();
        res.setId(property.getId());
        res.setName(property.getName());
        res.setDescription(property.getDescription());
        res.setPropertyType(property.getPropertyType());
        res.setAddress(property.getAddress());
        res.setCity(property.getCity());
        res.setDistrict(property.getDistrict());
        res.setLatitude(property.getLatitude());
        res.setLongitude(property.getLongitude());
        res.setStatus(property.getStatus());
        res.setVerificationStatus(property.getVerificationStatus());
        res.setMainImage(property.getMainImage());
        res.setImages(property.getImages());
        res.setOwner(OwnerSummaryResponse.fromEntity(property.getOwner()));
        if (property.getRooms() != null) {
            res.setRooms(property.getRooms().stream()
                    .map(RoomResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        res.setCreatedAt(property.getCreateAt());
        res.setUpdatedAt(property.getUpdateAt());
        return res;
    }
}
