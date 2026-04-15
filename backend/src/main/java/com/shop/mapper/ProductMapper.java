package com.shop.mapper;

import com.shop.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper {
    Product findById(Long id);
    int updateStock(Long id, Integer quantity);
    int updateSales(Long id, Integer quantity);
}
