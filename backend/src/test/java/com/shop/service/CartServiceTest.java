package com.shop.service;

import com.shop.entity.Cart;
import com.shop.mapper.CartMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("购物车服务单元测试")
class CartServiceTest {

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    private Cart testCart;

    @BeforeEach
    void setUp() {
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUserId(1L);
        testCart.setProductId(1L);
        testCart.setProductName("iPhone 15");
        testCart.setPrice(new BigDecimal("6999.00"));
        testCart.setQuantity(2);
        testCart.setSelected(true);
    }

    @Test
    @DisplayName("获取用户购物车")
    void findByUserId() {
        List<Cart> carts = Arrays.asList(testCart);
        when(cartMapper.findByUserId(1L)).thenReturn(carts);

        List<Cart> result = cartService.findByUserId(1L);

        assertEquals(1, result.size());
        assertEquals("iPhone 15", result.get(0).getProductName());
    }

    @Test
    @DisplayName("添加商品到购物车 - 新增")
    void add_NewItem() {
        when(cartMapper.findByUserIdAndProductId(1L, 2L)).thenReturn(null);
        when(cartMapper.insert(any(Cart.class))).thenReturn(1);

        boolean result = cartService.add(1L, 2L, 1);

        assertTrue(result);
        verify(cartMapper).insert(any(Cart.class));
    }

    @Test
    @DisplayName("添加商品到购物车 - 已存在则更新数量")
    void add_ExistingItem() {
        when(cartMapper.findByUserIdAndProductId(1L, 1L)).thenReturn(testCart);
        when(cartMapper.updateQuantity(1L, 3)).thenReturn(1);

        boolean result = cartService.add(1L, 1L, 1);

        assertTrue(result);
        verify(cartMapper).updateQuantity(1L, 3);
        verify(cartMapper, never()).insert(any(Cart.class));
    }

    @Test
    @DisplayName("更新购物车数量")
    void updateQuantity() {
        when(cartMapper.updateQuantity(1L, 5)).thenReturn(1);

        boolean result = cartService.updateQuantity(1L, 5);

        assertTrue(result);
    }

    @Test
    @DisplayName("更新选中状态")
    void updateSelected() {
        when(cartMapper.updateSelected(1L, false)).thenReturn(1);

        boolean result = cartService.updateSelected(1L, false);

        assertTrue(result);
    }

    @Test
    @DisplayName("全选/取消全选")
    void updateAllSelected() {
        when(cartMapper.updateAllSelected(1L, true)).thenReturn(3);

        boolean result = cartService.updateAllSelected(1L, true);

        assertTrue(result);
    }

    @Test
    @DisplayName("删除购物车项")
    void delete() {
        when(cartMapper.deleteById(1L)).thenReturn(1);

        boolean result = cartService.delete(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("清空购物车")
    void clear() {
        when(cartMapper.deleteByUserId(1L)).thenReturn(3);

        boolean result = cartService.clear(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("删除选中的购物车项")
    void deleteSelected() {
        when(cartMapper.deleteSelected(1L)).thenReturn(2);

        boolean result = cartService.deleteSelected(1L);

        assertTrue(result);
    }

    // ==================== 边缘情况测试 ====================

    @Test
    @DisplayName("获取用户购物车 - 空购物车")
    void findByUserId_EmptyCart() {
        when(cartMapper.findByUserId(999L)).thenReturn(Arrays.asList());

        List<Cart> result = cartService.findByUserId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("添加商品到购物车 - 数量为0")
    void add_ZeroQuantity() {
        when(cartMapper.findByUserIdAndProductId(1L, 2L)).thenReturn(null);
        when(cartMapper.insert(any(Cart.class))).thenReturn(1);

        boolean result = cartService.add(1L, 2L, 0);

        assertTrue(result);
    }

    @Test
    @DisplayName("添加商品到购物车 - 负数数量")
    void add_NegativeQuantity() {
        when(cartMapper.findByUserIdAndProductId(1L, 2L)).thenReturn(null);
        when(cartMapper.insert(any(Cart.class))).thenReturn(1);

        boolean result = cartService.add(1L, 2L, -1);

        assertTrue(result);
    }

    @Test
    @DisplayName("添加商品到购物车 - 已存在商品数量累加后为负")
    void add_ExistingItem_ResultNegative() {
        testCart.setQuantity(1);
        when(cartMapper.findByUserIdAndProductId(1L, 1L)).thenReturn(testCart);
        when(cartMapper.updateQuantity(1L, -1)).thenReturn(1);

        boolean result = cartService.add(1L, 1L, -2);

        assertTrue(result);
        verify(cartMapper).updateQuantity(1L, -1);
    }

    @Test
    @DisplayName("更新购物车数量 - 数量为0")
    void updateQuantity_Zero() {
        when(cartMapper.updateQuantity(1L, 0)).thenReturn(1);

        boolean result = cartService.updateQuantity(1L, 0);

        assertTrue(result);
    }

    @Test
    @DisplayName("更新购物车数量 - 负数")
    void updateQuantity_Negative() {
        when(cartMapper.updateQuantity(1L, -5)).thenReturn(1);

        boolean result = cartService.updateQuantity(1L, -5);

        assertTrue(result);
    }

    @Test
    @DisplayName("更新购物车数量 - 不存在的购物车项")
    void updateQuantity_NotFound() {
        when(cartMapper.updateQuantity(999L, 5)).thenReturn(0);

        boolean result = cartService.updateQuantity(999L, 5);

        assertFalse(result);
    }

    @Test
    @DisplayName("更新选中状态 - 不存在的购物车项")
    void updateSelected_NotFound() {
        when(cartMapper.updateSelected(999L, true)).thenReturn(0);

        boolean result = cartService.updateSelected(999L, true);

        assertFalse(result);
    }

    @Test
    @DisplayName("全选/取消全选 - 空购物车")
    void updateAllSelected_EmptyCart() {
        when(cartMapper.updateAllSelected(999L, true)).thenReturn(0);

        boolean result = cartService.updateAllSelected(999L, true);

        assertFalse(result);
    }

    @Test
    @DisplayName("删除购物车项 - 不存在的项")
    void delete_NotFound() {
        when(cartMapper.deleteById(999L)).thenReturn(0);

        boolean result = cartService.delete(999L);

        assertFalse(result);
    }

    @Test
    @DisplayName("清空购物车 - 已经为空")
    void clear_AlreadyEmpty() {
        when(cartMapper.deleteByUserId(999L)).thenReturn(0);

        boolean result = cartService.clear(999L);

        assertFalse(result);
    }

    @Test
    @DisplayName("删除选中的购物车项 - 无选中项")
    void deleteSelected_NoneSelected() {
        when(cartMapper.deleteSelected(1L)).thenReturn(0);

        boolean result = cartService.deleteSelected(1L);

        assertFalse(result);
    }

    @Test
    @DisplayName("添加商品到购物车 - 大数量")
    void add_LargeQuantity() {
        when(cartMapper.findByUserIdAndProductId(1L, 2L)).thenReturn(null);
        when(cartMapper.insert(any(Cart.class))).thenReturn(1);

        boolean result = cartService.add(1L, 2L, Integer.MAX_VALUE);

        assertTrue(result);
    }

    @Test
    @DisplayName("添加商品到购物车 - 插入失败")
    void add_InsertFailure() {
        when(cartMapper.findByUserIdAndProductId(1L, 2L)).thenReturn(null);
        when(cartMapper.insert(any(Cart.class))).thenReturn(0);

        boolean result = cartService.add(1L, 2L, 1);

        assertFalse(result);
    }

    @Test
    @DisplayName("添加商品到购物车 - 更新数量失败")
    void add_UpdateQuantityFailure() {
        when(cartMapper.findByUserIdAndProductId(1L, 1L)).thenReturn(testCart);
        when(cartMapper.updateQuantity(1L, 3)).thenReturn(0);

        boolean result = cartService.add(1L, 1L, 1);

        assertFalse(result);
    }
}
