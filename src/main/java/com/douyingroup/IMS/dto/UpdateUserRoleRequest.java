package com.douyingroup.IMS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleRequest {
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ADMIN|WAREHOUSE_MANAGER|SALES|LOGISTICS|FINANCE",
            message = "Invalid role")
    private String role;
}
