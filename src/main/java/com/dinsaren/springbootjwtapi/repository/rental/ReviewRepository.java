package com.dinsaren.springbootjwtapi.repository.rental;

import com.dinsaren.springbootjwtapi.models.rental.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByPropertyId(Long propertyId, Pageable pageable);
    boolean existsByStudentIdAndPropertyId(int studentId, Long propertyId);
    Optional<Review> findByIdAndStudentId(Long id, int studentId);
}
