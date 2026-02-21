package com.shop.mapper;

import com.shop.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper {
    /** 查询所有商品 */
    List<Product> findAll();
    
    /** 查询上架商品 */
    List<Product> findOnSale();
    
    /** 根据ID查询商品 */
    Product findById(@Param("id") Long id);
    
    /** 根据分类ID查询商品 */
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);
    
    /** 搜索商品 */
    List<Product> search(@Param("keyword") String keyword);
    
    /** 新增商品 */
    int insert(Product product);
    
    /** 更新商品 */
    int update(Product product);
    
    /** 删除商品 */
    int deleteById(@Param("id") Long id);
    
    /** 更新库存 */
    int updateStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /** 更新销量 */
    int updateSales(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /** 统计商品总数 */
    int count();
    
    /** 查询热销商品(含销售额) */
    List<Map<String, Object>> findHotProducts(@Param("limit") Integer limit);
}
