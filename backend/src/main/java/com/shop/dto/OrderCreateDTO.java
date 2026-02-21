package com.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建订单请求DTO
 */
@Data
public class OrderCreateDTO {
    @NotBlank(message = "收货地址不能为空")
    @Size(min = 5, max = 200, message = "地址长度为5-200个字符")
    private String address;

    @NotBlank(message = "收货人不能为空")
    @Size(min = 2, max = 20, message = "收货人姓名长度为2-20个字符")
    private String receiver;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String remark;
}
