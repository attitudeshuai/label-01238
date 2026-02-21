package com.shop.controller;

import com.shop.common.Result;
import com.shop.service.OrderService;
import com.shop.service.ProductService;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计控制器
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    /**
     * 获取概览数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userService.count());
        data.put("productCount", productService.count());
        data.put("orderCount", orderService.count());
        data.put("salesAmount", orderService.sumTotalAmount());
        return Result.success(data);
    }

    /**
     * 获取订单状态统计
     */
    @GetMapping("/orderStatus")
    public Result<List<Map<String, Object>>> orderStatus() {
        return Result.success(orderService.countByStatus());
    }

    /**
     * 获取近期订单统计
     */
    @GetMapping("/recentOrders")
    public Result<List<Map<String, Object>>> recentOrders(@RequestParam(defaultValue = "7") Integer days) {
        return Result.success(orderService.countByDate(days));
    }

    /**
     * 获取月度销售统计
     */
    @GetMapping("/monthlySales")
    public Result<List<Map<String, Object>>> monthlySales(@RequestParam(defaultValue = "6") Integer months) {
        return Result.success(orderService.sumAmountByMonth(months));
    }

    /**
     * 获取热销商品
     */
    @GetMapping("/hotProducts")
    public Result<?> hotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(productService.findHotProducts(limit));
    }
}
