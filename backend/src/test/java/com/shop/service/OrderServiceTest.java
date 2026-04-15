package com.shop.service;

import com.shop.entity.Cart;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.entity.Product;
import com.shop.mapper.CartMapper;
import com.shop.mapper.OrderMapper;
import com.shop.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("订单服务单元测试")
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private Cart testCart;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("20240101120000001");
        testOrder.setUserId(1L);
        testOrder.setTotalAmount(new BigDecimal("6999.00"));
        testOrder.setStatus(0);
        testOrder.setAddress("测试地址");
        testOrder.setReceiver("张三");
        testOrder.setPhone("13800138000");

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUserId(1L);
        testCart.setProductId(1L);
        testCart.setProductName("iPhone 15");
        testCart.setPrice(new BigDecimal("6999.00"));
        testCart.setQuantity(1);
        testCart.setSelected(true);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("iPhone 15");
        testProduct.setPrice(new BigDecimal("6999.00"));
        testProduct.setStock(100);
    }

    @Test
    @DisplayName("从购物车创建订单 - 3件商品选2件漏1件")
    void createFromCart_ThreeProductsTwoSelected() {
        Cart cart2 = new Cart();
        cart2.setId(2L);
        cart2.setUserId(1L);
        cart2.setProductId(2L);
        cart2.setProductName("MacBook Pro");
        cart2.setPrice(new BigDecimal("12999.00"));
        cart2.setQuantity(1);
        cart2.setSelected(true);

        Cart cart3 = new Cart();
        cart3.setId(3L);
        cart3.setUserId(1L);
        cart3.setProductId(3L);
        cart3.setProductName("AirPods Pro");
        cart3.setPrice(new BigDecimal("1999.00"));
        cart3.setQuantity(1);
        cart3.setSelected(false);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("MacBook Pro");
        product2.setPrice(new BigDecimal("12999.00"));
        product2.setStock(50);

        Product product3 = new Product();
        product3.setId(3L);
        product3.setName("AirPods Pro");
        product3.setPrice(new BigDecimal("1999.00"));
        product3.setStock(200);

        List<Cart> carts = Arrays.asList(testCart, cart2, cart3);
        when(cartMapper.findByUserId(1L)).thenReturn(carts);
        when(productMapper.findById(1L)).thenReturn(testProduct);
        when(productMapper.findById(2L)).thenReturn(product2);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderMapper.insertOrderItem(any(OrderItem.class))).thenReturn(1);
        when(productMapper.updateStock(anyLong(), anyInt())).thenReturn(1);
        when(productMapper.updateSales(anyLong(), anyInt())).thenReturn(1);
        when(cartMapper.deleteSelected(1L)).thenReturn(2);

        Order result = orderService.createFromCart(1L, "测试地址", "张三", "13800138000", "备注");

        assertNotNull(result);
        assertEquals(new BigDecimal("19998.00"), result.getTotalAmount());
        verify(productMapper, times(1)).updateStock(eq(1L), eq(1));
        verify(productMapper, times(1)).updateStock(eq(2L), eq(1));
        verify(productMapper, never()).updateStock(eq(3L), anyInt());
    }

    @Test
    @DisplayName("从购物车创建订单 - 3件商品全部未选")
    void createFromCart_ThreeProductsNoneSelected() {
        testCart.setSelected(false);

        Cart cart2 = new Cart();
        cart2.setId(2L);
        cart2.setUserId(1L);
        cart2.setProductId(2L);
        cart2.setProductName("MacBook Pro");
        cart2.setPrice(new BigDecimal("12999.00"));
        cart2.setQuantity(1);
        cart2.setSelected(false);

        Cart cart3 = new Cart();
        cart3.setId(3L);
        cart3.setUserId(1L);
        cart3.setProductId(3L);
        cart3.setProductName("AirPods Pro");
        cart3.setPrice(new BigDecimal("1999.00"));
        cart3.setQuantity(1);
        cart3.setSelected(false);

        List<Cart> carts = Arrays.asList(testCart, cart2, cart3);
        when(cartMapper.findByUserId(1L)).thenReturn(carts);

        Order result = orderService.createFromCart(1L, "测试地址", "张三", "13800138000", "备注");

        assertNull(result);
        verify(orderMapper, never()).insert(any(Order.class));
        verify(productMapper, never()).updateStock(anyLong(), anyInt());
    }
}
