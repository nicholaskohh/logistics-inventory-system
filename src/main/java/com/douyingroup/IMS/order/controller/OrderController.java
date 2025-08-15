package com.douyingroup.IMS.order.controller;

import com.douyingroup.IMS.order.dto.*;
import com.douyingroup.IMS.order.entity.Order;
import com.douyingroup.IMS.order.entity.Shipment;
import com.douyingroup.IMS.order.enums.OrderStatus;
import com.douyingroup.IMS.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody CreateOrderRequest req) {
        return ResponseEntity.status(201).body(orderService.createOrder(req));
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable String id) {
        return orderService.getOrder(id);
    }

    // 简易分页 + 过滤：?page=0&size=20&orderNo=SO-&customerId=123&status=CONFIRMED
    @GetMapping
    public Page<Order> list(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "20") int size,
                            @RequestParam(required = false) String orderNo,
                            @RequestParam(required = false) String customerId,
                            @RequestParam(required = false) OrderStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        return orderService.listOrders(orderNo, customerId, status, pageable);
    }

    @PutMapping("/{id}")
    public Order update(@PathVariable String id, @Valid @RequestBody UpdateOrderRequest req) {
        return orderService.updateOrder(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public Order changeStatus(@PathVariable String id, @Valid @RequestBody UpdateOrderStatusRequest req) {
        return orderService.changeStatus(id, req.status());
    }

    // 1:1 运单：创建 + 查询
    @PostMapping("/{id}/shipment")
    public ResponseEntity<Shipment> createShipment(@PathVariable String id,
                                                   @Valid @RequestBody CreateShipmentRequest req) {
        return ResponseEntity.status(201).body(orderService.createShipment(id, req));
    }

    @GetMapping("/{id}/shipment")
    public Shipment getShipment(@PathVariable String id) {
        return orderService.getShipment(id);
    }
}
