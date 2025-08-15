package com.douyingroup.IMS.order.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record CreateOrderRequest(
        @NotBlank String customerID,
        @NotNull @Size(min = 1) List<Item> items,
        @NotNull BigDecimal totalAmount,
        @NotBlank String paymentMethod,
        @NotBlank String deliveryMethod,
        OffsetDateTime expectedDeliveryAt
) {
    public record Item(@NotBlank String goodID,
                       @Min(1) Integer quantity,
                       @NotNull BigDecimal unitPrice) {}
}
