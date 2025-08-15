package com.douyingroup.IMS.order.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;

public record AddShipmentEventRequest(
        @NotBlank String eventType,
        String location,
        OffsetDateTime occurredAt,
        String remark
) {}
