package com.dinsaren.springbootjwtapi.payload.response;

import com.dinsaren.springbootjwtapi.models.rental.Favorite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
    private Long id;
    private int studentId;
    private PropertySummaryResponse property;
    private RoomResponse room;
    private Date createdAt;

    public static FavoriteResponse fromEntity(Favorite favorite) {
        if (favorite == null) return null;
        FavoriteResponse res = new FavoriteResponse();
        res.setId(favorite.getId());
        if (favorite.getStudent() != null) {
            res.setStudentId(favorite.getStudent().getId());
        }
        if (favorite.getProperty() != null) {
            res.setProperty(PropertySummaryResponse.fromEntity(favorite.getProperty()));
        }
        if (favorite.getRoom() != null) {
            res.setRoom(RoomResponse.fromEntity(favorite.getRoom()));
        }
        res.setCreatedAt(favorite.getCreatedAt());
        return res;
    }
}
