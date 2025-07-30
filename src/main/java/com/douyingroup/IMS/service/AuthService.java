package com.douyingroup.IMS.service;

import com.douyingroup.IMS.dto.JwtResponse;
import com.douyingroup.IMS.dto.LoginRequest;
import com.douyingroup.IMS.entity.User;
import com.douyingroup.IMS.entity.OperationLog;
import com.douyingroup.IMS.repository.UserRepository;
import com.douyingroup.IMS.repository.OperationLogRepository;
import com.douyingroup.IMS.security.JwtUtil;
import com.douyingroup.IMS.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OperationLogRepository operationLogRepository;

    public JwtResponse login(LoginRequest request, String ipAddress, String userAgent) {
        // Find user by username
        User user = userRepository.findByUsernameAndIsActiveTrue(request.getUsername())
                .orElseThrow(() -> new BusinessException(1003, "用户名或密码错误"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Log failed login attempt
            logOperation(user.getUserId(), user.getUsername(),
                    OperationLog.OperationType.LOGIN,
                    "用户登录失败：密码错误", ipAddress, userAgent);
            throw new BusinessException(1003, "用户名或密码错误");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getUserId(), user.getRole());

        // Log successful login
        logOperation(user.getUserId(), user.getUsername(),
                OperationLog.OperationType.LOGIN,
                "用户登录成功", ipAddress, userAgent);

        return new JwtResponse(token, user.getUserId(), user.getUsername(), user.getRole());
    }

    public void logout(String userId, String username, String ipAddress, String userAgent) {
        // Log logout operation
        logOperation(userId, username,
                OperationLog.OperationType.LOGOUT,
                "用户登出", ipAddress, userAgent);
    }

    private void logOperation(String userId, String username,
                              OperationLog.OperationType operationType,
                              String description, String ipAddress, String userAgent) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperationType(operationType);
        log.setDescription(description);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);

        operationLogRepository.save(log);
    }
}