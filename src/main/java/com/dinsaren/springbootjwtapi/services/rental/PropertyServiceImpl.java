package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.constants.ErrorCode;
import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.User;
import com.dinsaren.springbootjwtapi.models.rental.*;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreatePropertyRequest;
import com.dinsaren.springbootjwtapi.payload.request.PropertySearchRequest;
import com.dinsaren.springbootjwtapi.payload.request.UpdatePropertyRequest;
import com.dinsaren.springbootjwtapi.payload.request.VerifyPropertyRequest;
import com.dinsaren.springbootjwtapi.payload.response.PropertyResponse;
import com.dinsaren.springbootjwtapi.payload.response.PropertySummaryResponse;
import com.dinsaren.springbootjwtapi.repository.rental.PropertyRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final RentalAccessService rentalAccessService;

    @Override
    @Transactional
    public PropertyResponse createProperty(CreatePropertyRequest req) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        rentalAccessService.assertIsOwnerRole(currentUser);

        Property property = new Property();
        property.setOwner(currentUser);
        property.setName(req.getName());
        property.setDescription(req.getDescription());
        if (req.getPropertyType() != null) {
            property.setPropertyType(req.getPropertyType());
        }
        property.setAddress(req.getAddress());
        property.setCity(req.getCity());
        property.setDistrict(req.getDistrict());
        property.setLatitude(req.getLatitude());
        property.setLongitude(req.getLongitude());
        property.setStatus(PropertyStatus.DRAFT);
        property.setVerificationStatus(PropertyVerificationStatus.PENDING);
        property.setMainImage(req.getMainImage());
        property.setImages(req.getImages());
        property.setCreateAt(new Date());
        property.setCreateBy(currentUser.getUsername());

        Property saved = propertyRepository.save(property);
        log.info("Property created successfully with id {} by owner {}", saved.getId(), currentUser.getUsername());
        return PropertyResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyResponse getPropertyById(Long id) throws AppException {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found with id: " + id));
        return PropertyResponse.fromEntity(property);
    }

    @Override
    @Transactional
    public PropertyResponse updateProperty(Long id, UpdatePropertyRequest req) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found with id: " + id));

        rentalAccessService.assertPropertyOwner(property, currentUser);

        if (req.getName() != null && !req.getName().isBlank()) {
            property.setName(req.getName());
        }
        if (req.getDescription() != null) {
            property.setDescription(req.getDescription());
        }
        if (req.getPropertyType() != null) {
            property.setPropertyType(req.getPropertyType());
        }
        if (req.getAddress() != null) {
            property.setAddress(req.getAddress());
        }
        if (req.getCity() != null) {
            property.setCity(req.getCity());
        }
        if (req.getDistrict() != null) {
            property.setDistrict(req.getDistrict());
        }
        if (req.getLatitude() != null) {
            property.setLatitude(req.getLatitude());
        }
        if (req.getLongitude() != null) {
            property.setLongitude(req.getLongitude());
        }
        if (req.getStatus() != null) {
            // If owner tries to publish but property is suspended or rejected
            if (req.getStatus() == PropertyStatus.PUBLISHED && property.getStatus() == PropertyStatus.SUSPENDED) {
                throw new AppException(HttpStatus.FORBIDDEN, ErrorCode.INVALID_STATUS_TRANSITION, "Cannot publish a suspended property");
            }
            property.setStatus(req.getStatus());
        }
        if (req.getMainImage() != null) {
            property.setMainImage(req.getMainImage());
        }
        if (req.getImages() != null) {
            property.setImages(req.getImages());
        }

        property.setUpdateAt(new Date());
        property.setUpdateBy(currentUser.getUsername());

        Property updated = propertyRepository.save(property);
        log.info("Property {} updated by {}", id, currentUser.getUsername());
        return PropertyResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteProperty(Long id) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found with id: " + id));

        rentalAccessService.assertPropertyOwner(property, currentUser);
        propertyRepository.delete(property);
        log.info("Property {} deleted by {}", id, currentUser.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public PageRes<PropertySummaryResponse> searchProperties(PropertySearchRequest req) {
        int page = Math.max(0, req.getPage());
        int size = req.getSize() > 0 ? req.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));

        Specification<Property> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // By default, public search returns only PUBLISHED and VERIFIED
            predicates.add(cb.equal(root.get("status"), PropertyStatus.PUBLISHED));
            predicates.add(cb.equal(root.get("verificationStatus"), PropertyVerificationStatus.VERIFIED));

            if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
                String pattern = "%" + req.getKeyword().trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), pattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), pattern);
                Predicate addressLike = cb.like(cb.lower(root.get("address")), pattern);
                predicates.add(cb.or(nameLike, descLike, addressLike));
            }

            if (req.getCity() != null && !req.getCity().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), req.getCity().trim().toLowerCase()));
            }

            if (req.getDistrict() != null && !req.getDistrict().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("district")), req.getDistrict().trim().toLowerCase()));
            }

            // Room level criteria
            boolean hasRoomCriteria = req.getMinPrice() != null || req.getMaxPrice() != null
                    || req.getRoomType() != null || req.getAvailable() != null || (req.getFacility() != null && !req.getFacility().isBlank());

            if (hasRoomCriteria) {
                Join<Property, Room> roomJoin = root.join("rooms", JoinType.INNER);
                if (req.getMinPrice() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(roomJoin.get("price"), req.getMinPrice()));
                }
                if (req.getMaxPrice() != null) {
                    predicates.add(cb.lessThanOrEqualTo(roomJoin.get("price"), req.getMaxPrice()));
                }
                if (req.getRoomType() != null) {
                    predicates.add(cb.equal(roomJoin.get("roomType"), req.getRoomType()));
                }
                if (req.getAvailable() != null) {
                    predicates.add(cb.equal(roomJoin.get("available"), req.getAvailable()));
                }
                if (req.getFacility() != null && !req.getFacility().isBlank()) {
                    Join<Room, Facility> facilityJoin = roomJoin.join("facilities", JoinType.INNER);
                    predicates.add(cb.equal(cb.upper(facilityJoin.get("name")), req.getFacility().trim().toUpperCase()));
                }
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Property> propertyPage = propertyRepository.findAll(spec, pageable);
        Page<PropertySummaryResponse> dtoPage = propertyPage.map(PropertySummaryResponse::fromEntity);
        return PageRes.of(dtoPage);
    }

    @Override
    @Transactional
    public PropertyResponse verifyProperty(Long id, VerifyPropertyRequest req) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        rentalAccessService.assertIsAdminRole(currentUser);

        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found with id: " + id));

        property.setVerificationStatus(req.getVerificationStatus());
        if (req.getStatus() != null) {
            property.setStatus(req.getStatus());
        } else if (req.getVerificationStatus() == PropertyVerificationStatus.VERIFIED) {
            property.setStatus(PropertyStatus.PUBLISHED);
        }
        property.setUpdateAt(new Date());
        property.setUpdateBy(currentUser.getUsername());

        Property updated = propertyRepository.save(property);
        log.info("Property {} verified by admin {} with status {}", id, currentUser.getUsername(), req.getVerificationStatus());
        return PropertyResponse.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PageRes<PropertySummaryResponse> getMyProperties(int page, int size) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        rentalAccessService.assertIsOwnerRole(currentUser);

        int p = Math.max(0, page);
        int s = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "createAt"));

        Page<Property> propertyPage = propertyRepository.findByOwnerId(currentUser.getId(), pageable);
        return PageRes.of(propertyPage.map(PropertySummaryResponse::fromEntity));
    }
}
