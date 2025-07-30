package com.douyingroup.IMS.dto;

import com.douyingroup.IMS.entity.OperationLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

// Operation Log Response
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogResponse {
    private String logId;
    private String userId;
    private String username;
    private String operationType;
    private String operationDescription;
    private String entityType;
    private String entityId;
    private String description;
    private String ipAddress;
    private String createdAt;
}