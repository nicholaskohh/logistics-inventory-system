package com.douyingroup.IMS.order.dto;

import com.douyingroup.IMS.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {}
