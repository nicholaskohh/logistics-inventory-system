package com.douyingroup.IMS.order.service.impl;

import com.douyingroup.IMS.order.dto.AddShipmentEventRequest;
import com.douyingroup.IMS.order.entity.ShipmentEvent;
import com.douyingroup.IMS.order.enums.OrderStatus;
import com.douyingroup.IMS.order.enums.ShipmentStatus;
import com.douyingroup.IMS.order.repository.ShipmentEventRepository;
import com.douyingroup.IMS.order.repository.ShipmentRepository;
import com.douyingroup.IMS.order.service.ShipmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepo;
    private final ShipmentEventRepository eventRepo;

    @Override
    @Transactional
    public ShipmentEvent addEvent(String shipmentId, AddShipmentEventRequest req) {
        var shipment = shipmentRepo.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found"));

        var event = new ShipmentEvent();
        event.setShipment(shipment);
        event.setEventType(req.eventType());
        event.setLocation(req.location());
        event.setOccurredAt(req.occurredAt() != null ? req.occurredAt() : OffsetDateTime.now());
        event.setRemark(req.remark());
        event = eventRepo.save(event);

        var et = req.eventType().toUpperCase();
        if (et.equals("DELIVERED") || et.contains("签收")) {
            shipment.setStatus(ShipmentStatus.DELIVERED);
            shipment.setDeliveredAt(event.getOccurredAt());
            var order = shipment.getOrder();
            order.setStatus(OrderStatus.DELIVERED);
            order.setUpdatedAt(OffsetDateTime.now());
        } else if (et.equals("IN_TRANSIT") || et.contains("在途") || et.contains("派送")) {
            shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        } else if (et.equals("EXCEPTION") || et.contains("异常")) {
            shipment.setStatus(ShipmentStatus.EXCEPTION);
        }
        return event;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentEvent> listEvents(String shipmentId) {
        shipmentRepo.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found"));
        return eventRepo.findByShipmentIdOrderByOccurredAtAsc(shipmentId);
    }
}
