package com.douyingroup.IMS.service;   // ← adjust if needed

import com.douyingroup.IMS.dto.RegisterRequest;
import com.douyingroup.IMS.dto.UpdateUserRequest;
import com.douyingroup.IMS.dto.UserResponse;
import com.douyingroup.IMS.entity.OperationLog;
import com.douyingroup.IMS.entity.User;
import com.douyingroup.IMS.repository.UserRepository;
import com.douyingroup.IMS.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OperationLogService operationLogService;

    /* -----------------------------------------------------------------------
     *  UserDetailsService implementation
     * --------------------------------------------------------------------- */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        return new AuthenticatedUser(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                user.getIsActive(),
                user.getIsActive(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }

    /* -----------------------------------------------------------------------
     *  Business-logic CRUD / helper methods
     * --------------------------------------------------------------------- */

    // Backward-compatible method
    public UserResponse registerUser(RegisterRequest request) {
        return registerUser(request, null, null);
    }

    // Register new user with optional ip/userAgent for logging
    public UserResponse registerUser(RegisterRequest request, String ipAddress, String userAgent) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在！");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        User savedUser = userRepository.save(user);

        // Log REGISTER operation
        operationLogService.logOperation(
                savedUser.getUserId(),
                savedUser.getUsername(),
                OperationLog.OperationType.REGISTER,
                "USER",
                savedUser.getUserId(),
                "用户注册",
                ipAddress,
                userAgent
        );

        return convertToUserResponse(savedUser);
    }

    // Get user by ID
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToUserResponse(user);
    }

    // Get user by username
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToUserResponse(user);
    }

    // Update user
    public UserResponse updateUser(String userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (request.getEmail() != null)  user.setEmail(request.getEmail());
        if (request.getPhone() != null)  user.setPhone(request.getPhone());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    // Overload with logging context
    public UserResponse updateUser(String userId, UpdateUserRequest request,
                                   String ip, String ua, String actorUserId, String actorUsername) {
        UserResponse resp = updateUser(userId, request);
        operationLogService.logOperation(
                actorUserId,
                actorUsername,
                OperationLog.OperationType.UPDATE,
                "USER",
                userId,
                "更新用户资料",
                ip,
                ua
        );
        return resp;
    }

    // Get all users
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    // Soft-delete user
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    // Overload with logging context
    public void deleteUser(String userId, String ip, String ua, String actorUserId, String actorUsername) {
        deleteUser(userId);
        operationLogService.logOperation(
                actorUserId,
                actorUsername,
                OperationLog.OperationType.DELETE,
                "USER",
                userId,
                "删除用户",
                ip,
                ua
        );
    }

    public UserResponse updateUserRole(String userId, String role, String ip, String ua,
                                       String actorUserId, String actorUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setRole(role);
        User saved = userRepository.save(user);
        operationLogService.logOperation(
                actorUserId,
                actorUsername,
                OperationLog.OperationType.UPDATE,
                "USER",
                userId,
                "更新用户角色",
                ip,
                ua
        );
        return convertToUserResponse(saved);
    }

    /* -----------------------------------------------------------------------
     *  DTO converter
     * --------------------------------------------------------------------- */
    private UserResponse convertToUserResponse(User user) {
        UserResponse r = new UserResponse();
        r.setUserId(user.getUserId());
        r.setUsername(user.getUsername());
        r.setRole(user.getRole());
        r.setEmail(user.getEmail());
        r.setPhone(user.getPhone());
        r.setIsActive(user.getIsActive());
        r.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        r.setUpdatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null);
        return r;
    }
}
