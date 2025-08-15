package com.douyingroup.IMS.order.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "shipment_events",
       indexes = {@Index(name = "idx_ship_events_shipment_id", columnList = "shipment_id")})
public class ShipmentEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Column(length = 30, nullable = false)
    private String eventType;    // 到站/派送中/签收/异常/IN_TRANSIT/DELIVERED/EXCEPTION

    @Column(length = 120)
    private String location;

    private OffsetDateTime occurredAt;

    @Column(length = 255)
    private String remark;

    // getters & setters
    public String getId() { return id; }
    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
