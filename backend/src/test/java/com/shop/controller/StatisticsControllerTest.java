package com.shop.controller;

import com.shop.common.Result;
import com.shop.service.OrderService;
import com.shop.service.ProductService;
import com.shop.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("统计控制器测试")
class StatisticsControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @InjectMocks
    private StatisticsController statisticsController;

    @Test
    @DisplayName("获取概览数据")
    void overview() {
        when(userService.count()).thenReturn(100);
        when(productService.count()).thenReturn(50);
        when(orderService.count()).thenReturn(200);
        
        Map<String, Object> salesAmount = new HashMap<>();
        salesAmount.put("paid", new BigDecimal("100000.00"));
        salesAmount.put("pending", new BigDecimal("20000.00"));
        when(orderService.sumTotalAmount()).thenReturn(salesAmount);

        Result<Map<String, Object>> result = statisticsController.overview();

        assertEquals(200, result.getCode());
        assertEquals(100, result.getData().get("userCount"));
        assertEquals(50, result.getData().get("productCount"));
        assertEquals(200, result.getData().get("orderCount"));
    }

    @Test
    @DisplayName("获取订单状态统计")
    void orderStatus() {
        List<Map<String, Object>> statusList = new ArrayList<>();
        Map<String, Object> status1 = new HashMap<>();
        status1.put("status", 0);
        status1.put("count", 10);
        statusList.add(status1);
        
        Map<String, Object> status2 = new HashMap<>();
        status2.put("status", 1);
        status2.put("count", 20);
        statusList.add(status2);

        when(orderService.countByStatus()).thenReturn(statusList);

        Result<List<Map<String, Object>>> result = statisticsController.orderStatus();

        assertEquals(200, result.getCode());
        assertEquals(2, result.getData().size());
    }

    @Test
    @DisplayName("获取近期订单统计 - 默认7天")
    void recentOrders_Default() {
        List<Map<String, Object>> dateList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Map<String, Object> dateCount = new HashMap<>();
            dateCount.put("date", "2024-01-0" + (i + 1));
            dateCount.put("count", i + 1);
            dateList.add(dateCount);
        }

        when(orderService.countByDate(7)).thenReturn(dateList);

        Result<List<Map<String, Object>>> result = statisticsController.recentOrders(7);

        assertEquals(200, result.getCode());
        assertEquals(7, result.getData().size());
    }

    @Test
    @DisplayName("获取近期订单统计 - 指定天数")
    void recentOrders_CustomDays() {
        List<Map<String, Object>> dateList = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            Map<String, Object> dateCount = new HashMap<>();
            dateCount.put("date", "2024-01-" + String.format("%02d", i + 1));
            dateCount.put("count", i + 1);
            dateList.add(dateCount);
        }

        when(orderService.countByDate(14)).thenReturn(dateList);

        Result<List<Map<String, Object>>> result = statisticsController.recentOrders(14);

        assertEquals(200, result.getCode());
        assertEquals(14, result.getData().size());
    }

    @Test
    @DisplayName("获取月度销售统计 - 默认6个月")
    void monthlySales_Default() {
        List<Map<String, Object>> monthList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Map<String, Object> monthAmount = new HashMap<>();
            monthAmount.put("month", "2024-0" + (i + 1));
            monthAmount.put("amount", new BigDecimal((i + 1) * 10000));
            monthList.add(monthAmount);
        }

        when(orderService.sumAmountByMonth(6)).thenReturn(monthList);

        Result<List<Map<String, Object>>> result = statisticsController.monthlySales(6);

        assertEquals(200, result.getCode());
        assertEquals(6, result.getData().size());
    }

    @Test
    @DisplayName("获取月度销售统计 - 指定月数")
    void monthlySales_CustomMonths() {
        List<Map<String, Object>> monthList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Map<String, Object> monthAmount = new HashMap<>();
            monthAmount.put("month", "2024-" + String.format("%02d", i + 1));
            monthAmount.put("amount", new BigDecimal((i + 1) * 10000));
            monthList.add(monthAmount);
        }

        when(orderService.sumAmountByMonth(12)).thenReturn(monthList);

        Result<List<Map<String, Object>>> result = statisticsController.monthlySales(12);

        assertEquals(200, result.getCode());
        assertEquals(12, result.getData().size());
    }

    @Test
    @DisplayName("获取热销商品 - 默认10个")
    void hotProducts_Default() {
        List<Map<String, Object>> hotList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> product = new HashMap<>();
            product.put("id", (long) (i + 1));
            product.put("name", "商品" + (i + 1));
            product.put("salesCount", (i + 1) * 10);
            product.put("salesAmount", new BigDecimal((i + 1) * 1000));
            hotList.add(product);
        }

        when(productService.findHotProducts(10)).thenReturn(hotList);

        Result<?> result = statisticsController.hotProducts(10);

        assertEquals(200, result.getCode());
    }

    @Test
    @DisplayName("获取热销商品 - 指定数量")
    void hotProducts_CustomLimit() {
        List<Map<String, Object>> hotList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> product = new HashMap<>();
            product.put("id", (long) (i + 1));
            product.put("name", "商品" + (i + 1));
            product.put("salesCount", (i + 1) * 10);
            hotList.add(product);
        }

        when(productService.findHotProducts(5)).thenReturn(hotList);

        Result<?> result = statisticsController.hotProducts(5);

        assertEquals(200, result.getCode());
    }
}
