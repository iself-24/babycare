package com.xiaoyu.babycare.mapper;

import com.xiaoyu.babycare.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {
    @Select("select * from product order by id desc limit #{limit} offset #{offset}")
    List<Product> listPage(@Param("offset") int offset, @Param("limit") int limit);

    @Select("select count(1) from product")
    long countAll();

    @Select("select * from product where id = #{id}")
    Product findById(Long id);

    @Insert("insert into product(name,price,description,stock,created_at,updated_at) " +
            "values(#{name},#{price},#{description},#{stock},now(),now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("update product set name=#{name}, price=#{price}, description=#{description}, stock=#{stock}, updated_at=now() where id=#{id}")
    int update(Product product);

    @Delete("delete from product where id = #{id}")
    int deleteById(Long id);

    @Update("update product set stock = stock - #{quantity}, updated_at=now() where id=#{productId} and stock >= #{quantity}")
    int deductStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
