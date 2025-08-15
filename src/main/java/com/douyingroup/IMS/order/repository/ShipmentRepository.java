package com.douyingroup.IMS.order.repository;

import com.douyingroup.IMS.order.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, String> {
    Optional<Shipment> findByTrackingNo(String trackingNo);
    Optional<Shipment> findByOrderId(String orderId);
    boolean existsByOrderId(String orderId); // 1:1 约束的保障
}
