package com.shop.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 订单项实体类
 */
@Data
public class OrderItem {
    /** 订单项ID */
    private Long id;
    /** 订单ID */
    private Long orderId;
    /** 商品ID */
    private Long productId;
    /** 商品名称 */
    private String productName;
    /** 商品图片 */
    private String productImage;
    /** 商品单价 */
    private BigDecimal price;
    /** 购买数量 */
    private Integer quantity;
    /** 小计金额 */
    private BigDecimal subtotal;
}
