package com.shop.mapper;

import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper {
    /** 查询所有订单 */
    List<Order> findAll();
    
    /** 根据ID查询订单 */
    Order findById(@Param("id") Long id);
    
    /** 根据订单号查询订单 */
    Order findByOrderNo(@Param("orderNo") String orderNo);
    
    /** 根据用户ID查询订单 */
    List<Order> findByUserId(@Param("userId") Long userId);
    
    /** 新增订单 */
    int insert(Order order);
    
    /** 新增订单项 */
    int insertOrderItem(OrderItem item);
    
    /** 批量新增订单项 */
    int batchInsertOrderItems(@Param("items") List<OrderItem> items);
    
    /** 更新订单状态 */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /** 删除订单 */
    int deleteById(@Param("id") Long id);
    
    /** 查询订单项 */
    List<OrderItem> findOrderItems(@Param("orderId") Long orderId);
    
    /** 统计订单总数 */
    int count();
    
    /** 统计各状态订单数量 */
    List<Map<String, Object>> countByStatus();
    
    /** 统计销售额 */
    Map<String, Object> sumTotalAmount();
    
    /** 按日期统计订单 */
    List<Map<String, Object>> countByDate(@Param("days") Integer days);
    
    /** 按月统计销售额 */
    List<Map<String, Object>> sumAmountByMonth(@Param("months") Integer months);
}
