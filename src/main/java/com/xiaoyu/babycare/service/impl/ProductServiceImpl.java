package com.xiaoyu.babycare.service.impl;

import com.xiaoyu.babycare.common.BizException;
import com.xiaoyu.babycare.dto.PageResponse;
import com.xiaoyu.babycare.dto.product.ProductRequest;
import com.xiaoyu.babycare.entity.Product;
import com.xiaoyu.babycare.mapper.ProductMapper;
import com.xiaoyu.babycare.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public PageResponse<Product> list(int pageNum, int pageSize) {
        int safePageNum = Math.max(pageNum, 1);
        int safePageSize = Math.max(pageSize, 1);
        int offset = (safePageNum - 1) * safePageSize;
        List<Product> records = productMapper.listPage(offset, safePageSize);
        long total = productMapper.countAll();
        return new PageResponse<>(total, safePageNum, safePageSize, records);
    }

    @Override
    public void create(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setStock(request.getStock());
        productMapper.insert(product);
    }

    @Override
    public void update(Long id, ProductRequest request) {
        Product old = productMapper.findById(id);
        if (old == null) {
            throw new BizException("商品不存在");
        }
        old.setName(request.getName());
        old.setPrice(request.getPrice());
        old.setDescription(request.getDescription());
        old.setStock(request.getStock());
        productMapper.update(old);
    }

    @Override
    public void delete(Long id) {
        if (productMapper.deleteById(id) == 0) {
            throw new BizException("商品不存在或已删除");
        }
    }
}
