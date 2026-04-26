package com.xiaoyu.babycare.controller;

import com.xiaoyu.babycare.auth.RequireLogin;
import com.xiaoyu.babycare.auth.UserContext;
import com.xiaoyu.babycare.common.ApiResponse;
import com.xiaoyu.babycare.dto.order.CreateOrderRequest;
import com.xiaoyu.babycare.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
// 类级别要求登录：本控制器下接口默认都需要 token。
@RequireLogin
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody CreateOrderRequest request) {
        // 当前登录用户下单，不从前端接收 userId，避免越权。
        return ApiResponse.success("下单成功", orderService.createOrder(UserContext.userId(), request));
    }

    @GetMapping("/mine")
    public ApiResponse<List<Map<String, Object>>> myOrders() {
        // 仅查询自己的订单。
        return ApiResponse.success(orderService.listMyOrders(UserContext.userId()));
    }

    @RequireLogin(adminOnly = true)
    @GetMapping
    public ApiResponse<List<Map<String, Object>>> allOrders() {
        // 管理员可查看所有订单。
        return ApiResponse.success(orderService.listAllOrders());
    }
}
