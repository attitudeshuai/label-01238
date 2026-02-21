package com.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 分类请求DTO
 */
@Data
public class CategoryDTO {
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    private String name;

    @Size(max = 255, message = "分类描述长度不能超过255个字符")
    private String description;

    private Integer sort = 0;

    private Integer status = 1;
}
