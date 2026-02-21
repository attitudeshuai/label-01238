package com.shop.controller;

import com.shop.common.Result;
import com.shop.dto.OrderCreateDTO;
import com.shop.entity.Order;
import com.shop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取所有订单(管理员)
     */
    @GetMapping("/all")
    public Result<List<Order>> all() {
        return Result.success(orderService.findAll());
    }

    /**
     * 获取用户订单
     */
    @GetMapping("/list")
    public Result<List<Order>> list(@RequestAttribute Long userId) {
        return Result.success(orderService.findByUserId(userId));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/detail/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        Order order = orderService.findById(id);
        if (order != null) {
            return Result.success(order);
        }
        return Result.error("订单不存在");
    }

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<?> create(@RequestAttribute Long userId, @Valid @RequestBody OrderCreateDTO dto) {
        try {
            Order order = orderService.createFromCart(userId, dto.getAddress(), dto.getReceiver(), dto.getPhone(), dto.getRemark());
            if (order != null) {
                return Result.success("下单成功", order);
            }
            return Result.error("购物车为空");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        if (orderService.updateStatus(id, status)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (orderService.delete(id)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
