package com.xiaoyu.babycare.service.impl;

import com.xiaoyu.babycare.common.BizException;
import com.xiaoyu.babycare.dto.order.CreateOrderRequest;
import com.xiaoyu.babycare.dto.order.OrderItemRequest;
import com.xiaoyu.babycare.entity.Order;
import com.xiaoyu.babycare.entity.OrderItem;
import com.xiaoyu.babycare.entity.Product;
import com.xiaoyu.babycare.mapper.CartMapper;
import com.xiaoyu.babycare.mapper.OrderItemMapper;
import com.xiaoyu.babycare.mapper.OrderMapper;
import com.xiaoyu.babycare.mapper.ProductMapper;
import com.xiaoyu.babycare.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final CartMapper cartMapper;

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper, ProductMapper productMapper, CartMapper cartMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.cartMapper = cartMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long userId, CreateOrderRequest request) {
        // 整个下单流程在一个事务中：任意一步失败则回滚，避免脏数据。
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 1) 遍历每个订单项：校验商品、扣库存、累计金额。
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productMapper.findById(itemReq.getProductId());
            if (product == null) {
                throw new BizException("商品不存在: " + itemReq.getProductId());
            }
            // 使用 "stock >= quantity" 条件更新，防止并发下超卖。
            int updatedRows = productMapper.deductStock(itemReq.getProductId(), itemReq.getQuantity());
            if (updatedRows == 0) {
                throw new BizException("库存不足: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);
            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
        }

        // 2) 生成主订单。
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setStatus("UNPAID");
        orderMapper.insert(order);

        // 3) 写入订单明细。
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order.getId());
            orderItemMapper.insert(orderItem);
        }
        // 4) 下单成功后清空该用户购物车。
        cartMapper.clearByUserId(userId);
        return order.getId();
    }

    @Override
    public List<Map<String, Object>> listMyOrders(Long userId) {
        return toView(orderMapper.listByUserId(userId));
    }

    @Override
    public List<Map<String, Object>> listAllOrders() {
        return toView(orderMapper.listAll());
    }

    private List<Map<String, Object>> toView(List<Order> orders) {
        // 简单组装为 {order, items} 结构，便于前端直接渲染。
        List<Map<String, Object>> result = new ArrayList<>();
        for (Order order : orders) {
            Map<String, Object> one = new HashMap<>();
            one.put("order", order);
            one.put("items", orderItemMapper.listByOrderId(order.getId()));
            result.add(one);
        }
        return result;
    }
}
