package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.constants.ErrorCode;
import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.User;
import com.dinsaren.springbootjwtapi.models.UserRole;
import com.dinsaren.springbootjwtapi.models.rental.Property;
import com.dinsaren.springbootjwtapi.models.rental.Review;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateReviewRequest;
import com.dinsaren.springbootjwtapi.payload.response.ReviewResponse;
import com.dinsaren.springbootjwtapi.repository.rental.PropertyRepository;
import com.dinsaren.springbootjwtapi.repository.rental.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final RentalAccessService rentalAccessService;

    @Override
    @Transactional
    public ReviewResponse createReview(Long propertyId, CreateReviewRequest req) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found"));

        if (req.getRating() < 1 || req.getRating() > 5) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, "Rating must be between 1 and 5");
        }

        if (reviewRepository.existsByStudentIdAndPropertyId(currentUser.getId(), propertyId)) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REVIEW, "You have already reviewed this property");
        }

        Review review = new Review();
        review.setStudent(currentUser);
        review.setProperty(property);
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        review.setCreateAt(new Date());
        review.setCreateBy(currentUser.getUsername());

        Review saved = reviewRepository.save(review);
        log.info("Review {} created for property {} by {}", saved.getId(), propertyId, currentUser.getUsername());
        return ReviewResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageRes<ReviewResponse> getReviewsByProperty(Long propertyId, int page, int size) throws AppException {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found"));

        int p = Math.max(0, page);
        int s = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "createAt"));

        Page<Review> reviewPage = reviewRepository.findByPropertyId(property.getId(), pageable);
        return PageRes.of(reviewPage.map(ReviewResponse::fromEntity));
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long id, CreateReviewRequest req) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Review not found"));

        if (review.getStudent().getId() != currentUser.getId()) {
            throw new AppException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, "You can only update your own reviews");
        }

        if (req.getRating() < 1 || req.getRating() > 5) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, "Rating must be between 1 and 5");
        }

        review.setRating(req.getRating());
        review.setComment(req.getComment());
        review.setUpdateAt(new Date());
        review.setUpdateBy(currentUser.getUsername());

        return ReviewResponse.fromEntity(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void deleteReview(Long id) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Review not found"));

        if (review.getStudent().getId() != currentUser.getId() && !rentalAccessService.hasRole(currentUser, UserRole.ROLE_ADMIN)) {
            throw new AppException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, "You can only delete your own reviews");
        }

        reviewRepository.delete(review);
        log.info("Review {} deleted by {}", id, currentUser.getUsername());
    }
}
