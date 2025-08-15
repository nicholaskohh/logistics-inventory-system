package com.douyingroup.IMS.order.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;

public record CreateShipmentRequest(
        @NotBlank String carrier,
        @NotBlank String trackingNo,
        OffsetDateTime shippedAt
) {}
