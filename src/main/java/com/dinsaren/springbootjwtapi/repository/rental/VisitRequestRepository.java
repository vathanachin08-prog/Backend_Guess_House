package com.dinsaren.springbootjwtapi.repository.rental;

import com.dinsaren.springbootjwtapi.models.rental.VisitRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRequestRepository extends JpaRepository<VisitRequest, Long> {
    Page<VisitRequest> findByStudentId(int studentId, Pageable pageable);
    Page<VisitRequest> findByPropertyId(Long propertyId, Pageable pageable);
    Page<VisitRequest> findByPropertyOwnerId(int ownerId, Pageable pageable);
}
