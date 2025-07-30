package com.douyingroup.IMS.repository;

import com.douyingroup.IMS.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, String> {

    // Find by user ID
    Page<OperationLog> findByUserId(String userId, Pageable pageable);

    // Find by operation type
    Page<OperationLog> findByOperationType(OperationLog.OperationType operationType, Pageable pageable);

    // Find by date range
    Page<OperationLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // Complex query with multiple conditions
    @Query("SELECT ol FROM OperationLog ol WHERE " +
            "(:userId IS NULL OR ol.userId = :userId) AND " +
            "(:operationType IS NULL OR ol.operationType = :operationType) AND " +
            "(:entityType IS NULL OR ol.entityType = :entityType) AND " +
            "(:startDate IS NULL OR ol.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR ol.createdAt <= :endDate)")
    Page<OperationLog> findByConditions(
            @Param("userId") String userId,
            @Param("operationType") OperationLog.OperationType operationType,
            @Param("entityType") String entityType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}