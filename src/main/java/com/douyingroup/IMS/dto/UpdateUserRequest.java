package com.douyingroup.IMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @Email(message = "错误邮箱格式")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "错误手机号码")
    private String phone;

    private String password;
}
