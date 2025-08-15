package com.douyingroup.IMS.order.repository;

import com.douyingroup.IMS.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> { }
