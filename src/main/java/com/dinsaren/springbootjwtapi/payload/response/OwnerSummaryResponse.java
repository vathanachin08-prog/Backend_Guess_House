package com.dinsaren.springbootjwtapi.payload.response;

import com.dinsaren.springbootjwtapi.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerSummaryResponse {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profile;

    public static OwnerSummaryResponse fromEntity(User user) {
        if (user == null) return null;
        return new OwnerSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getProfile()
        );
    }
}
