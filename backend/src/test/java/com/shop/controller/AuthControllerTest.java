package com.shop.controller;

import com.shop.common.Result;
import com.shop.dto.LoginDTO;
import com.shop.dto.RegisterDTO;
import com.shop.entity.User;
import com.shop.service.UserService;
import com.shop.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("认证控制器测试")
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setNickname("测试用户");
        testUser.setRole("user");
        testUser.setStatus(1);
    }

    @Test
    @DisplayName("登录成功")
    void login_Success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("123456");

        when(userService.login("testuser", "123456")).thenReturn(testUser);
        when(jwtUtil.generateToken(1L, "testuser", "user")).thenReturn("mock-token");

        Result<?> result = authController.login(loginDTO);

        assertEquals(200, result.getCode());
        assertEquals("登录成功", result.getMessage());
        assertNotNull(result.getData());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals("mock-token", data.get("token"));
    }

    @Test
    @DisplayName("登录失败 - 用户名或密码错误")
    void login_Failure() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("wrongpassword");

        when(userService.login("testuser", "wrongpassword")).thenReturn(null);

        Result<?> result = authController.login(loginDTO);

        assertEquals(500, result.getCode());
        assertEquals("用户名或密码错误", result.getMessage());
    }

    @Test
    @DisplayName("注册成功")
    void register_Success() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setPassword("123456");
        registerDTO.setNickname("新用户");

        when(userService.register(any(User.class))).thenReturn(true);

        Result<?> result = authController.register(registerDTO);

        assertEquals(200, result.getCode());
    }

    @Test
    @DisplayName("注册失败 - 用户名已存在")
    void register_UsernameExists() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("existuser");
        registerDTO.setPassword("123456");
        registerDTO.setNickname("已存在用户");

        when(userService.register(any(User.class))).thenReturn(false);

        Result<?> result = authController.register(registerDTO);

        assertEquals(500, result.getCode());
        assertEquals("用户名已存在", result.getMessage());
    }
}
