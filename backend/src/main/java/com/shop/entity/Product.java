package com.shop.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
public class Product {
    /** 商品ID */
    private Long id;
    /** 商品名称 */
    private String name;
    /** 商品描述 */
    private String description;
    /** 商品价格 */
    private BigDecimal price;
    /** 库存数量 */
    private Integer stock;
    /** 商品图片 */
    private String image;
    /** 分类ID */
    private Long categoryId;
    /** 分类名称 */
    private String categoryName;
    /** 状态: 0-下架, 1-上架 */
    private Integer status;
    /** 销量 */
    private Integer sales;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
