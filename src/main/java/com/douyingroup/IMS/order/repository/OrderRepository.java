package com.douyingroup.IMS.order.repository;

import com.douyingroup.IMS.order.entity.Order;
import com.douyingroup.IMS.order.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByOrderNo(String orderNo);
    boolean existsByOrderNo(String orderNo);
    long countByStatus(OrderStatus status);
}
