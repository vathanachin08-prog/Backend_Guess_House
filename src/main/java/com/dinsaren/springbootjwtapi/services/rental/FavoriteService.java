package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.res.PageRes;
import com.dinsaren.springbootjwtapi.payload.request.CreateFavoriteRequest;
import com.dinsaren.springbootjwtapi.payload.response.FavoriteResponse;

public interface FavoriteService {
    FavoriteResponse addFavorite(CreateFavoriteRequest req) throws AppException;
    void removeFavorite(Long id) throws AppException;
    PageRes<FavoriteResponse> getMyFavorites(int page, int size) throws AppException;
}
