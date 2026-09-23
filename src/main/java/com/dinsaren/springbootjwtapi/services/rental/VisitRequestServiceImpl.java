package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.constants.ErrorCode;
import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.User;
import com.dinsaren.springbootjwtapi.models.rental.Property;
import com.dinsaren.springbootjwtapi.models.rental.Room;
import com.dinsaren.springbootjwtapi.models.rental.VisitRequest;
import com.dinsaren.springbootjwtapi.models.rental.VisitRequestStatus;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateVisitRequest;
import com.dinsaren.springbootjwtapi.payload.response.VisitRequestResponse;
import com.dinsaren.springbootjwtapi.repository.rental.PropertyRepository;
import com.dinsaren.springbootjwtapi.repository.rental.RoomRepository;
import com.dinsaren.springbootjwtapi.repository.rental.VisitRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisitRequestServiceImpl implements VisitRequestService {

    private final VisitRequestRepository visitRequestRepository;
    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;
    private final RentalAccessService rentalAccessService;

    @Override
    @Transactional
    public VisitRequestResponse createVisitRequest(CreateVisitRequest req) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();

        Property property = propertyRepository.findById(req.getPropertyId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found"));

        if (req.getRequestedDate().isBefore(LocalDate.now())) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_VISIT_REQUEST, "Requested date cannot be in the past");
        }

        Room room = null;
        if (req.getRoomId() != null) {
            room = roomRepository.findById(req.getRoomId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Room not found"));
        }

        VisitRequest vr = new VisitRequest();
        vr.setStudent(currentUser);
        vr.setProperty(property);
        vr.setRoom(room);
        vr.setRequestedDate(req.getRequestedDate());
        vr.setRequestedTime(req.getRequestedTime());
        vr.setMessage(req.getMessage());
        vr.setStatus(VisitRequestStatus.PENDING);
        vr.setCreateAt(new Date());
        vr.setCreateBy(currentUser.getUsername());

        VisitRequest saved = visitRequestRepository.save(vr);
        log.info("Visit request {} created by student {} for property {}", saved.getId(), currentUser.getUsername(), property.getId());
        return VisitRequestResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageRes<VisitRequestResponse> getMyVisitRequests(int page, int size) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        int p = Math.max(0, page);
        int s = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "createAt"));

        Page<VisitRequest> pageResult = visitRequestRepository.findByStudentId(currentUser.getId(), pageable);
        return PageRes.of(pageResult.map(VisitRequestResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PageRes<VisitRequestResponse> getOwnerVisitRequests(Long propertyId, int page, int size) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        int p = Math.max(0, page);
        int s = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "createAt"));

        if (propertyId != null) {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found"));
            rentalAccessService.assertPropertyOwner(property, currentUser);
            Page<VisitRequest> pageResult = visitRequestRepository.findByPropertyId(propertyId, pageable);
            return PageRes.of(pageResult.map(VisitRequestResponse::fromEntity));
        } else {
            Page<VisitRequest> pageResult = visitRequestRepository.findByPropertyOwnerId(currentUser.getId(), pageable);
            return PageRes.of(pageResult.map(VisitRequestResponse::fromEntity));
        }
    }

    @Override
    @Transactional
    public VisitRequestResponse acceptVisitRequest(Long id) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        VisitRequest vr = visitRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Visit request not found"));

        rentalAccessService.assertPropertyOwner(vr.getProperty(), currentUser);

        if (vr.getStatus() != VisitRequestStatus.PENDING) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_STATUS_TRANSITION,
                    "Cannot accept visit request with status " + vr.getStatus());
        }

        vr.setStatus(VisitRequestStatus.ACCEPTED);
        vr.setUpdateAt(new Date());
        vr.setUpdateBy(currentUser.getUsername());

        return VisitRequestResponse.fromEntity(visitRequestRepository.save(vr));
    }

    @Override
    @Transactional
    public VisitRequestResponse rejectVisitRequest(Long id) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        VisitRequest vr = visitRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Visit request not found"));

        rentalAccessService.assertPropertyOwner(vr.getProperty(), currentUser);

        if (vr.getStatus() != VisitRequestStatus.PENDING && vr.getStatus() != VisitRequestStatus.ACCEPTED) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_STATUS_TRANSITION,
                    "Cannot reject visit request with status " + vr.getStatus());
        }

        vr.setStatus(VisitRequestStatus.REJECTED);
        vr.setUpdateAt(new Date());
        vr.setUpdateBy(currentUser.getUsername());

        return VisitRequestResponse.fromEntity(visitRequestRepository.save(vr));
    }

    @Override
    @Transactional
    public VisitRequestResponse cancelVisitRequest(Long id) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        VisitRequest vr = visitRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Visit request not found"));

        if (vr.getStudent().getId() != currentUser.getId()) {
            throw new AppException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, "You can only cancel your own visit requests");
        }

        if (vr.getStatus() == VisitRequestStatus.CANCELLED || vr.getStatus() == VisitRequestStatus.COMPLETED) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_STATUS_TRANSITION,
                    "Cannot cancel visit request with status " + vr.getStatus());
        }

        vr.setStatus(VisitRequestStatus.CANCELLED);
        vr.setUpdateAt(new Date());
        vr.setUpdateBy(currentUser.getUsername());

        return VisitRequestResponse.fromEntity(visitRequestRepository.save(vr));
    }

    @Override
    @Transactional
    public VisitRequestResponse completeVisitRequest(Long id) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        VisitRequest vr = visitRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Visit request not found"));

        rentalAccessService.assertPropertyOwner(vr.getProperty(), currentUser);

        if (vr.getStatus() != VisitRequestStatus.ACCEPTED) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_STATUS_TRANSITION,
                    "Can only mark as completed if current status is ACCEPTED");
        }

        vr.setStatus(VisitRequestStatus.COMPLETED);
        vr.setUpdateAt(new Date());
        vr.setUpdateBy(currentUser.getUsername());

        return VisitRequestResponse.fromEntity(visitRequestRepository.save(vr));
    }
}
