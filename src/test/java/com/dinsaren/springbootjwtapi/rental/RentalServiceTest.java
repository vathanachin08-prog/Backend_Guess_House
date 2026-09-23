package com.dinsaren.springbootjwtapi.rental;

import com.dinsaren.springbootjwtapi.constants.ErrorCode;
import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.Role;
import com.dinsaren.springbootjwtapi.models.User;
import com.dinsaren.springbootjwtapi.models.UserRole;
import com.dinsaren.springbootjwtapi.models.rental.*;
import com.dinsaren.springbootjwtapi.payload.request.CreatePropertyRequest;
import com.dinsaren.springbootjwtapi.payload.request.CreateReviewRequest;
import com.dinsaren.springbootjwtapi.payload.request.CreateRoomRequest;
import com.dinsaren.springbootjwtapi.payload.request.CreateVisitRequest;
import com.dinsaren.springbootjwtapi.repository.rental.PropertyRepository;
import com.dinsaren.springbootjwtapi.repository.rental.ReviewRepository;
import com.dinsaren.springbootjwtapi.repository.rental.RoomRepository;
import com.dinsaren.springbootjwtapi.repository.rental.VisitRequestRepository;
import com.dinsaren.springbootjwtapi.services.rental.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private VisitRequestRepository visitRequestRepository;

    @Mock
    private RentalAccessService rentalAccessService;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    @InjectMocks
    private RoomServiceImpl roomService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @InjectMocks
    private VisitRequestServiceImpl visitRequestService;

    private User owner;
    private User student;
    private Property property;

    @BeforeEach
    void setUp() {
        owner = new User("owner1", "owner@test.com", "pass", "012345678");
        owner.setId(1);
        owner.setRoles(Set.of(new Role(UserRole.ROLE_OWNER)));

        student = new User("student1", "student@test.com", "pass", "098765432");
        student.setId(2);
        student.setRoles(Set.of(new Role(UserRole.ROLE_STUDENT)));

        property = new Property();
        property.setId(100L);
        property.setName("Green House");
        property.setOwner(owner);
        property.setStatus(PropertyStatus.PUBLISHED);
        property.setVerificationStatus(PropertyVerificationStatus.VERIFIED);
    }

    @Test
    void testCreatePropertyAsStudent_ThrowsException() throws AppException {
        when(rentalAccessService.getCurrentUser()).thenReturn(student);
        doThrow(new AppException(org.springframework.http.HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, "Only property owners can perform this action"))
                .when(rentalAccessService).assertIsOwnerRole(student);

        CreatePropertyRequest req = new CreatePropertyRequest();
        req.setName("Student Attempt");

        AppException ex = assertThrows(AppException.class, () -> propertyService.createProperty(req));
        assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    }

    @Test
    void testUpdatePropertyByDifferentOwner_ThrowsException() throws AppException {
        User anotherOwner = new User("owner2", "owner2@test.com", "pass", "011111111");
        anotherOwner.setId(99);

        when(rentalAccessService.getCurrentUser()).thenReturn(anotherOwner);
        when(propertyRepository.findById(100L)).thenReturn(Optional.of(property));
        doThrow(new AppException(org.springframework.http.HttpStatus.FORBIDDEN, ErrorCode.NOT_PROPERTY_OWNER, "You are not the owner of this property"))
                .when(rentalAccessService).assertPropertyOwner(property, anotherOwner);

        com.dinsaren.springbootjwtapi.payload.request.UpdatePropertyRequest req = new com.dinsaren.springbootjwtapi.payload.request.UpdatePropertyRequest();
        req.setName("Hacked Name");

        AppException ex = assertThrows(AppException.class, () -> propertyService.updateProperty(100L, req));
        assertEquals(ErrorCode.NOT_PROPERTY_OWNER, ex.getErrorCode());
    }

    @Test
    void testDuplicateRoomNumberInSameProperty_ThrowsException() throws AppException {
        when(rentalAccessService.getCurrentUser()).thenReturn(owner);
        when(propertyRepository.findById(100L)).thenReturn(Optional.of(property));
        when(roomRepository.existsByPropertyIdAndRoomNumber(100L, "101")).thenReturn(true);

        CreateRoomRequest req = new CreateRoomRequest();
        req.setRoomNumber("101");
        req.setTitle("Room 101");
        req.setPrice(BigDecimal.valueOf(150));

        AppException ex = assertThrows(AppException.class, () -> roomService.createRoom(100L, req));
        assertEquals(ErrorCode.DUPLICATE_ROOM_NUMBER, ex.getErrorCode());
    }

    @Test
    void testInvalidReviewRating_ThrowsException() throws AppException {
        when(rentalAccessService.getCurrentUser()).thenReturn(student);
        when(propertyRepository.findById(100L)).thenReturn(Optional.of(property));

        CreateReviewRequest req = new CreateReviewRequest();
        req.setRating(6); // Invalid (> 5)
        req.setComment("Invalid rating test");

        AppException ex = assertThrows(AppException.class, () -> reviewService.createReview(100L, req));
        assertEquals(ErrorCode.VALIDATION_ERROR, ex.getErrorCode());
    }

    @Test
    void testDuplicateReviewBySameStudent_ThrowsException() throws AppException {
        when(rentalAccessService.getCurrentUser()).thenReturn(student);
        when(propertyRepository.findById(100L)).thenReturn(Optional.of(property));
        when(reviewRepository.existsByStudentIdAndPropertyId(student.getId(), 100L)).thenReturn(true);

        CreateReviewRequest req = new CreateReviewRequest();
        req.setRating(5);
        req.setComment("Great place");

        AppException ex = assertThrows(AppException.class, () -> reviewService.createReview(100L, req));
        assertEquals(ErrorCode.INVALID_REVIEW, ex.getErrorCode());
    }

    @Test
    void testVisitRequestDateInPast_ThrowsException() throws AppException {
        when(rentalAccessService.getCurrentUser()).thenReturn(student);
        when(propertyRepository.findById(100L)).thenReturn(Optional.of(property));

        CreateVisitRequest req = new CreateVisitRequest();
        req.setPropertyId(100L);
        req.setRequestedDate(LocalDate.now().minusDays(1)); // Past date

        AppException ex = assertThrows(AppException.class, () -> visitRequestService.createVisitRequest(req));
        assertEquals(ErrorCode.INVALID_VISIT_REQUEST, ex.getErrorCode());
    }

    @Test
    void testInvalidVisitStatusTransition_ThrowsException() throws AppException {
        when(rentalAccessService.getCurrentUser()).thenReturn(owner);

        VisitRequest vr = new VisitRequest();
        vr.setId(10L);
        vr.setProperty(property);
        vr.setStatus(VisitRequestStatus.PENDING);

        when(visitRequestRepository.findById(10L)).thenReturn(Optional.of(vr));

        // Attempt to complete a request that is PENDING (only ACCEPTED can be COMPLETED)
        AppException ex = assertThrows(AppException.class, () -> visitRequestService.completeVisitRequest(10L));
        assertEquals(ErrorCode.INVALID_STATUS_TRANSITION, ex.getErrorCode());
    }
}
