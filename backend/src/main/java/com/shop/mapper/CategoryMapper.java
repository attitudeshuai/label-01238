package com.shop.mapper;

import com.shop.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 分类Mapper接口
 */
@Mapper
public interface CategoryMapper {
    /** 查询所有分类 */
    List<Category> findAll();
    
    /** 根据ID查询分类 */
    Category findById(@Param("id") Long id);
    
    /** 新增分类 */
    int insert(Category category);
    
    /** 更新分类 */
    int update(Category category);
    
    /** 删除分类 */
    int deleteById(@Param("id") Long id);
}
