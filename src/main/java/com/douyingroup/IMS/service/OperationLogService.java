package com.douyingroup.IMS.service;

import com.douyingroup.IMS.dto.OperationLogQueryRequest;
import com.douyingroup.IMS.dto.OperationLogResponse;
import com.douyingroup.IMS.entity.OperationLog;
import com.douyingroup.IMS.repository.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;

    // Log an operation
    @Transactional
    public void logOperation(String userId, String username,
                             OperationLog.OperationType operationType,
                             String entityType, String entityId,
                             String description, String ipAddress, String userAgent) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperationType(operationType);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);

        operationLogRepository.save(log);
    }

    // Query operation logs
    public Page<OperationLogResponse> queryLogs(OperationLogQueryRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<OperationLog> logs = operationLogRepository.findByConditions(
                request.getUserId(),
                request.getOperationType(),
                request.getEntityType(),
                request.getStartDate(),
                request.getEndDate(),
                pageable
        );

        return logs.map(this::convertToResponse);
    }

    // Get logs by user
    public Page<OperationLogResponse> getLogsByUser(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OperationLog> logs = operationLogRepository.findByUserId(userId, pageable);
        return logs.map(this::convertToResponse);
    }

    // Get logs by date range
    public Page<OperationLogResponse> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OperationLog> logs = operationLogRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        return logs.map(this::convertToResponse);
    }

    // Convert entity to response DTO
    private OperationLogResponse convertToResponse(OperationLog log) {
        OperationLogResponse response = new OperationLogResponse();
        response.setLogId(log.getLogId());
        response.setUserId(log.getUserId());
        response.setUsername(log.getUsername());
        response.setOperationType(log.getOperationType().name());
        response.setOperationDescription(log.getOperationType().getDescription());
        response.setEntityType(log.getEntityType());
        response.setEntityId(log.getEntityId());
        response.setDescription(log.getDescription());
        response.setIpAddress(log.getIpAddress());
        response.setCreatedAt(log.getCreatedAt().toString());
        return response;
    }
}