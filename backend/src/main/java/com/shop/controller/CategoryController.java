package com.shop.controller;

import com.shop.common.Result;
import com.shop.dto.CategoryDTO;
import com.shop.entity.Category;
import com.shop.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类
     */
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.findAll());
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/detail/{id}")
    public Result<Category> detail(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        if (category != null) {
            return Result.success(category);
        }
        return Result.error("分类不存在");
    }

    /**
     * 新增分类
     */
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody CategoryDTO dto) {
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        if (categoryService.add(category)) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 更新分类
     */
    @PutMapping("/update")
    public Result<?> update(@Valid @RequestBody CategoryDTO dto) {
        if (dto.getId() == null) {
            return Result.error("分类ID不能为空");
        }
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        if (categoryService.update(category)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (categoryService.delete(id)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
