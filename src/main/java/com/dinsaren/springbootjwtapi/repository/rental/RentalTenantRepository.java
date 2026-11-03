package com.dinsaren.springbootjwtapi.repository.rental;

import com.dinsaren.springbootjwtapi.models.rental.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("rentalTenantRepository")
public interface RentalTenantRepository extends JpaRepository<Tenant, Long> {
    List<Tenant> findByPropertyIdAndStatusOrderByIdDesc(Long propertyId, String status);
    List<Tenant> findByPropertyIdOrderByIdDesc(Long propertyId);
    Optional<Tenant> findByIdAndPropertyId(Long id, Long propertyId);
    boolean existsByPropertyIdAndRoomNumberAndStatus(Long propertyId, String roomNumber, String status);
}

