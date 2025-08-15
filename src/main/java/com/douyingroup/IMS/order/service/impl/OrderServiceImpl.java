package com.douyingroup.IMS.order.service.impl;

import com.douyingroup.IMS.order.dto.*;
import com.douyingroup.IMS.order.entity.*;
import com.douyingroup.IMS.order.enums.OrderStatus;
import com.douyingroup.IMS.order.enums.ShipmentStatus;
import com.douyingroup.IMS.order.repository.*;
import com.douyingroup.IMS.order.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final ShipmentRepository shipmentRepo;

    @Override
    @Transactional
    public Order createOrder(CreateOrderRequest req) {
        var now = OffsetDateTime.now();
        var order = new Order();
        order.setOrderNo("SO-" + UUID.randomUUID().toString().substring(0, 8));
        order.setCustomerId(req.customerID());
        order.setTotalAmount(req.totalAmount());
        order.setPaymentMethod(req.paymentMethod());
        order.setDeliveryMethod(req.deliveryMethod());
        order.setStatus(OrderStatus.DRAFT);
        order.setExpectedDeliveryAt(req.expectedDeliveryAt());
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        order = orderRepo.save(order);

        for (var it : req.items()) {
            var item = new OrderItem();
            item.setOrder(order);
            item.setProductId(it.goodID());
            item.setQuantity(it.quantity());
            item.setUnitPrice(it.unitPrice());
            itemRepo.save(item);
            order.getItems().add(item);
        }
        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrder(String id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> listOrders(String orderNo, String customerId, OrderStatus status, Pageable pageable) {
        Specification<Order> spec = Specification.where(null);
        if (orderNo != null && !orderNo.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("orderNo")), "%" + orderNo.toLowerCase() + "%"));
        }
        if (customerId != null && !customerId.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("customerId"), customerId));
        }
        if (status != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        }
        return orderRepo.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public Order updateOrder(String id, UpdateOrderRequest req) {
        var order = getOrder(id);
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT orders can be updated");
        }
        order.setCustomerId(req.customerID());
        order.setTotalAmount(req.totalAmount());
        order.setPaymentMethod(req.paymentMethod());
        order.setDeliveryMethod(req.deliveryMethod());
        order.setExpectedDeliveryAt(req.expectedDeliveryAt());
        order.setUpdatedAt(OffsetDateTime.now());

        // 简化：先清后插（生产可按 diff 更新）
        itemRepo.deleteAll(order.getItems());
        order.getItems().clear();
        for (var it : req.items()) {
            var item = new OrderItem();
            item.setOrder(order);
            item.setProductId(it.goodID());
            item.setQuantity(it.quantity());
            item.setUnitPrice(it.unitPrice());
            itemRepo.save(item);
            order.getItems().add(item);
        }
        return order;
    }

    @Override
    @Transactional
    public void deleteOrder(String id) {
        var order = getOrder(id);
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT orders can be deleted");
        }
        orderRepo.delete(order);
    }

    @Override
    @Transactional
    public Order changeStatus(String id, OrderStatus toStatus) {
        var order = getOrder(id);
        var from = order.getStatus();
        boolean ok = switch (from) {
            case DRAFT -> (toStatus == OrderStatus.CONFIRMED || toStatus == OrderStatus.CANCELLED);
            case CONFIRMED -> (toStatus == OrderStatus.SHIPPED || toStatus == OrderStatus.CANCELLED);
            case SHIPPED -> (toStatus == OrderStatus.DELIVERED);
            default -> false;
        };
        if (!ok) throw new IllegalStateException("Illegal status transition: " + from + " -> " + toStatus);
        order.setStatus(toStatus);
        order.setUpdatedAt(OffsetDateTime.now());
        return order;
    }

    @Override
    @Transactional
    public Shipment createShipment(String orderId, CreateShipmentRequest req) {
        var order = getOrder(orderId);
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Order must be CONFIRMED to create shipment");
        }
        if (shipmentRepo.existsByOrderId(orderId)) {
            throw new IllegalStateException("Shipment already exists for this order");
        }

        var s = new Shipment();
        s.setOrder(order);
        s.setCarrier(req.carrier());
        s.setTrackingNo(req.trackingNo());
        s.setStatus(ShipmentStatus.CREATED);
        s.setShippedAt(req.shippedAt());
        s = shipmentRepo.save(s);

        order.setStatus(OrderStatus.SHIPPED);
        order.setUpdatedAt(OffsetDateTime.now());
        return s;
    }

    @Override
    @Transactional(readOnly = true)
    public Shipment getShipment(String orderId) {
        return shipmentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found for order"));
    }
}
