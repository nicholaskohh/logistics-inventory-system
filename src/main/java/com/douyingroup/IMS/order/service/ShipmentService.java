package com.douyingroup.IMS.order.service;

import com.douyingroup.IMS.order.dto.AddShipmentEventRequest;
import com.douyingroup.IMS.order.entity.ShipmentEvent;

import java.util.List;

public interface ShipmentService {
    ShipmentEvent addEvent(String shipmentId, AddShipmentEventRequest req);
    List<ShipmentEvent> listEvents(String shipmentId);
}
