package com.dinsaren.springbootjwtapi.services.rental;

import com.dinsaren.springbootjwtapi.constants.ErrorCode;
import com.dinsaren.springbootjwtapi.exception.AppException;
import com.dinsaren.springbootjwtapi.models.User;
import com.dinsaren.springbootjwtapi.models.UserRole;
import com.dinsaren.springbootjwtapi.models.rental.Property;
import com.dinsaren.springbootjwtapi.services.AuthenticationUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RentalAccessService {

    private final AuthenticationUtilService authenticationUtilService;

    public User getCurrentUser() throws AppException {
        User user = authenticationUtilService.checkUser();
        if (user == null) {
            throw new AppException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, "User is not authenticated");
        }
        return user;
    }

    public boolean hasRole(User user, UserRole role) {
        if (user == null || user.getRoles() == null) return false;
        return user.getRoles().stream().anyMatch(r -> r.getName() == role);
    }

    public void assertIsOwnerRole(User user) throws AppException {
        if (!hasRole(user, UserRole.ROLE_OWNER) && !hasRole(user, UserRole.ROLE_ADMIN)) {
            throw new AppException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, "Only property owners can perform this action");
        }
    }

    public void assertIsAdminRole(User user) throws AppException {
        if (!hasRole(user, UserRole.ROLE_ADMIN)) {
            throw new AppException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, "Only administrators can perform this action");
        }
    }

    public void assertPropertyOwner(Property property, User user) throws AppException {
        if (property == null) {
            throw new AppException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Property not found");
        }
        if (property.getOwner() == null || property.getOwner().getId() != user.getId()) {
            if (!hasRole(user, UserRole.ROLE_ADMIN)) {
                throw new AppException(HttpStatus.FORBIDDEN, ErrorCode.NOT_PROPERTY_OWNER, "You are not the owner of this property");
            }
        }
    }
}
