package com.xiaoyu.babycare.controller;

import com.xiaoyu.babycare.auth.RequireLogin;
import com.xiaoyu.babycare.common.ApiResponse;
import com.xiaoyu.babycare.dto.PageResponse;
import com.xiaoyu.babycare.dto.product.ProductRequest;
import com.xiaoyu.babycare.entity.Product;
import com.xiaoyu.babycare.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<PageResponse<Product>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(productService.list(pageNum, pageSize));
    }

    @RequireLogin(adminOnly = true)
    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody ProductRequest request) {
        productService.create(request);
        return ApiResponse.success("创建商品成功", null);
    }

    @RequireLogin(adminOnly = true)
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        productService.update(id, request);
        return ApiResponse.success("更新商品成功", null);
    }

    @RequireLogin(adminOnly = true)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.success("删除商品成功", null);
    }
}
