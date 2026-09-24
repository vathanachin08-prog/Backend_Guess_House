package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.constants.ErrorCode;
import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.User;
import com.dinsaren.springbootjwtapi.models.rental.Favorite;
import com.dinsaren.springbootjwtapi.models.rental.Property;
import com.dinsaren.springbootjwtapi.models.rental.Room;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateFavoriteRequest;
import com.dinsaren.springbootjwtapi.payload.response.FavoriteResponse;
import com.dinsaren.springbootjwtapi.repository.rental.FavoriteRepository;
import com.dinsaren.springbootjwtapi.repository.rental.PropertyRepository;
import com.dinsaren.springbootjwtapi.repository.rental.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;
    private final RentalAccessService rentalAccessService;

    @Override
    @Transactional
    public FavoriteResponse addFavorite(CreateFavoriteRequest req) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();

        if (req.getPropertyId() == null && req.getRoomId() == null) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, "Either propertyId or roomId must be provided");
        }

        Property property = null;
        if (req.getPropertyId() != null) {
            property = propertyRepository.findById(req.getPropertyId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found"));

            Optional<Favorite> existing = favoriteRepository.findByStudentIdAndPropertyId(currentUser.getId(), req.getPropertyId());
            if (existing.isPresent()) {
                log.info("Property {} is already in favorites for student {}, returning existing favorite", req.getPropertyId(), currentUser.getUsername());
                return FavoriteResponse.fromEntity(existing.get());
            }
        }

        Room room = null;
        if (req.getRoomId() != null) {
            room = roomRepository.findById(req.getRoomId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Room not found"));

            Optional<Favorite> existingRoom = favoriteRepository.findByStudentIdAndRoomId(currentUser.getId(), req.getRoomId());
            if (existingRoom.isPresent()) {
                log.info("Room {} is already in favorites for student {}, returning existing favorite", req.getRoomId(), currentUser.getUsername());
                return FavoriteResponse.fromEntity(existingRoom.get());
            }
        }

        Favorite favorite = new Favorite(currentUser, property, room);
        Favorite saved = favoriteRepository.save(favorite);
        log.info("Favorite saved with id {} for student {}", saved.getId(), currentUser.getUsername());
        return FavoriteResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public void removeFavorite(Long id) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        Favorite favorite = favoriteRepository.findByIdAndStudentId(id, currentUser.getId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Favorite not found"));

        favoriteRepository.delete(favorite);
        log.info("Favorite {} deleted for student {}", id, currentUser.getUsername());
    }

    @Override
    @Transactional
    public void removeFavoriteByProperty(Long propertyId) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        Optional<Favorite> fav = favoriteRepository.findByStudentIdAndPropertyId(currentUser.getId(), propertyId);
        if (fav.isPresent()) {
            favoriteRepository.delete(fav.get());
            log.info("Favorite for property {} deleted for student {}", propertyId, currentUser.getUsername());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageRes<FavoriteResponse> getMyFavorites(int page, int size) throws AppException {
        User currentUser = rentalAccessService.getCurrentUser();
        int p = Math.max(0, page);
        int s = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Favorite> favorites = favoriteRepository.findByStudentId(currentUser.getId(), pageable);
        return PageRes.of(favorites.map(FavoriteResponse::fromEntity));
    }
}
