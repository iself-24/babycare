package com.xiaoyu.babycare.mapper;

import com.xiaoyu.babycare.entity.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {
    @Insert("insert into `order`(user_id,total_price,status,created_at,updated_at) " +
            "values(#{userId},#{totalPrice},#{status},now(),now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Select("select * from `order` where user_id = #{userId} order by id desc")
    List<Order> listByUserId(Long userId);

    @Select("select * from `order` order by id desc")
    List<Order> listAll();
}
