package com.xiaoyu.babycare.controller;

import com.xiaoyu.babycare.auth.RequireLogin;
import com.xiaoyu.babycare.auth.UserContext;
import com.xiaoyu.babycare.common.ApiResponse;
import com.xiaoyu.babycare.dto.cart.CartRequest;
import com.xiaoyu.babycare.entity.Cart;
import com.xiaoyu.babycare.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequireLogin
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<List<Cart>> list() {
        return ApiResponse.success(cartService.list(UserContext.userId()));
    }

    @PostMapping
    public ApiResponse<Void> add(@Valid @RequestBody CartRequest request) {
        cartService.add(UserContext.userId(), request);
        return ApiResponse.success("加入购物车成功", null);
    }

    @PutMapping
    public ApiResponse<Void> updateQuantity(@Valid @RequestBody CartRequest request) {
        cartService.updateQuantity(UserContext.userId(), request);
        return ApiResponse.success("更新购物车成功", null);
    }
}
