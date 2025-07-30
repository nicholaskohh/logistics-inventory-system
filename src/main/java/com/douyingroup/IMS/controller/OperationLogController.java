package com.douyingroup.IMS.controller;

import com.douyingroup.IMS.dto.*;
import com.douyingroup.IMS.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "操作日志管理", description = "操作日志查询相关接口")
@SecurityRequirement(name = "bearerAuth")
public class OperationLogController {

    private final OperationLogService operationLogService;

    @PostMapping("/query")
    @Operation(summary = "查询操作日志", description = "支持多条件组合查询")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<ApiResponse<Page<OperationLogResponse>>> queryLogs(
            @RequestBody OperationLogQueryRequest request) {

        Page<OperationLogResponse> logs = operationLogService.queryLogs(request);
        return ResponseEntity.ok(ApiResponse.success("查询成功", logs));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户操作日志")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    public ResponseEntity<ApiResponse<Page<OperationLogResponse>>> getLogsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<OperationLogResponse> logs = operationLogService.getLogsByUser(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success("查询成功", logs));
    }

    @GetMapping("/date-range")
    @Operation(summary = "按日期范围查询操作日志")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<ApiResponse<Page<OperationLogResponse>>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<OperationLogResponse> logs = operationLogService.getLogsByDateRange(startDate, endDate, page, size);
        return ResponseEntity.ok(ApiResponse.success("查询成功", logs));
    }
}