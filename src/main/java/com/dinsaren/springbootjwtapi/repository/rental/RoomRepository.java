package com.dinsaren.springbootjwtapi.repository.rental;

import com.dinsaren.springbootjwtapi.models.rental.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("rentalRoomRepository")
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByPropertyId(Long propertyId);
    boolean existsByPropertyIdAndRoomNumber(Long propertyId, String roomNumber);
    Optional<Room> findByIdAndPropertyId(Long id, Long propertyId);
}
