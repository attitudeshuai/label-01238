package com.shop.service;

import com.shop.entity.Category;
import com.shop.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务类
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 查询所有分类
     */
    public List<Category> findAll() {
        return categoryMapper.findAll();
    }

    /**
     * 根据ID查询分类
     */
    public Category findById(Long id) {
        return categoryMapper.findById(id);
    }

    /**
     * 新增分类
     */
    public boolean add(Category category) {
        return categoryMapper.insert(category) > 0;
    }

    /**
     * 更新分类
     */
    public boolean update(Category category) {
        return categoryMapper.update(category) > 0;
    }

    /**
     * 删除分类
     */
    public boolean delete(Long id) {
        return categoryMapper.deleteById(id) > 0;
    }
}
