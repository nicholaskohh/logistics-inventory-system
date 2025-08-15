package com.douyingroup.IMS.order.entity;

import com.douyingroup.IMS.order.enums.ShipmentStatus;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "shipments",
       indexes = {
           @Index(name = "idx_shipments_order_id", columnList = "order_id", unique = true),
           @Index(name = "idx_shipments_tracking", columnList = "trackingNo", unique = true)
       })
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // 1:1 通过 unique index 保证唯一

    @Column(length = 30)
    private String carrier;

    @Column(length = 60, nullable = false)
    private String trackingNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShipmentStatus status;

    private OffsetDateTime shippedAt;
    private OffsetDateTime deliveredAt;

    // getters & setters
    public String getId() { return id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public String getTrackingNo() { return trackingNo; }
    public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }
    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }
    public OffsetDateTime getShippedAt() { return shippedAt; }
    public void setShippedAt(OffsetDateTime shippedAt) { this.shippedAt = shippedAt; }
    public OffsetDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(OffsetDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
}
