package com.douyingroup.IMS.controller;

import com.douyingroup.IMS.dto.ApiResponse;
import com.douyingroup.IMS.dto.UpdateUserRequest;
import com.douyingroup.IMS.dto.UserResponse;
import com.douyingroup.IMS.dto.UpdateUserRoleRequest;
import com.douyingroup.IMS.security.AuthenticatedUser;
import com.douyingroup.IMS.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息获取、更新、删除及权限分配")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "分页或全部获取用户列表")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> listUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("获取成功", users));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "根据ID获取用户信息")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success("获取成功", userService.getUserById(userId)));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "更新用户资料（邮箱/手机/密码）")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request,
            HttpServletRequest httpRequest,
            Authentication authentication) {

        String ip = getClientIp(httpRequest);
        String ua = httpRequest.getHeader("User-Agent");
        AuthenticatedUser au = (AuthenticatedUser) authentication.getPrincipal();
        UserResponse updated = userService.updateUser(userId, request, ip, ua, au.getUserId(), au.getUsername());
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }

    @PatchMapping("/{userId}/role")
    @Operation(summary = "修改用户角色（权限分配）")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRoleRequest request,
            HttpServletRequest httpRequest,
            Authentication authentication) {

        String ip = getClientIp(httpRequest);
        String ua = httpRequest.getHeader("User-Agent");
        AuthenticatedUser au = (AuthenticatedUser) authentication.getPrincipal();
        UserResponse updated = userService.updateUserRole(userId, request.getRole(), ip, ua, au.getUserId(), au.getUsername());
        return ResponseEntity.ok(ApiResponse.success("角色更新成功", updated));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "删除（停用）用户")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable String userId,
            HttpServletRequest httpRequest,
            Authentication authentication) {

        String ip = getClientIp(httpRequest);
        String ua = httpRequest.getHeader("User-Agent");
        AuthenticatedUser au = (AuthenticatedUser) authentication.getPrincipal();
        userService.deleteUser(userId, ip, ua, au.getUserId(), au.getUsername());
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
