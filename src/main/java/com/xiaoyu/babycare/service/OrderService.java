package com.xiaoyu.babycare.service;

import com.xiaoyu.babycare.dto.order.CreateOrderRequest;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Long createOrder(Long userId, CreateOrderRequest request);

    List<Map<String, Object>> listMyOrders(Long userId);

    List<Map<String, Object>> listAllOrders();
}
