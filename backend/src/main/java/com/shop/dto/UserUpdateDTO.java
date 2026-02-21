package com.shop.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 用户更新请求DTO
 */
@Data
public class UserUpdateDTO {
    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Size(min = 2, max = 20, message = "用户名长度为2-20个字符")
    private String username;

    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String role;

    private Integer status;
}
