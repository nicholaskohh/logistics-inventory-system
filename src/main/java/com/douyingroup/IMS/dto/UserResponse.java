package com.douyingroup.IMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String userId;
    private String username;
    private String role;
    private String email;
    private String phone;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
}