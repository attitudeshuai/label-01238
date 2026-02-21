package com.shop.service;

import com.shop.entity.Cart;
import com.shop.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务类
 */
@Service
public class CartService {

    @Autowired
    private CartMapper cartMapper;

    /**
     * 获取用户购物车
     */
    public List<Cart> findByUserId(Long userId) {
        return cartMapper.findByUserId(userId);
    }

    /**
     * 添加商品到购物车
     */
    public boolean add(Long userId, Long productId, Integer quantity) {
        Cart existCart = cartMapper.findByUserIdAndProductId(userId, productId);
        if (existCart != null) {
            // 已存在则更新数量
            return cartMapper.updateQuantity(existCart.getId(), existCart.getQuantity() + quantity) > 0;
        }
        // 不存在则新增
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        cart.setSelected(true);
        return cartMapper.insert(cart) > 0;
    }

    /**
     * 更新购物车数量
     */
    public boolean updateQuantity(Long id, Integer quantity) {
        return cartMapper.updateQuantity(id, quantity) > 0;
    }

    /**
     * 更新选中状态
     */
    public boolean updateSelected(Long id, Boolean selected) {
        return cartMapper.updateSelected(id, selected) > 0;
    }

    /**
     * 全选/取消全选
     */
    public boolean updateAllSelected(Long userId, Boolean selected) {
        return cartMapper.updateAllSelected(userId, selected) > 0;
    }

    /**
     * 删除购物车项
     */
    public boolean delete(Long id) {
        return cartMapper.deleteById(id) > 0;
    }

    /**
     * 清空购物车
     */
    public boolean clear(Long userId) {
        return cartMapper.deleteByUserId(userId) > 0;
    }

    /**
     * 删除选中的购物车项
     */
    public boolean deleteSelected(Long userId) {
        return cartMapper.deleteSelected(userId) > 0;
    }
}
