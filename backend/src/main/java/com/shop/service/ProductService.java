package com.shop.service;

import com.shop.entity.Product;
import com.shop.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商品服务类
 */
@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 查询所有商品
     */
    public List<Product> findAll() {
        return productMapper.findAll();
    }

    /**
     * 查询上架商品(前台展示)
     */
    public List<Product> findOnSale() {
        return productMapper.findOnSale();
    }

    /**
     * 根据ID查询商品
     */
    public Product findById(Long id) {
        return productMapper.findById(id);
    }

    /**
     * 根据分类ID查询商品
     */
    public List<Product> findByCategoryId(Long categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }

    /**
     * 搜索商品
     */
    public List<Product> search(String keyword) {
        return productMapper.search(keyword);
    }

    /**
     * 新增商品
     */
    public boolean add(Product product) {
        return productMapper.insert(product) > 0;
    }

    /**
     * 更新商品
     */
    public boolean update(Product product) {
        return productMapper.update(product) > 0;
    }

    /**
     * 删除商品
     */
    public boolean delete(Long id) {
        return productMapper.deleteById(id) > 0;
    }

    /**
     * 统计商品总数
     */
    public int count() {
        return productMapper.count();
    }

    /**
     * 查询热销商品(含销售额)
     */
    public List<Map<String, Object>> findHotProducts(Integer limit) {
        return productMapper.findHotProducts(limit);
    }
}
