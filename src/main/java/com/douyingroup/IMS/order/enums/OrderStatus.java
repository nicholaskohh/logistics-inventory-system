package com.douyingroup.IMS.order.enums;

public enum OrderStatus {
    DRAFT,        // 草稿
    CONFIRMED,    // 已确认（可发货）
    SHIPPED,      // 已发货（已创建运单）
    DELIVERED,    // 已签收
    CANCELLED     // 已取消
}
