package com.douyingroup.IMS.order.repository;

import com.douyingroup.IMS.order.entity.ShipmentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentEventRepository extends JpaRepository<ShipmentEvent, String> {
    List<ShipmentEvent> findByShipmentIdOrderByOccurredAtAsc(String shipmentId);
}
