package com.shop.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品分类实体类
 */
@Data
public class Category {
    /** 分类ID */
    private Long id;
    /** 分类名称 */
    private String name;
    /** 分类描述 */
    private String description;
    /** 排序 */
    private Integer sort;
    /** 状态: 0-禁用, 1-启用 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
