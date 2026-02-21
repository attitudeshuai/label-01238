package com.shop.service;

import com.shop.entity.User;
import com.shop.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务单元测试")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        testUser.setNickname("测试用户");
        testUser.setRole("user");
        testUser.setStatus(1);
    }

    @Test
    @DisplayName("登录成功")
    void login_Success() {
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        User result = userService.login("testuser", "123456");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper).findByUsername("testuser");
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void login_UserNotFound() {
        when(userMapper.findByUsername("nonexistent")).thenReturn(null);

        User result = userService.login("nonexistent", "123456");

        assertNull(result);
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void login_WrongPassword() {
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        User result = userService.login("testuser", "wrongpassword");

        assertNull(result);
    }

    @Test
    @DisplayName("登录失败 - 用户被禁用")
    void login_UserDisabled() {
        testUser.setStatus(0);
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        User result = userService.login("testuser", "123456");

        assertNull(result);
    }

    @Test
    @DisplayName("注册成功")
    void register_Success() {
        when(userMapper.findByUsername("newuser")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("123456");

        boolean result = userService.register(newUser);

        assertTrue(result);
        assertEquals("user", newUser.getRole());
        assertEquals(1, newUser.getStatus());
        verify(userMapper).insert(any(User.class));
    }

    @Test
    @DisplayName("注册失败 - 用户名已存在")
    void register_UsernameExists() {
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("123456");

        boolean result = userService.register(newUser);

        assertFalse(result);
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("查询所有用户")
    void findAll() {
        List<User> users = Arrays.asList(testUser, new User());
        when(userMapper.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("根据ID查询用户")
    void findById() {
        when(userMapper.findById(1L)).thenReturn(testUser);

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("更新用户")
    void update() {
        when(userMapper.update(any(User.class))).thenReturn(1);

        boolean result = userService.update(testUser);

        assertTrue(result);
        verify(userMapper).update(testUser);
    }

    @Test
    @DisplayName("删除用户")
    void delete() {
        when(userMapper.deleteById(1L)).thenReturn(1);

        boolean result = userService.delete(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("更新用户状态")
    void updateStatus() {
        when(userMapper.updateStatus(1L, 0)).thenReturn(1);

        boolean result = userService.updateStatus(1L, 0);

        assertTrue(result);
    }

    @Test
    @DisplayName("统计用户数量")
    void count() {
        when(userMapper.count()).thenReturn(10);

        int result = userService.count();

        assertEquals(10, result);
    }

    // ==================== 边缘情况测试 ====================

    @Test
    @DisplayName("登录 - 空用户名")
    void login_EmptyUsername() {
        when(userMapper.findByUsername("")).thenReturn(null);

        User result = userService.login("", "123456");

        assertNull(result);
    }

    @Test
    @DisplayName("登录 - 空密码")
    void login_EmptyPassword() {
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        User result = userService.login("testuser", "");

        assertNull(result);
    }

    @Test
    @DisplayName("登录 - null用户名")
    void login_NullUsername() {
        when(userMapper.findByUsername(null)).thenReturn(null);

        User result = userService.login(null, "123456");

        assertNull(result);
    }

    @Test
    @DisplayName("注册 - 空用户名")
    void register_EmptyUsername() {
        when(userMapper.findByUsername("")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User newUser = new User();
        newUser.setUsername("");
        newUser.setPassword("123456");

        boolean result = userService.register(newUser);

        assertTrue(result);
    }

    @Test
    @DisplayName("注册 - 空密码")
    void register_EmptyPassword() {
        when(userMapper.findByUsername("newuser")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("");

        boolean result = userService.register(newUser);

        assertTrue(result);
    }

    @Test
    @DisplayName("注册 - 插入失败")
    void register_InsertFailure() {
        when(userMapper.findByUsername("newuser")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(0);

        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("123456");

        boolean result = userService.register(newUser);

        assertFalse(result);
    }

    @Test
    @DisplayName("根据ID查询用户 - 不存在")
    void findById_NotFound() {
        when(userMapper.findById(999L)).thenReturn(null);

        User result = userService.findById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("更新用户 - 失败")
    void update_Failure() {
        when(userMapper.update(any(User.class))).thenReturn(0);

        boolean result = userService.update(testUser);

        assertFalse(result);
    }

    @Test
    @DisplayName("删除用户 - 不存在")
    void delete_NotFound() {
        when(userMapper.deleteById(999L)).thenReturn(0);

        boolean result = userService.delete(999L);

        assertFalse(result);
    }

    @Test
    @DisplayName("更新用户状态 - 失败")
    void updateStatus_Failure() {
        when(userMapper.updateStatus(999L, 0)).thenReturn(0);

        boolean result = userService.updateStatus(999L, 0);

        assertFalse(result);
    }

    @Test
    @DisplayName("更新用户状态 - 无效状态值")
    void updateStatus_InvalidStatus() {
        when(userMapper.updateStatus(1L, -1)).thenReturn(1);

        boolean result = userService.updateStatus(1L, -1);

        assertTrue(result);
    }

    @Test
    @DisplayName("登录 - 特殊字符密码")
    void login_SpecialCharPassword() {
        String specialPassword = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        testUser.setPassword(DigestUtils.md5DigestAsHex(specialPassword.getBytes()));
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        User result = userService.login("testuser", specialPassword);

        assertNotNull(result);
    }

    @Test
    @DisplayName("登录 - Unicode密码")
    void login_UnicodePassword() {
        String unicodePassword = "密码测试123";
        testUser.setPassword(DigestUtils.md5DigestAsHex(unicodePassword.getBytes()));
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        User result = userService.login("testuser", unicodePassword);

        assertNotNull(result);
    }

    @Test
    @DisplayName("查询所有用户 - 空列表")
    void findAll_Empty() {
        when(userMapper.findAll()).thenReturn(Arrays.asList());

        List<User> result = userService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("统计用户数量 - 零")
    void count_Zero() {
        when(userMapper.count()).thenReturn(0);

        int result = userService.count();

        assertEquals(0, result);
    }
}
