package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.constants.ErrorCode;
import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.User;
import com.dinsaren.springbootjwtapi.models.rental.Floor;
import com.dinsaren.springbootjwtapi.models.rental.Property;
import com.dinsaren.springbootjwtapi.models.rental.Room;
import com.dinsaren.springbootjwtapi.payload.request.CreateFloorRequest;
import com.dinsaren.springbootjwtapi.payload.response.FloorResponse;
import com.dinsaren.springbootjwtapi.repository.rental.PropertyRepository;
import com.dinsaren.springbootjwtapi.repository.rental.RentalFloorRepository;
import com.dinsaren.springbootjwtapi.repository.rental.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FloorServiceImpl implements FloorService {

    private final RentalFloorRepository rentalFloorRepository;
    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;
    private final RentalAccessService rentalAccessService;

    @Override
    @Transactional(readOnly = true)
    public List<FloorResponse> getFloorsByProperty(Long propertyId) throws AppException {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found with id: " + propertyId));

        List<Floor> floors = rentalFloorRepository.findByPropertyIdAndStatusOrderByFloorOrderAsc(propertyId, "ACTIVE");
        List<Room> rooms = roomRepository.findByPropertyId(propertyId);

        List<FloorResponse> responses = new ArrayList<>();
        for (Floor floor : floors) {
            FloorResponse res = FloorResponse.fromEntity(floor);

            // Calculate room statistics for this floor
            int total = 0;
            int occupied = 0;
            for (Room room : rooms) {
                boolean match = false;
                if (room.getFloor() != null && floor.getFloorOrder() != null && room.getFloor().equals(floor.getFloorOrder())) {
                    match = true;
                } else if (floor.getName() != null && floor.getName().contains(String.valueOf(room.getFloor()))) {
                    match = true;
                }

                if (match) {
                    total++;
                    if (Boolean.FALSE.equals(room.getAvailable())) {
                        occupied++;
                    }
                }
            }

            res.setTotalRooms(total);
            res.setOccupiedRooms(occupied);
            responses.add(res);
        }

        return responses;
    }

    @Override
    @Transactional
    public FloorResponse createFloor(Long propertyId, CreateFloorRequest request) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found with id: " + propertyId));

        rentalAccessService.assertPropertyOwner(property, currentUser);

        String trimmedName = request.getName().trim();
        if (rentalFloorRepository.existsByPropertyIdAndName(propertyId, trimmedName)) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                    "Floor with name '" + trimmedName + "' already exists for this property");
        }

        Integer order = request.getFloorOrder();
        if (order == null || order <= 0) {
            List<Floor> existing = rentalFloorRepository.findByPropertyId(propertyId);
            order = existing.size() + 1;
        }

        Floor floor = new Floor();
        floor.setProperty(property);
        floor.setName(trimmedName);
        floor.setFloorOrder(order);
        floor.setStatus("ACTIVE");

        Floor saved = rentalFloorRepository.save(floor);
        log.info("Created new floor '{}' (id: {}) for property id: {}", saved.getName(), saved.getId(), propertyId);
        return FloorResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public void deleteFloor(Long floorId) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        Floor floor = rentalFloorRepository.findById(floorId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Floor not found with id: " + floorId));

        if (floor.getProperty() != null) {
            rentalAccessService.assertPropertyOwner(floor.getProperty(), currentUser);
        }

        floor.setStatus("INACTIVE");
        rentalFloorRepository.save(floor);
        log.info("Floor id {} marked as INACTIVE by user {}", floorId, currentUser.getUsername());
    }
}
