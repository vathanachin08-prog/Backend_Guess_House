package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateReviewRequest;
import com.dinsaren.springbootjwtapi.payload.response.ReviewResponse;

public interface ReviewService {
    ReviewResponse createReview(Long propertyId, CreateReviewRequest req) throws AppException;
    PageRes<ReviewResponse> getReviewsByProperty(Long propertyId, int page, int size) throws AppException;
    ReviewResponse updateReview(Long id, CreateReviewRequest req) throws AppException;
    void deleteReview(Long id) throws AppException;
}
