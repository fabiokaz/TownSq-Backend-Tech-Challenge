package com.kaz.townsq.dto.response;

import com.kaz.townsq.model.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long userId;
    private String username;
    private String role;

    public static UserResponse fromUser(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        return response;
    }
}
