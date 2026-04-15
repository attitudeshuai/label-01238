package com.shop.mapper;

import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    int insert(Order order);
    int insertOrderItem(OrderItem orderItem);
}
