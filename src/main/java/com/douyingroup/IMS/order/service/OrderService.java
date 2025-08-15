package com.douyingroup.IMS.order.service;

import com.douyingroup.IMS.order.dto.*;
import com.douyingroup.IMS.order.entity.Order;
import com.douyingroup.IMS.order.entity.Shipment;
import com.douyingroup.IMS.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Order createOrder(CreateOrderRequest req);
    Order getOrder(String id);
    Page<Order> listOrders(String orderNo, String customerId, OrderStatus status, Pageable pageable);
    Order updateOrder(String id, UpdateOrderRequest req);
    void deleteOrder(String id);
    Order changeStatus(String id, OrderStatus toStatus);

    // 1:1 运单
    Shipment createShipment(String orderId, CreateShipmentRequest req);
    Shipment getShipment(String orderId);
}
