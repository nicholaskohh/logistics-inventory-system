package com.douyingroup.IMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String userId;
    private String username;
    private String role;

    public JwtResponse(String token, String userId, String username, String role) {
        this.token = token;
        this.type = "Bearer";
        this.userId = userId;
        this.username = username;
        this.role = role;
    }
}