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
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Transactional
    public Order createFromCart(Long userId, String address, String receiver, String phone, String remark) {
        List<Cart> carts = cartMapper.findByUserId(userId);
        
        List<Cart> selectedCarts = new ArrayList<>();
        for (Cart cart : carts) {
            if (cart.getSelected() != null && cart.getSelected()) {
                selectedCarts.add(cart);
            }
        }
        
        if (selectedCarts.isEmpty()) {
            return null;
        }
        
        Order order = new Order();
        order.setUserId(userId);
        order.setAddress(address);
        order.setReceiver(receiver);
        order.setPhone(phone);
        order.setRemark(remark);
        order.setStatus(0);
        
        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
            + String.format("%04d", (int)(Math.random() * 10000));
        order.setOrderNo(orderNo);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (Cart cart : selectedCarts) {
            Product product = productMapper.findById(cart.getProductId());
            if (product == null) {
                continue;
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cart.getProductId());
            orderItem.setProductName(cart.getProductName());
            orderItem.setProductImage(cart.getProductImage());
            orderItem.setPrice(cart.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            BigDecimal subtotal = cart.getPrice().multiply(new BigDecimal(cart.getQuantity()));
            orderItem.setSubtotal(subtotal);
            orderItems.add(orderItem);
            
            totalAmount = totalAmount.add(subtotal);
            
            productMapper.updateStock(cart.getProductId(), cart.getQuantity());
            productMapper.updateSales(cart.getProductId(), cart.getQuantity());
        }
        
        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);
        
        orderMapper.insert(order);
        
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order.getId());
            orderMapper.insertOrderItem(orderItem);
        }
        
        cartMapper.deleteSelected(userId);
        
        return order;
    }
}
