package com.dinsaren.springbootjwtapi.payload.response;

import com.dinsaren.springbootjwtapi.models.rental.Facility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityResponse {
    private Long id;
    private String name;
    private String description;
    private String icon;

    public static FacilityResponse fromEntity(Facility facility) {
        if (facility == null) return null;
        return new FacilityResponse(
                facility.getId(),
                facility.getName(),
                facility.getDescription(),
                facility.getIcon()
        );
    }
}
