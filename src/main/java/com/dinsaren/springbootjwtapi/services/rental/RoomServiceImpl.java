package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.constants.ErrorCode;
import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.User;
import com.dinsaren.springbootjwtapi.models.rental.Facility;
import com.dinsaren.springbootjwtapi.models.rental.Property;
import com.dinsaren.springbootjwtapi.models.rental.Room;
import com.dinsaren.springbootjwtapi.payload.request.CreateRoomRequest;
import com.dinsaren.springbootjwtapi.payload.request.UpdateRoomRequest;
import com.dinsaren.springbootjwtapi.payload.response.RoomResponse;
import com.dinsaren.springbootjwtapi.repository.rental.FacilityRepository;
import com.dinsaren.springbootjwtapi.repository.rental.PropertyRepository;
import com.dinsaren.springbootjwtapi.repository.rental.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final PropertyRepository propertyRepository;
    private final FacilityRepository facilityRepository;
    private final RentalAccessService rentalAccessService;

    @Override
    @Transactional
    public RoomResponse createRoom(Long propertyId, CreateRoomRequest req) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found with id: " + propertyId));

        rentalAccessService.assertPropertyOwner(property, currentUser);

        if (roomRepository.existsByPropertyIdAndRoomNumber(propertyId, req.getRoomNumber())) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATE_ROOM_NUMBER,
                    "Room number '" + req.getRoomNumber() + "' already exists in this property");
        }

        Room room = new Room();
        room.setProperty(property);
        room.setRoomNumber(req.getRoomNumber());
        room.setTitle(req.getTitle());
        room.setDescription(req.getDescription());
        room.setPrice(req.getPrice());
        if (req.getRoomType() != null) {
            room.setRoomType(req.getRoomType());
        }
        if (req.getGenderPreference() != null) {
            room.setGenderPreference(req.getGenderPreference());
        }
        room.setAvailable(req.getAvailable() != null ? req.getAvailable() : true);
        room.setFloor(req.getFloor());
        room.setArea(req.getArea());
        room.setImages(req.getImages());
        room.setCreateAt(new Date());
        room.setCreateBy(currentUser.getUsername());

        if (req.getFacilityIds() != null && !req.getFacilityIds().isEmpty()) {
            List<Facility> facilities = facilityRepository.findAllById(req.getFacilityIds());
            room.setFacilities(new HashSet<>(facilities));
        }

        Room saved = roomRepository.save(room);
        log.info("Room {} created for property {} by {}", saved.getId(), propertyId, currentUser.getUsername());
        return RoomResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long id) throws AppException {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Room not found with id: " + id));
        return RoomResponse.fromEntity(room);
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(Long id, UpdateRoomRequest req) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Room not found with id: " + id));

        rentalAccessService.assertPropertyOwner(room.getProperty(), currentUser);

        if (req.getRoomNumber() != null && !req.getRoomNumber().equals(room.getRoomNumber())) {
            if (roomRepository.existsByPropertyIdAndRoomNumber(room.getProperty().getId(), req.getRoomNumber())) {
                throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATE_ROOM_NUMBER,
                        "Room number '" + req.getRoomNumber() + "' already exists in this property");
            }
            room.setRoomNumber(req.getRoomNumber());
        }

        if (req.getTitle() != null && !req.getTitle().isBlank()) {
            room.setTitle(req.getTitle());
        }
        if (req.getDescription() != null) {
            room.setDescription(req.getDescription());
        }
        if (req.getPrice() != null) {
            room.setPrice(req.getPrice());
        }
        if (req.getRoomType() != null) {
            room.setRoomType(req.getRoomType());
        }
        if (req.getGenderPreference() != null) {
            room.setGenderPreference(req.getGenderPreference());
        }
        if (req.getAvailable() != null) {
            room.setAvailable(req.getAvailable());
        }
        if (req.getFloor() != null) {
            room.setFloor(req.getFloor());
        }
        if (req.getArea() != null) {
            room.setArea(req.getArea());
        }
        if (req.getImages() != null) {
            room.setImages(req.getImages());
        }
        if (req.getFacilityIds() != null) {
            List<Facility> facilities = facilityRepository.findAllById(req.getFacilityIds());
            room.setFacilities(new HashSet<>(facilities));
        }

        room.setUpdateAt(new Date());
        room.setUpdateBy(currentUser.getUsername());

        Room updated = roomRepository.save(room);
        log.info("Room {} updated by {}", id, currentUser.getUsername());
        return RoomResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Room not found with id: " + id));

        rentalAccessService.assertPropertyOwner(room.getProperty(), currentUser);
        roomRepository.delete(room);
        log.info("Room {} deleted by {}", id, currentUser.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByProperty(Long propertyId) throws AppException {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found with id: " + propertyId));

        return roomRepository.findByPropertyId(property.getId()).stream()
                .map(RoomResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
