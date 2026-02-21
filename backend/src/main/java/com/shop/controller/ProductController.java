package com.shop.controller;

import com.shop.common.Result;
import com.shop.dto.ProductDTO;
import com.shop.entity.Product;
import com.shop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取上架商品(前台展示)
     */
    @GetMapping("/list")
    public Result<List<Product>> list() {
        return Result.success(productService.findOnSale());
    }

    /**
     * 获取所有商品(后台管理)
     */
    @GetMapping("/all")
    public Result<List<Product>> all() {
        return Result.success(productService.findAll());
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/detail/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Product product = productService.findById(id);
        if (product != null) {
            return Result.success(product);
        }
        return Result.error("商品不存在");
    }

    /**
     * 根据分类获取商品
     */
    @GetMapping("/category/{categoryId}")
    public Result<List<Product>> listByCategory(@PathVariable Long categoryId) {
        return Result.success(productService.findByCategoryId(categoryId));
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public Result<List<Product>> search(@RequestParam String keyword) {
        return Result.success(productService.search(keyword));
    }

    /**
     * 新增商品
     */
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody ProductDTO dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        if (productService.add(product)) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 更新商品
     */
    @PutMapping("/update")
    public Result<?> update(@Valid @RequestBody ProductDTO dto) {
        if (dto.getId() == null) {
            return Result.error("商品ID不能为空");
        }
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        if (productService.update(product)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (productService.delete(id)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 获取热销商品
     */
    @GetMapping("/hot")
    public Result<?> hot(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(productService.findHotProducts(limit));
    }
}
