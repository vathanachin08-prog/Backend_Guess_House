package com.dinsaren.springbootjwtapi.repository.rental;

import com.dinsaren.springbootjwtapi.models.rental.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("rentalFloorRepository")
public interface RentalFloorRepository extends JpaRepository<Floor, Long> {
    List<Floor> findByPropertyIdAndStatusOrderByFloorOrderAsc(Long propertyId, String status);
    List<Floor> findByPropertyId(Long propertyId);
    boolean existsByPropertyIdAndName(Long propertyId, String name);
    Optional<Floor> findByIdAndPropertyId(Long id, Long propertyId);
}
