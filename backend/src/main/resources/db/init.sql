-- 商业购物管理系统数据库初始化脚本
-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS shop_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shop_db;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(MD5加密)',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `avatar` VARCHAR(255) COMMENT '头像',
    `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色: admin-管理员, user-普通用户',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 分类表
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(255) COMMENT '分类描述',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 商品表
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `description` TEXT COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `stock` INT DEFAULT 0 COMMENT '库存数量',
    `image` LONGTEXT COMMENT '商品图片',
    `category_id` BIGINT COMMENT '分类ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    `sales` INT DEFAULT 0 COMMENT '销量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`category_id`) REFERENCES `category`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 购物车表
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `quantity` INT DEFAULT 1 COMMENT '数量',
    `selected` BOOLEAN DEFAULT TRUE COMMENT '是否选中',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- 订单表
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `status` TINYINT DEFAULT 0 COMMENT '订单状态: 0-待支付, 1-已支付, 2-已发货, 3-已完成, 4-已取消',
    `address` VARCHAR(255) COMMENT '收货地址',
    `receiver` VARCHAR(50) COMMENT '收货人',
    `phone` VARCHAR(20) COMMENT '收货人电话',
    `remark` VARCHAR(255) COMMENT '备注',
    `pay_time` DATETIME COMMENT '支付时间',
    `ship_time` DATETIME COMMENT '发货时间',
    `finish_time` DATETIME COMMENT '完成时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单项表
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单项ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT COMMENT '商品ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `product_image` LONGTEXT COMMENT '商品图片',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    `quantity` INT NOT NULL COMMENT '购买数量',
    `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    FOREIGN KEY (`order_id`) REFERENCES `order`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

-- 插入测试数据
-- 管理员账号: admin/admin123, 普通用户: user/user123
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `phone`, `role`, `status`) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', 'admin@shop.com', '13800000000', 'admin', 1),
('user', '6ad14ba9986e3615423dfca256d04e3f', '测试用户', 'user@shop.com', '13800000001', 'user', 1);

-- 插入分类数据
INSERT INTO `category` (`name`, `description`, `sort`, `status`) VALUES
('手机数码', '手机、平板、数码配件', 1, 1),
('电脑办公', '笔记本、台式机、办公设备', 2, 1),
('家用电器', '电视、冰箱、洗衣机、空调', 3, 1),
('服装鞋包', '男装、女装、鞋靴、箱包', 4, 1),
('食品生鲜', '零食、饮料、生鲜水果', 5, 1);

-- 插入商品数据
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `image`, `category_id`, `status`, `sales`) VALUES
('iPhone 15 Pro Max', '苹果最新旗舰手机，A17 Pro芯片，钛金属边框', 9999.00, 100, '/images/products/iphone15.png', 1, 1, 50),
('华为Mate 60 Pro', '华为旗舰手机，麒麟9000S芯片，卫星通话', 6999.00, 80, '/images/products/huawei-mate60.png', 1, 1, 80),
('小米14 Ultra', '小米影像旗舰，徕卡光学镜头，骁龙8 Gen3', 5999.00, 120, '/images/products/xiaomi14.png', 1, 1, 60),
('MacBook Pro 14', '苹果笔记本电脑，M3 Pro芯片，14英寸Liquid视网膜XDR显示屏', 14999.00, 50, '/images/products/macbook-pro.png', 2, 1, 30),
('联想ThinkPad X1 Carbon', '商务轻薄本，14英寸2.8K OLED屏，Intel酷睿Ultra', 12999.00, 40, '/images/products/thinkpad.png', 2, 1, 25),
('戴尔XPS 15', '高性能创作本，15.6英寸4K触控屏，RTX 4070', 15999.00, 30, '/images/products/dell-xps.png', 2, 1, 20),
('索尼65英寸OLED电视', '4K HDR OLED电视，XR认知芯片，杜比视界', 12999.00, 20, '/images/products/sony-tv.png', 3, 1, 15),
('海尔冰箱', '对开门冰箱，500L大容量，变频风冷无霜', 4999.00, 60, '/images/products/refrigerator.png', 3, 1, 40),
('美的空调', '1.5匹变频冷暖空调，一级能效，智能控制', 3299.00, 100, '/images/products/air-conditioner.png', 3, 1, 70),
('Nike Air Jordan 1', '经典篮球鞋，复古配色，舒适缓震', 1299.00, 200, '/images/products/nike-jordan.png', 4, 1, 150);

-- 插入测试订单
INSERT INTO `order` (`order_no`, `user_id`, `total_amount`, `status`, `address`, `receiver`, `phone`, `remark`, `pay_time`) VALUES
('20240101120000001', 2, 9999.00, 3, '北京市朝阳区xxx街道xxx号', '张三', '13800000001', '请尽快发货', '2024-01-01 12:30:00'),
('20240115150000002', 2, 6999.00, 2, '上海市浦东新区xxx路xxx号', '李四', '13800000002', '', '2024-01-15 15:30:00'),
('20240201100000003', 2, 14999.00, 1, '广州市天河区xxx大道xxx号', '王五', '13800000003', '发票抬头：个人', '2024-02-01 10:30:00');

-- 插入订单项
INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `product_image`, `price`, `quantity`, `subtotal`) VALUES
(1, 1, 'iPhone 15 Pro Max', '/images/products/iphone15.png', 9999.00, 1, 9999.00),
(2, 2, '华为Mate 60 Pro', '/images/products/huawei-mate60.png', 6999.00, 1, 6999.00),
(3, 4, 'MacBook Pro 14', '/images/products/macbook-pro.png', 14999.00, 1, 14999.00);
