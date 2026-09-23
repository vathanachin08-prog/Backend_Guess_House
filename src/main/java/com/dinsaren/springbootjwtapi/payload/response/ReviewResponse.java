package com.dinsaren.springbootjwtapi.payload.response;

import com.dinsaren.springbootjwtapi.models.rental.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private int studentId;
    private String studentName;
    private String studentProfile;
    private Long propertyId;
    private String propertyName;
    private Integer rating;
    private String comment;
    private Date createdAt;
    private Date updatedAt;

    public static ReviewResponse fromEntity(Review review) {
        if (review == null) return null;
        ReviewResponse res = new ReviewResponse();
        res.setId(review.getId());
        if (review.getStudent() != null) {
            res.setStudentId(review.getStudent().getId());
            res.setStudentName((review.getStudent().getFirstName() != null ? review.getStudent().getFirstName() + " " : "")
                    + (review.getStudent().getLastName() != null ? review.getStudent().getLastName() : ""));
            res.setStudentProfile(review.getStudent().getProfile());
        }
        if (review.getProperty() != null) {
            res.setPropertyId(review.getProperty().getId());
            res.setPropertyName(review.getProperty().getName());
        }
        res.setRating(review.getRating());
        res.setComment(review.getComment());
        res.setCreatedAt(review.getCreateAt());
        res.setUpdatedAt(review.getUpdateAt());
        return res;
    }
}
