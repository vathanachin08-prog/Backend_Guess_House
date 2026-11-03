package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.constants.ErrorCode;
import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.User;
import com.dinsaren.springbootjwtapi.models.rental.Property;
import com.dinsaren.springbootjwtapi.models.rental.Room;
import com.dinsaren.springbootjwtapi.models.rental.Tenant;
import com.dinsaren.springbootjwtapi.payload.request.CreateTenantRequest;
import com.dinsaren.springbootjwtapi.payload.request.UpdateTenantRequest;
import com.dinsaren.springbootjwtapi.payload.response.TenantResponse;
import com.dinsaren.springbootjwtapi.repository.rental.PropertyRepository;
import com.dinsaren.springbootjwtapi.repository.rental.RentalTenantRepository;
import com.dinsaren.springbootjwtapi.repository.rental.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final RentalTenantRepository rentalTenantRepository;
    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;
    private final RentalAccessService rentalAccessService;

    @Override
    @Transactional(readOnly = true)
    public List<TenantResponse> getTenantsByProperty(Long propertyId) throws AppException {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found with id: " + propertyId));

        List<Tenant> tenants = rentalTenantRepository.findByPropertyIdOrderByIdDesc(propertyId);
        return tenants.stream()
                .map(TenantResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TenantResponse createTenant(Long propertyId, CreateTenantRequest req) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found with id: " + propertyId));

        rentalAccessService.assertPropertyOwner(property, currentUser);

        String trimmedRoomNumber = req.getRoomNumber() != null ? req.getRoomNumber().trim() : "";

        Tenant tenant = new Tenant();
        tenant.setProperty(property);
        tenant.setName(req.getName().trim());
        tenant.setRoomNumber(trimmedRoomNumber);
        tenant.setFloor(req.getFloor() != null ? req.getFloor().trim() : null);
        tenant.setPhoneNumber(req.getPhoneNumber() != null ? req.getPhoneNumber().trim() : null);
        tenant.setEmail(req.getEmail() != null ? req.getEmail().trim() : null);
        tenant.setStatus(req.getStatus() != null && !req.getStatus().isBlank() ? req.getStatus() : "ACTIVE");
        tenant.setJoinedDate(LocalDateTime.now());

        // Link with room entity if room number matches
        Optional<Room> roomOpt = roomRepository.findByPropertyIdAndRoomNumber(propertyId, trimmedRoomNumber);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            tenant.setRoom(room);
            if (tenant.getFloor() == null || tenant.getFloor().isBlank()) {
                if (room.getFloor() != null) {
                    tenant.setFloor(room.getFloor() == 1 ? "ជាន់ផ្ទាល់ដី" : "ជាន់ទី" + (room.getFloor() - 1));
                }
            }
            // Mark room occupied
            room.setAvailable(false);
            roomRepository.save(room);
        }

        Tenant saved = rentalTenantRepository.save(tenant);
        log.info("Created tenant id {} ('{}') for property id {}", saved.getId(), saved.getName(), propertyId);
        return TenantResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public TenantResponse updateTenant(Long id, UpdateTenantRequest req) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        Tenant tenant = rentalTenantRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Tenant not found with id: " + id));

        if (tenant.getProperty() != null) {
            rentalAccessService.assertPropertyOwner(tenant.getProperty(), currentUser);
        }

        String oldRoomNumber = tenant.getRoomNumber();
        String newRoomNumber = req.getRoomNumber() != null ? req.getRoomNumber().trim() : "";

        tenant.setName(req.getName().trim());
        tenant.setRoomNumber(newRoomNumber);
        if (req.getFloor() != null) {
            tenant.setFloor(req.getFloor().trim());
        }
        if (req.getPhoneNumber() != null) {
            tenant.setPhoneNumber(req.getPhoneNumber().trim());
        }
        if (req.getEmail() != null) {
            tenant.setEmail(req.getEmail().trim());
        }
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            tenant.setStatus(req.getStatus().trim());
        }

        // Check if room changed
        if (tenant.getProperty() != null && !newRoomNumber.equalsIgnoreCase(oldRoomNumber)) {
            // Find new room
            Optional<Room> newRoomOpt = roomRepository.findByPropertyIdAndRoomNumber(tenant.getProperty().getId(), newRoomNumber);
            if (newRoomOpt.isPresent()) {
                Room newRoom = newRoomOpt.get();
                tenant.setRoom(newRoom);
                newRoom.setAvailable(false);
                roomRepository.save(newRoom);
            } else {
                tenant.setRoom(null);
            }

            // Free old room if no other tenant occupies it
            if (oldRoomNumber != null && !oldRoomNumber.isBlank()) {
                Optional<Room> oldRoomOpt = roomRepository.findByPropertyIdAndRoomNumber(tenant.getProperty().getId(), oldRoomNumber);
                if (oldRoomOpt.isPresent()) {
                    Room oldRoom = oldRoomOpt.get();
                    boolean stillOccupied = rentalTenantRepository.existsByPropertyIdAndRoomNumberAndStatus(
                            tenant.getProperty().getId(), oldRoomNumber, "ACTIVE");
                    if (!stillOccupied) {
                        oldRoom.setAvailable(true);
                        roomRepository.save(oldRoom);
                    }
                }
            }
        }

        Tenant updated = rentalTenantRepository.save(tenant);
        log.info("Updated tenant id {} for property id {}", updated.getId(), updated.getProperty() != null ? updated.getProperty().getId() : null);
        return TenantResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteTenant(Long id) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        Tenant tenant = rentalTenantRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Tenant not found with id: " + id));

        if (tenant.getProperty() != null) {
            rentalAccessService.assertPropertyOwner(tenant.getProperty(), currentUser);
        }

        String roomNumber = tenant.getRoomNumber();
        Long propertyId = tenant.getProperty() != null ? tenant.getProperty().getId() : null;

        rentalTenantRepository.delete(tenant);
        log.info("Deleted tenant id {} by user {}", id, currentUser.getUsername());

        // Free room if no other active tenant is occupying it
        if (propertyId != null && roomNumber != null && !roomNumber.isBlank()) {
            boolean stillOccupied = rentalTenantRepository.existsByPropertyIdAndRoomNumberAndStatus(propertyId, roomNumber, "ACTIVE");
            if (!stillOccupied) {
                Optional<Room> roomOpt = roomRepository.findByPropertyIdAndRoomNumber(propertyId, roomNumber);
                if (roomOpt.isPresent()) {
                    Room room = roomOpt.get();
                    room.setAvailable(true);
                    roomRepository.save(room);
                }
            }
        }
    }
}

