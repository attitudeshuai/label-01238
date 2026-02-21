package com.shop.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 */
@Data
public class Order {
    /** 订单ID */
    private Long id;
    /** 订单编号 */
    private String orderNo;
    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String username;
    /** 订单总金额 */
    private BigDecimal totalAmount;
    /** 订单状态: 0-待支付, 1-已支付, 2-已发货, 3-已完成, 4-已取消 */
    private Integer status;
    /** 收货地址 */
    private String address;
    /** 收货人 */
    private String receiver;
    /** 收货人电话 */
    private String phone;
    /** 备注 */
    private String remark;
    /** 支付时间 */
    private LocalDateTime payTime;
    /** 发货时间 */
    private LocalDateTime shipTime;
    /** 完成时间 */
    private LocalDateTime finishTime;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
    /** 订单项列表 */
    private List<OrderItem> items;
}
