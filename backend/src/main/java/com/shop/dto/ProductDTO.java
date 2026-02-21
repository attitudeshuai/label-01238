package com.shop.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 商品请求DTO
 */
@Data
public class ProductDTO {
    private Long id;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称长度不能超过100个字符")
    private String name;

    @Size(max = 1000, message = "商品描述长度不能超过1000个字符")
    private String description;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    @DecimalMax(value = "9999999.99", message = "商品价格超出范围")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    private String image;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private Integer status = 1;
}
