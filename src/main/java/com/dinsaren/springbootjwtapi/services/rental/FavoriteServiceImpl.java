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

            if (favoriteRepository.existsByStudentIdAndPropertyId(currentUser.getId(), req.getPropertyId())) {
                throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATE_FAVORITE, "Property is already in favorites");
            }
        }

        Room room = null;
        if (req.getRoomId() != null) {
            room = roomRepository.findById(req.getRoomId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Room not found"));

            if (favoriteRepository.existsByStudentIdAndRoomId(currentUser.getId(), req.getRoomId())) {
                throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATE_FAVORITE, "Room is already in favorites");
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
