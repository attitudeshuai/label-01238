package com.shop.mapper;

import com.shop.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CartMapper {
    List<Cart> findByUserId(Long userId);
    int deleteSelected(Long userId);
}
