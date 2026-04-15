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
    @DisplayName("查询所有订单")
    void findAll() {
        List<Order> orders = Collections.singletonList(testOrder);
        when(orderMapper.findAll()).thenReturn(orders);
        when(orderMapper.findOrderItems(1L)).thenReturn(new ArrayList<>());

        List<Order> result = orderService.findAll();

        assertEquals(1, result.size());
        verify(orderMapper).findAll();
    }

    @Test
    @DisplayName("根据ID查询订单")
    void findById() {
        when(orderMapper.findById(1L)).thenReturn(testOrder);
        when(orderMapper.findOrderItems(1L)).thenReturn(new ArrayList<>());

        Order result = orderService.findById(1L);

        assertNotNull(result);
        assertEquals("20240101120000001", result.getOrderNo());
    }

    @Test
    @DisplayName("根据ID查询订单 - 不存在")
    void findById_NotFound() {
        when(orderMapper.findById(999L)).thenReturn(null);

        Order result = orderService.findById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("根据用户ID查询订单")
    void findByUserId() {
        List<Order> orders = Collections.singletonList(testOrder);
        when(orderMapper.findByUserId(1L)).thenReturn(orders);
        when(orderMapper.findOrderItems(1L)).thenReturn(new ArrayList<>());

        List<Order> result = orderService.findByUserId(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("从购物车创建订单成功")
    void createFromCart_Success() {
        List<Cart> carts = Collections.singletonList(testCart);
        when(cartMapper.findByUserId(1L)).thenReturn(carts);
        when(productMapper.findById(1L)).thenReturn(testProduct);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderMapper.insertOrderItem(any(OrderItem.class))).thenReturn(1);
        when(productMapper.updateStock(anyLong(), anyInt())).thenReturn(1);
        when(productMapper.updateSales(anyLong(), anyInt())).thenReturn(1);
        when(cartMapper.deleteSelected(1L)).thenReturn(1);

        Order result = orderService.createFromCart(1L, "测试地址", "张三", "13800138000", "备注");

        assertNotNull(result);
        assertEquals(new BigDecimal("6999.00"), result.getTotalAmount());
        verify(orderMapper).insert(any(Order.class));
    }

    @Test
    @DisplayName("从购物车创建订单失败 - 购物车为空")
    void createFromCart_EmptyCart() {
        when(cartMapper.findByUserId(1L)).thenReturn(new ArrayList<>());

        Order result = orderService.createFromCart(1L, "测试地址", "张三", "13800138000", "备注");

        assertNull(result);
        verify(orderMapper, never()).insert(any(Order.class));
    }

    @Test
    @DisplayName("从购物车创建订单失败 - 无选中商品")
    void createFromCart_NoSelectedItems() {
        testCart.setSelected(false);
        List<Cart> carts = Collections.singletonList(testCart);
        when(cartMapper.findByUserId(1L)).thenReturn(carts);

        Order result = orderService.createFromCart(1L, "测试地址", "张三", "13800138000", "备注");

        assertNull(result);
    }

    @Test
    @DisplayName("从购物车创建订单失败 - 库存不足")
    void createFromCart_InsufficientStock() {
        testProduct.setStock(0);
        List<Cart> carts = Collections.singletonList(testCart);
        when(cartMapper.findByUserId(1L)).thenReturn(carts);
        when(productMapper.findById(1L)).thenReturn(testProduct);

        assertThrows(RuntimeException.class, () -> 
            orderService.createFromCart(1L, "测试地址", "张三", "13800138000", "备注"));
    }

    @Test
    @DisplayName("更新订单状态")
    void updateStatus() {
        when(orderMapper.updateStatus(1L, 1)).thenReturn(1);

        boolean result = orderService.updateStatus(1L, 1);

        assertTrue(result);
    }

    @Test
    @DisplayName("删除订单")
    void delete() {
        when(orderMapper.deleteById(1L)).thenReturn(1);

        boolean result = orderService.delete(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("统计订单数量")
    void count() {
        when(orderMapper.count()).thenReturn(100);

        int result = orderService.count();

        assertEquals(100, result);
    }

    @Test
    @DisplayName("统计各状态订单数量")
    void countByStatus() {
        Map<String, Object> status1 = new HashMap<>();
        status1.put("status", 0);
        status1.put("count", 10);
        List<Map<String, Object>> statusList = Collections.singletonList(status1);
        when(orderMapper.countByStatus()).thenReturn(statusList);

        List<Map<String, Object>> result = orderService.countByStatus();

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).get("count"));
    }

    @Test
    @DisplayName("统计销售额")
    void sumTotalAmount() {
        Map<String, Object> amount = new HashMap<>();
        amount.put("paid", new BigDecimal("100000.00"));
        amount.put("pending", new BigDecimal("20000.00"));
        when(orderMapper.sumTotalAmount()).thenReturn(amount);

        Map<String, Object> result = orderService.sumTotalAmount();

        assertEquals(new BigDecimal("100000.00"), result.get("paid"));
    }

    @Test
    @DisplayName("按日期统计订单")
    void countByDate() {
        Map<String, Object> dateCount = new HashMap<>();
        dateCount.put("date", "2024-01-01");
        dateCount.put("count", 5);
        List<Map<String, Object>> dateList = Collections.singletonList(dateCount);
        when(orderMapper.countByDate(7)).thenReturn(dateList);

        List<Map<String, Object>> result = orderService.countByDate(7);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("按月统计销售额")
    void sumAmountByMonth() {
        Map<String, Object> monthAmount = new HashMap<>();
        monthAmount.put("month", "2024-01");
        monthAmount.put("amount", new BigDecimal("50000.00"));
        List<Map<String, Object>> monthList = Collections.singletonList(monthAmount);
        when(orderMapper.sumAmountByMonth(6)).thenReturn(monthList);

        List<Map<String, Object>> result = orderService.sumAmountByMonth(6);

        assertEquals(1, result.size());
    }

    // ==================== 边缘情况测试 ====================

    @Test
    @DisplayName("查询所有订单 - 空列表")
    void findAll_Empty() {
        when(orderMapper.findAll()).thenReturn(new ArrayList<>());

        List<Order> result = orderService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("根据用户ID查询订单 - 无订单")
    void findByUserId_Empty() {
        when(orderMapper.findByUserId(999L)).thenReturn(new ArrayList<>());

        List<Order> result = orderService.findByUserId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("从购物车创建订单失败 - 商品不存在")
    void createFromCart_ProductNotFound() {
        List<Cart> carts = Collections.singletonList(testCart);
        when(cartMapper.findByUserId(1L)).thenReturn(carts);
        when(productMapper.findById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> 
            orderService.createFromCart(1L, "测试地址", "张三", "13800138000", "备注"));
    }

    @Test
    @DisplayName("从购物车创建订单 - 多个商品")
    void createFromCart_MultipleProducts() {
        Cart cart2 = new Cart();
        cart2.setId(2L);
        cart2.setUserId(1L);
        cart2.setProductId(2L);
        cart2.setProductName("MacBook Pro");
        cart2.setPrice(new BigDecimal("12999.00"));
        cart2.setQuantity(1);
        cart2.setSelected(true);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("MacBook Pro");
        product2.setPrice(new BigDecimal("12999.00"));
        product2.setStock(50);

        List<Cart> carts = Arrays.asList(testCart, cart2);
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
    }

    @Test
    @DisplayName("从购物车创建订单 - 部分商品选中")
    void createFromCart_PartialSelected() {
        Cart cart2 = new Cart();
        cart2.setId(2L);
        cart2.setUserId(1L);
        cart2.setProductId(2L);
        cart2.setProductName("MacBook Pro");
        cart2.setPrice(new BigDecimal("12999.00"));
        cart2.setQuantity(1);
        cart2.setSelected(false); // 未选中

        List<Cart> carts = Arrays.asList(testCart, cart2);
        when(cartMapper.findByUserId(1L)).thenReturn(carts);
        when(productMapper.findById(1L)).thenReturn(testProduct);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderMapper.insertOrderItem(any(OrderItem.class))).thenReturn(1);
        when(productMapper.updateStock(anyLong(), anyInt())).thenReturn(1);
        when(productMapper.updateSales(anyLong(), anyInt())).thenReturn(1);
        when(cartMapper.deleteSelected(1L)).thenReturn(1);

        Order result = orderService.createFromCart(1L, "测试地址", "张三", "13800138000", "备注");

        assertNotNull(result);
        assertEquals(new BigDecimal("6999.00"), result.getTotalAmount());
    }

    @Test
    @DisplayName("从购物车创建订单 - 库存刚好足够")
    void createFromCart_ExactStock() {
        testProduct.setStock(1); // 库存刚好等于购买数量
        testCart.setQuantity(1);
        List<Cart> carts = Collections.singletonList(testCart);
        when(cartMapper.findByUserId(1L)).thenReturn(carts);
        when(productMapper.findById(1L)).thenReturn(testProduct);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderMapper.insertOrderItem(any(OrderItem.class))).thenReturn(1);
        when(productMapper.updateStock(anyLong(), anyInt())).thenReturn(1);
        when(productMapper.updateSales(anyLong(), anyInt())).thenReturn(1);
        when(cartMapper.deleteSelected(1L)).thenReturn(1);

        Order result = orderService.createFromCart(1L, "测试地址", "张三", "13800138000", "备注");

        assertNotNull(result);
    }

    @Test
    @DisplayName("从购物车创建订单 - 大数量订单")
    void createFromCart_LargeQuantity() {
        testCart.setQuantity(1000);
        testProduct.setStock(2000);
        List<Cart> carts = Collections.singletonList(testCart);
        when(cartMapper.findByUserId(1L)).thenReturn(carts);
        when(productMapper.findById(1L)).thenReturn(testProduct);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderMapper.insertOrderItem(any(OrderItem.class))).thenReturn(1);
        when(productMapper.updateStock(anyLong(), anyInt())).thenReturn(1);
        when(productMapper.updateSales(anyLong(), anyInt())).thenReturn(1);
        when(cartMapper.deleteSelected(1L)).thenReturn(1);

        Order result = orderService.createFromCart(1L, "测试地址", "张三", "13800138000", "备注");

        assertNotNull(result);
        assertEquals(new BigDecimal("6999000.00"), result.getTotalAmount());
    }

    @Test
    @DisplayName("更新订单状态 - 失败")
    void updateStatus_Failure() {
        when(orderMapper.updateStatus(999L, 1)).thenReturn(0);

        boolean result = orderService.updateStatus(999L, 1);

        assertFalse(result);
    }

    @Test
    @DisplayName("更新订单状态 - 无效状态值")
    void updateStatus_InvalidStatus() {
        when(orderMapper.updateStatus(1L, -1)).thenReturn(1);

        boolean result = orderService.updateStatus(1L, -1);

        assertTrue(result);
    }

    @Test
    @DisplayName("删除订单 - 不存在")
    void delete_NotFound() {
        when(orderMapper.deleteById(999L)).thenReturn(0);

        boolean result = orderService.delete(999L);

        assertFalse(result);
    }

    @Test
    @DisplayName("统计订单数量 - 零")
    void count_Zero() {
        when(orderMapper.count()).thenReturn(0);

        int result = orderService.count();

        assertEquals(0, result);
    }

    @Test
    @DisplayName("统计各状态订单数量 - 空")
    void countByStatus_Empty() {
        when(orderMapper.countByStatus()).thenReturn(new ArrayList<>());

        List<Map<String, Object>> result = orderService.countByStatus();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("统计销售额 - 空")
    void sumTotalAmount_Empty() {
        when(orderMapper.sumTotalAmount()).thenReturn(new HashMap<>());

        Map<String, Object> result = orderService.sumTotalAmount();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("按日期统计订单 - 零天")
    void countByDate_ZeroDays() {
        when(orderMapper.countByDate(0)).thenReturn(new ArrayList<>());

        List<Map<String, Object>> result = orderService.countByDate(0);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("按日期统计订单 - 负数天")
    void countByDate_NegativeDays() {
        when(orderMapper.countByDate(-1)).thenReturn(new ArrayList<>());

        List<Map<String, Object>> result = orderService.countByDate(-1);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("按月统计销售额 - 零月")
    void sumAmountByMonth_ZeroMonths() {
        when(orderMapper.sumAmountByMonth(0)).thenReturn(new ArrayList<>());

        List<Map<String, Object>> result = orderService.sumAmountByMonth(0);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("从购物车创建订单 - 空地址")
    void createFromCart_EmptyAddress() {
        List<Cart> carts = Collections.singletonList(testCart);
        when(cartMapper.findByUserId(1L)).thenReturn(carts);
        when(productMapper.findById(1L)).thenReturn(testProduct);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderMapper.insertOrderItem(any(OrderItem.class))).thenReturn(1);
        when(productMapper.updateStock(anyLong(), anyInt())).thenReturn(1);
        when(productMapper.updateSales(anyLong(), anyInt())).thenReturn(1);
        when(cartMapper.deleteSelected(1L)).thenReturn(1);

        Order result = orderService.createFromCart(1L, "", "", "", "");

        assertNotNull(result);
        assertEquals("", result.getAddress());
    }

    @Test
    @DisplayName("从购物车创建订单 - null备注")
    void createFromCart_NullRemark() {
        List<Cart> carts = Collections.singletonList(testCart);
        when(cartMapper.findByUserId(1L)).thenReturn(carts);
        when(productMapper.findById(1L)).thenReturn(testProduct);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderMapper.insertOrderItem(any(OrderItem.class))).thenReturn(1);
        when(productMapper.updateStock(anyLong(), anyInt())).thenReturn(1);
        when(productMapper.updateSales(anyLong(), anyInt())).thenReturn(1);
        when(cartMapper.deleteSelected(1L)).thenReturn(1);

        Order result = orderService.createFromCart(1L, "测试地址", "张三", "13800138000", null);

        assertNotNull(result);
        assertNull(result.getRemark());
    }

    @Test
    @DisplayName("从购物车创建订单 - 3件商品选2件漏1件")
    void createFromCart_ThreeItemsTwoSelected() {
        Cart cart1 = new Cart();
        cart1.setId(1L);
        cart1.setUserId(1L);
        cart1.setProductId(1L);
        cart1.setProductName("iPhone 15");
        cart1.setPrice(new BigDecimal("6999.00"));
        cart1.setQuantity(1);
        cart1.setSelected(true);

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
        cart3.setPrice(new BigDecimal("799.00"));
        cart3.setQuantity(1);
        cart3.setSelected(false);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("iPhone 15");
        product1.setPrice(new BigDecimal("6999.00"));
        product1.setStock(100);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("MacBook Pro");
        product2.setPrice(new BigDecimal("12999.00"));
        product2.setStock(50);

        Product product3 = new Product();
        product3.setId(3L);
        product3.setName("AirPods Pro");
        product3.setPrice(new BigDecimal("799.00"));
        product3.setStock(200);

        List<Cart> carts = Arrays.asList(cart1, cart2, cart3);
        when(cartMapper.findByUserId(1L)).thenReturn(carts);
        when(productMapper.findById(1L)).thenReturn(product1);
        when(productMapper.findById(2L)).thenReturn(product2);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderMapper.insertOrderItem(any(OrderItem.class))).thenReturn(1);
        when(productMapper.updateStock(anyLong(), anyInt())).thenReturn(1);
        when(productMapper.updateSales(anyLong(), anyInt())).thenReturn(1);
        when(cartMapper.deleteSelected(1L)).thenReturn(2);

        Order result = orderService.createFromCart(1L, "测试地址", "张三", "13800138000", "备注");

        assertNotNull(result);
        assertEquals(new BigDecimal("19998.00"), result.getTotalAmount());
        verify(productMapper, times(2)).updateStock(anyLong(), anyInt());
        verify(productMapper).updateStock(1L, 1);
        verify(productMapper).updateStock(2L, 1);
        verify(productMapper, never()).updateStock(3L, 1);
        verify(productMapper, times(2)).updateSales(anyLong(), anyInt());
        verify(productMapper, never()).updateSales(3L, 1);
        verify(cartMapper).deleteSelected(1L);
        verify(orderMapper, times(2)).insertOrderItem(any(OrderItem.class));
    }

    @Test
    @DisplayName("从购物车创建订单 - 全部商品未选中")
    void createFromCart_AllItemsUnselected() {
        Cart cart1 = new Cart();
        cart1.setId(1L);
        cart1.setUserId(1L);
        cart1.setProductId(1L);
        cart1.setProductName("iPhone 15");
        cart1.setPrice(new BigDecimal("6999.00"));
        cart1.setQuantity(1);
        cart1.setSelected(false);

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
        cart3.setPrice(new BigDecimal("799.00"));
        cart3.setQuantity(1);
        cart3.setSelected(false);

        List<Cart> carts = Arrays.asList(cart1, cart2, cart3);
        when(cartMapper.findByUserId(1L)).thenReturn(carts);

        Order result = orderService.createFromCart(1L, "测试地址", "张三", "13800138000", "备注");

        assertNull(result);
        verify(productMapper, never()).updateStock(anyLong(), anyInt());
        verify(productMapper, never()).updateSales(anyLong(), anyInt());
        verify(orderMapper, never()).insert(any(Order.class));
        verify(orderMapper, never()).insertOrderItem(any(OrderItem.class));
        verify(cartMapper, never()).deleteSelected(1L);
    }
}
