package com.douyingroup.IMS.dto;

import com.douyingroup.IMS.entity.OperationLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

// Operation Log Query Request
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogQueryRequest {
    private String userId;
    private OperationLog.OperationType operationType;
    private String entityType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private int page = 0;
    private int size = 20;
}
