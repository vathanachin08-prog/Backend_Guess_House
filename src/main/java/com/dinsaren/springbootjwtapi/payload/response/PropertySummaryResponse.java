package com.dinsaren.springbootjwtapi.payload.response;

import com.dinsaren.springbootjwtapi.models.rental.Property;
import com.dinsaren.springbootjwtapi.models.rental.PropertyStatus;
import com.dinsaren.springbootjwtapi.models.rental.PropertyVerificationStatus;
import com.dinsaren.springbootjwtapi.models.rental.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertySummaryResponse {
    private Long id;
    private String name;
    private String mainImage;
    private String city;
    private String district;
    private String address;
    private BigDecimal minRoomPrice;
    private PropertyStatus status;
    private PropertyVerificationStatus verificationStatus;
    private int availableRoomCount;
    private int totalRoomCount;
    private OwnerSummaryResponse ownerSummary;

    public static PropertySummaryResponse fromEntity(Property property) {
        if (property == null) return null;
        PropertySummaryResponse res = new PropertySummaryResponse();
        res.setId(property.getId());
        res.setName(property.getName());
        res.setMainImage(property.getMainImage());
        res.setCity(property.getCity());
        res.setDistrict(property.getDistrict());
        res.setAddress(property.getAddress());
        res.setStatus(property.getStatus());
        res.setVerificationStatus(property.getVerificationStatus());
        res.setOwnerSummary(OwnerSummaryResponse.fromEntity(property.getOwner()));

        if (property.getRooms() != null && !property.getRooms().isEmpty()) {
            res.setTotalRoomCount(property.getRooms().size());
            res.setAvailableRoomCount((int) property.getRooms().stream()
                    .filter(r -> Boolean.TRUE.equals(r.getAvailable()))
                    .count());

            BigDecimal minPrice = property.getRooms().stream()
                    .map(Room::getPrice)
                    .filter(Objects::nonNull)
                    .min(Comparator.naturalOrder())
                    .orElse(null);
            res.setMinRoomPrice(minPrice);
        } else {
            res.setAvailableRoomCount(0);
            res.setTotalRoomCount(0);
            res.setMinRoomPrice(null);
        }
        return res;
    }
}
