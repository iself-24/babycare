package com.xiaoyu.babycare.mapper;

import com.xiaoyu.babycare.entity.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    @Insert("insert into order_item(order_id,product_id,quantity,price) values(#{orderId},#{productId},#{quantity},#{price})")
    int insert(OrderItem orderItem);

    @Select("select * from order_item where order_id = #{orderId}")
    List<OrderItem> listByOrderId(Long orderId);
}
