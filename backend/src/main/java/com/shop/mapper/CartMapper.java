package com.shop.mapper;

import com.shop.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 购物车Mapper接口
 */
@Mapper
public interface CartMapper {
    /** 根据用户ID查询购物车 */
    List<Cart> findByUserId(@Param("userId") Long userId);
    
    /** 根据用户ID和商品ID查询购物车项 */
    Cart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    /** 新增购物车项 */
    int insert(Cart cart);
    
    /** 更新购物车项数量 */
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /** 更新选中状态 */
    int updateSelected(@Param("id") Long id, @Param("selected") Boolean selected);
    
    /** 全选/取消全选 */
    int updateAllSelected(@Param("userId") Long userId, @Param("selected") Boolean selected);
    
    /** 删除购物车项 */
    int deleteById(@Param("id") Long id);
    
    /** 清空用户购物车 */
    int deleteByUserId(@Param("userId") Long userId);
    
    /** 删除选中的购物车项 */
    int deleteSelected(@Param("userId") Long userId);
}
