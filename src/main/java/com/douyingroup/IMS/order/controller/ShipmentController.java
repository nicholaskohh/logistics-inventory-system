package com.douyingroup.IMS.order.controller;

import com.douyingroup.IMS.order.dto.AddShipmentEventRequest;
import com.douyingroup.IMS.order.entity.ShipmentEvent;
import com.douyingroup.IMS.order.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping("/{shipmentId}/events")
    public ResponseEntity<ShipmentEvent> addEvent(@PathVariable String shipmentId,
                                                  @Valid @RequestBody AddShipmentEventRequest req) {
        return ResponseEntity.status(201).body(shipmentService.addEvent(shipmentId, req));
    }

    @GetMapping("/{shipmentId}/events")
    public List<ShipmentEvent> list(@PathVariable String shipmentId) {
        return shipmentService.listEvents(shipmentId);
    }
}
