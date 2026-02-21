package com.shop.mapper;

import com.shop.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    /** 查询所有用户 */
    List<User> findAll();
    
    /** 根据ID查询用户 */
    User findById(@Param("id") Long id);
    
    /** 根据用户名查询用户 */
    User findByUsername(@Param("username") String username);
    
    /** 新增用户 */
    int insert(User user);
    
    /** 更新用户 */
    int update(User user);
    
    /** 删除用户 */
    int deleteById(@Param("id") Long id);
    
    /** 更新用户状态 */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /** 统计用户总数 */
    int count();
}
