package com.shop.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("统一响应结果类测试")
class ResultTest {

    @Test
    @DisplayName("成功响应 - 无数据")
    void success_NoData() {
        Result<Void> result = Result.success();

        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("成功响应 - 带数据")
    void success_WithData() {
        String data = "test data";
        Result<String> result = Result.success(data);

        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals("test data", result.getData());
    }

    @Test
    @DisplayName("成功响应 - 带消息和数据")
    void success_WithMessageAndData() {
        String data = "test data";
        Result<String> result = Result.success("操作成功", data);

        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals("test data", result.getData());
    }

    @Test
    @DisplayName("错误响应 - 默认状态码")
    void error_DefaultCode() {
        Result<Void> result = Result.error("操作失败");

        assertEquals(500, result.getCode());
        assertEquals("操作失败", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("错误响应 - 自定义状态码")
    void error_CustomCode() {
        Result<Void> result = Result.error(401, "未授权");

        assertEquals(401, result.getCode());
        assertEquals("未授权", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("泛型数据类型")
    void genericDataType() {
        Result<Integer> intResult = Result.success(123);
        assertEquals(123, intResult.getData());

        Result<Double> doubleResult = Result.success(3.14);
        assertEquals(3.14, doubleResult.getData());
    }
}
