package com.shop.controller;

import com.shop.common.Result;
import com.shop.dto.CartAddDTO;
import com.shop.entity.Cart;
import com.shop.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public Result<List<Cart>> list(@RequestAttribute Long userId) {
        return Result.success(cartService.findByUserId(userId));
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public Result<?> add(@RequestAttribute Long userId, @Valid @RequestBody CartAddDTO dto) {
        if (cartService.add(userId, dto.getProductId(), dto.getQuantity())) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 更新购物车数量
     */
    @PutMapping("/update/{id}")
    public Result<?> updateQuantity(@PathVariable Long id, @RequestBody Map<String, @Min(value = 1, message = "数量至少为1") Integer> params) {
        Integer quantity = params.get("quantity");
        if (quantity == null || quantity < 1) {
            return Result.error("数量至少为1");
        }
        if (cartService.updateQuantity(id, quantity)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 更新选中状态
     */
    @PutMapping("/select/{id}")
    public Result<?> updateSelected(@PathVariable Long id, @RequestBody Map<String, Boolean> params) {
        Boolean selected = params.get("selected");
        if (cartService.updateSelected(id, selected)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 全选/取消全选
     */
    @PutMapping("/selectAll")
    public Result<?> selectAll(@RequestAttribute Long userId, @RequestBody Map<String, Boolean> params) {
        Boolean selected = params.get("selected");
        if (cartService.updateAllSelected(userId, selected)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除购物车项
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (cartService.delete(id)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public Result<?> clear(@RequestAttribute Long userId) {
        if (cartService.clear(userId)) {
            return Result.success("清空成功");
        }
        return Result.error("清空失败");
    }
}
