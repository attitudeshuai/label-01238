package com.shop.service;

import com.shop.entity.Cart;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.entity.Product;
import com.shop.mapper.CartMapper;
import com.shop.mapper.OrderMapper;
import com.shop.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 订单服务类
 */
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 查询所有订单
     */
    public List<Order> findAll() {
        List<Order> orders = orderMapper.findAll();
        for (Order order : orders) {
            order.setItems(orderMapper.findOrderItems(order.getId()));
        }
        return orders;
    }

    /**
     * 根据ID查询订单
     */
    public Order findById(Long id) {
        Order order = orderMapper.findById(id);
        if (order != null) {
            order.setItems(orderMapper.findOrderItems(id));
        }
        return order;
    }

    /**
     * 根据用户ID查询订单
     */
    public List<Order> findByUserId(Long userId) {
        List<Order> orders = orderMapper.findByUserId(userId);
        for (Order order : orders) {
            order.setItems(orderMapper.findOrderItems(order.getId()));
        }
        return orders;
    }

    /**
     * 创建订单(从购物车)
     */
    @Transactional
    public Order createFromCart(Long userId, String address, String receiver, String phone, String remark) {
        // 获取选中的购物车项
        List<Cart> cartList = cartMapper.findByUserId(userId);
        List<Cart> selectedCarts = cartList.stream().filter(Cart::getSelected).toList();
        if (selectedCarts.isEmpty()) {
            return null;
        }

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();
        for (Cart cart : selectedCarts) {
            Product product = productMapper.findById(cart.getProductId());
            if (product == null || product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("商品库存不足: " + cart.getProductName());
            }
            BigDecimal subtotal = cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem item = new OrderItem();
            item.setProductId(cart.getProductId());
            item.setProductName(cart.getProductName());
            item.setProductImage(cart.getProductImage());
            item.setPrice(cart.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(subtotal);
            items.add(item);
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        order.setAddress(address);
        order.setReceiver(receiver);
        order.setPhone(phone);
        order.setRemark(remark);
        orderMapper.insert(order);

        // 创建订单项并更新库存
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderMapper.insertOrderItem(item);
            productMapper.updateStock(item.getProductId(), item.getQuantity());
            productMapper.updateSales(item.getProductId(), item.getQuantity());
        }

        // 删除已购买的购物车项
        cartMapper.deleteSelected(userId);

        order.setItems(items);
        return order;
    }

    /**
     * 更新订单状态
     */
    public boolean updateStatus(Long id, Integer status) {
        return orderMapper.updateStatus(id, status) > 0;
    }

    /**
     * 删除订单
     */
    public boolean delete(Long id) {
        return orderMapper.deleteById(id) > 0;
    }

    /**
     * 统计订单总数
     */
    public int count() {
        return orderMapper.count();
    }

    /**
     * 统计各状态订单数量
     */
    public List<Map<String, Object>> countByStatus() {
        return orderMapper.countByStatus();
    }

    /**
     * 统计销售额
     */
    public Map<String, Object> sumTotalAmount() {
        return orderMapper.sumTotalAmount();
    }

    /**
     * 按日期统计订单
     */
    public List<Map<String, Object>> countByDate(Integer days) {
        return orderMapper.countByDate(days);
    }

    /**
     * 按月统计销售额
     */
    public List<Map<String, Object>> sumAmountByMonth(Integer months) {
        return orderMapper.sumAmountByMonth(months);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return timestamp + random;
    }
}
