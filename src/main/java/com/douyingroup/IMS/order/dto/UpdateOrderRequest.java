package com.douyingroup.IMS.order.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record UpdateOrderRequest(
        @NotBlank String customerID,
        @NotNull @Size(min = 1) List<CreateOrderRequest.Item> items,
        @NotNull BigDecimal totalAmount,
        @NotBlank String paymentMethod,
        @NotBlank String deliveryMethod,
        OffsetDateTime expectedDeliveryAt
) {}
