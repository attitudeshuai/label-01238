package com.shop.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JWT工具类测试")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 设置测试用的secret和expiration
        ReflectionTestUtils.setField(jwtUtil, "secret", "test-secret-key-for-unit-testing-purposes-only-12345");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    @DisplayName("生成Token")
    void generateToken() {
        String token = jwtUtil.generateToken(1L, "testuser", "user");

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    @DisplayName("解析Token")
    void parseToken() {
        String token = jwtUtil.generateToken(1L, "testuser", "admin");

        Claims claims = jwtUtil.parseToken(token);

        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
        assertEquals(1L, claims.get("userId", Long.class));
        assertEquals("admin", claims.get("role", String.class));
    }

    @Test
    @DisplayName("获取用户ID")
    void getUserId() {
        String token = jwtUtil.generateToken(123L, "testuser", "user");

        Long userId = jwtUtil.getUserId(token);

        assertEquals(123L, userId);
    }

    @Test
    @DisplayName("获取用户名")
    void getUsername() {
        String token = jwtUtil.generateToken(1L, "admin", "admin");

        String username = jwtUtil.getUsername(token);

        assertEquals("admin", username);
    }

    @Test
    @DisplayName("获取角色")
    void getRole() {
        String token = jwtUtil.generateToken(1L, "testuser", "admin");

        String role = jwtUtil.getRole(token);

        assertEquals("admin", role);
    }

    @Test
    @DisplayName("Token未过期")
    void isTokenExpired_NotExpired() {
        String token = jwtUtil.generateToken(1L, "testuser", "user");

        boolean expired = jwtUtil.isTokenExpired(token);

        assertFalse(expired);
    }

    @Test
    @DisplayName("Token已过期")
    void isTokenExpired_Expired() {
        // 设置过期时间为0，生成立即过期的token
        ReflectionTestUtils.setField(jwtUtil, "expiration", 0L);
        String token = jwtUtil.generateToken(1L, "testuser", "user");

        // 等待一小段时间确保过期
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean expired = jwtUtil.isTokenExpired(token);

        assertTrue(expired);
    }

    @Test
    @DisplayName("无效Token返回过期")
    void isTokenExpired_InvalidToken() {
        boolean expired = jwtUtil.isTokenExpired("invalid-token");

        assertTrue(expired);
    }

    // ==================== 边缘情况测试 ====================

    @Test
    @DisplayName("生成Token - 空用户名")
    void generateToken_EmptyUsername() {
        String token = jwtUtil.generateToken(1L, "", "user");

        assertNotNull(token);
        // JWT subject 为空字符串时，解析后返回 null
        assertNull(jwtUtil.getUsername(token));
    }

    @Test
    @DisplayName("生成Token - 空角色")
    void generateToken_EmptyRole() {
        String token = jwtUtil.generateToken(1L, "testuser", "");

        assertNotNull(token);
        assertEquals("", jwtUtil.getRole(token));
    }

    @Test
    @DisplayName("生成Token - null用户名")
    void generateToken_NullUsername() {
        String token = jwtUtil.generateToken(1L, null, "user");

        assertNotNull(token);
        assertNull(jwtUtil.getUsername(token));
    }

    @Test
    @DisplayName("生成Token - null角色")
    void generateToken_NullRole() {
        String token = jwtUtil.generateToken(1L, "testuser", null);

        assertNotNull(token);
        assertNull(jwtUtil.getRole(token));
    }

    @Test
    @DisplayName("生成Token - 用户ID为0")
    void generateToken_ZeroUserId() {
        String token = jwtUtil.generateToken(0L, "testuser", "user");

        assertNotNull(token);
        assertEquals(0L, jwtUtil.getUserId(token));
    }

    @Test
    @DisplayName("生成Token - 负数用户ID")
    void generateToken_NegativeUserId() {
        String token = jwtUtil.generateToken(-1L, "testuser", "user");

        assertNotNull(token);
        assertEquals(-1L, jwtUtil.getUserId(token));
    }

    @Test
    @DisplayName("生成Token - 特殊字符用户名")
    void generateToken_SpecialCharUsername() {
        String specialUsername = "user!@#$%^&*()";
        String token = jwtUtil.generateToken(1L, specialUsername, "user");

        assertNotNull(token);
        assertEquals(specialUsername, jwtUtil.getUsername(token));
    }

    @Test
    @DisplayName("生成Token - Unicode用户名")
    void generateToken_UnicodeUsername() {
        String unicodeUsername = "测试用户";
        String token = jwtUtil.generateToken(1L, unicodeUsername, "user");

        assertNotNull(token);
        assertEquals(unicodeUsername, jwtUtil.getUsername(token));
    }

    @Test
    @DisplayName("生成Token - 长用户名")
    void generateToken_LongUsername() {
        String longUsername = "a".repeat(1000);
        String token = jwtUtil.generateToken(1L, longUsername, "user");

        assertNotNull(token);
        assertEquals(longUsername, jwtUtil.getUsername(token));
    }

    @Test
    @DisplayName("解析Token - 空Token")
    void parseToken_EmptyToken() {
        assertThrows(Exception.class, () -> jwtUtil.parseToken(""));
    }

    @Test
    @DisplayName("解析Token - 格式错误的Token")
    void parseToken_MalformedToken() {
        assertThrows(Exception.class, () -> jwtUtil.parseToken("not.a.valid.jwt.token"));
    }

    @Test
    @DisplayName("获取用户ID - 无效Token")
    void getUserId_InvalidToken() {
        assertThrows(Exception.class, () -> jwtUtil.getUserId("invalid-token"));
    }

    @Test
    @DisplayName("获取用户名 - 无效Token")
    void getUsername_InvalidToken() {
        assertThrows(Exception.class, () -> jwtUtil.getUsername("invalid-token"));
    }

    @Test
    @DisplayName("获取角色 - 无效Token")
    void getRole_InvalidToken() {
        assertThrows(Exception.class, () -> jwtUtil.getRole("invalid-token"));
    }

    @Test
    @DisplayName("Token过期检查 - 空Token")
    void isTokenExpired_EmptyToken() {
        boolean expired = jwtUtil.isTokenExpired("");

        assertTrue(expired);
    }

    @Test
    @DisplayName("Token过期检查 - null Token")
    void isTokenExpired_NullToken() {
        boolean expired = jwtUtil.isTokenExpired(null);

        assertTrue(expired);
    }

    @Test
    @DisplayName("生成Token - 大用户ID")
    void generateToken_LargeUserId() {
        String token = jwtUtil.generateToken(Long.MAX_VALUE, "testuser", "user");

        assertNotNull(token);
        assertEquals(Long.MAX_VALUE, jwtUtil.getUserId(token));
    }
}
