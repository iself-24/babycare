package com.xiaoyu.babycare.mapper;

import com.xiaoyu.babycare.entity.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {
    @Select("select * from cart where user_id = #{userId} and product_id = #{productId}")
    Cart findByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("select * from cart where user_id = #{userId} order by id desc")
    List<Cart> listByUserId(Long userId);

    @Insert("insert into cart(user_id,product_id,quantity) values(#{userId},#{productId},#{quantity})")
    int insert(Cart cart);

    @Update("update cart set quantity = #{quantity} where id = #{id}")
    int updateQuantity(Cart cart);

    @Delete("delete from cart where user_id = #{userId}")
    int clearByUserId(Long userId);
}
