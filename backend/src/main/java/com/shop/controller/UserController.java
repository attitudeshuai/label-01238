package com.shop.controller;

import com.shop.common.Result;
import com.shop.dto.RegisterDTO;
import com.shop.dto.UserUpdateDTO;
import com.shop.entity.User;
import com.shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户(管理员)
     */
    @GetMapping("/list")
    public Result<List<User>> list() {
        List<User> users = userService.findAll();
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/detail/{id}")
    public Result<User> detail(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<?> update(@Valid @RequestBody UserUpdateDTO dto) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        if (userService.update(user)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (userService.delete(id)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        if (userService.updateStatus(id, status)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 新增用户(管理员)
     */
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody RegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        if (userService.register(user)) {
            return Result.success("添加成功");
        }
        return Result.error("用户名已存在");
    }
}
