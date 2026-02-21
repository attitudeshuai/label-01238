package com.shop.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车实体类
 */
@Data
public class Cart {
    /** 购物车ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 商品ID */
    private Long productId;
    /** 商品名称 */
    private String productName;
    /** 商品图片 */
    private String productImage;
    /** 商品单价 */
    private BigDecimal price;
    /** 库存 */
    private Integer stock;
    /** 数量 */
    private Integer quantity;
    /** 是否选中 */
    private Boolean selected;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
