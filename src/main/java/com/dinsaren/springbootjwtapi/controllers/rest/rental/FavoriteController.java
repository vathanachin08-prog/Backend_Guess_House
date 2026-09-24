package com.dinsaren.springbootjwtapi.controllers.rest.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.res.MessageRes;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateFavoriteRequest;
import com.dinsaren.springbootjwtapi.payload.response.FavoriteResponse;
import com.dinsaren.springbootjwtapi.services.rental.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/app/favorites")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Rental - Favorites", description = "Student favorite listings")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "Add favorite", description = "Save a property or room as a student favorite")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saved to favorites"),
            @ApiResponse(responseCode = "400", description = "Already in favorites or invalid input")
    })
    @PostMapping
    public ResponseEntity<MessageRes> addFavorite(@Valid @RequestBody CreateFavoriteRequest req) {
        MessageRes res = new MessageRes();
        try {
            FavoriteResponse data = favoriteService.addFavorite(req);
            res.setCreateSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error adding favorite", e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Remove favorite", description = "Remove an item from favorites by favorite ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageRes> removeFavorite(@PathVariable Long id) {
        MessageRes res = new MessageRes();
        try {
            favoriteService.removeFavorite(id);
            res.setSuccess("Favorite removed successfully");
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error removing favorite " + id, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Remove favorite by property ID", description = "Remove an item from favorites by property ID")
    @DeleteMapping("/property/{propertyId}")
    public ResponseEntity<MessageRes> removeFavoriteByProperty(@PathVariable Long propertyId) {
        MessageRes res = new MessageRes();
        try {
            favoriteService.removeFavoriteByProperty(propertyId);
            res.setSuccess("Favorite removed successfully");
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error removing favorite for property " + propertyId, e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @Operation(summary = "Get my favorites", description = "List all favorites saved by the current student")
    @GetMapping
    public ResponseEntity<MessageRes> getMyFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        MessageRes res = new MessageRes();
        try {
            PageRes<FavoriteResponse> data = favoriteService.getMyFavorites(page, size);
            res.setSuccess(data);
            return ResponseEntity.ok(res);
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new MessageRes(e.getErrorCode(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error getting favorites", e);
            res.setInternalServer();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
}
