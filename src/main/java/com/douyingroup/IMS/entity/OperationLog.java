package com.douyingroup.IMS.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "operation_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {

    @Id
    @Column(name = "log_id", length = 36)
    private String logId = UUID.randomUUID().toString();

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "operation_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    @Column(name = "entity_type", length = 50)
    private String entityType;

    @Column(name = "entity_id", length = 36)
    private String entityId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Enum for operation types
    public enum OperationType {
        LOGIN("用户登录"),
        LOGOUT("用户登出"),
        REGISTER("用户注册"),
        CREATE("创建"),
        UPDATE("更新"),
        DELETE("删除"),
        APPROVE("审批"),
        EXPORT("导出"),
        IMPORT("导入");

        private final String description;

        OperationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}