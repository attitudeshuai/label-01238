package com.shop.service;

import com.shop.entity.User;
import com.shop.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

/**
 * 用户服务类
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     */
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(user.getPassword())) {
            return null;
        }
        if (user.getStatus() == 0) {
            return null;
        }
        return user;
    }

    /**
     * 用户注册
     */
    public boolean register(User user) {
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            return false;
        }
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setRole("user");
        user.setStatus(1);
        return userMapper.insert(user) > 0;
    }

    /**
     * 查询所有用户
     */
    public List<User> findAll() {
        return userMapper.findAll();
    }

    /**
     * 根据ID查询用户
     */
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * 更新用户
     */
    public boolean update(User user) {
        return userMapper.update(user) > 0;
    }

    /**
     * 删除用户
     */
    public boolean delete(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    /**
     * 更新用户状态
     */
    public boolean updateStatus(Long id, Integer status) {
        return userMapper.updateStatus(id, status) > 0;
    }

    /**
     * 统计用户总数
     */
    public int count() {
        return userMapper.count();
    }
}
