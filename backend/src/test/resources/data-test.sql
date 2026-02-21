-- 测试用户数据 (密码: 123456 的MD5)
INSERT INTO user (id, username, password, nickname, role, status) VALUES
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 'admin', 1),
(2, 'testuser', 'e10adc3949ba59abbe56e057f20f883e', '测试用户', 'user', 1),
(3, 'disabled', 'e10adc3949ba59abbe56e057f20f883e', '禁用用户', 'user', 0);

-- 测试分类数据
INSERT INTO category (id, name, description, sort, status) VALUES
(1, '手机', '智能手机', 1, 1),
(2, '电脑', '笔记本电脑', 2, 1);

-- 测试商品数据
INSERT INTO product (id, name, description, price, stock, category_id, status, sales) VALUES
(1, 'iPhone 15', '苹果手机', 6999.00, 100, 1, 1, 50),
(2, 'MacBook Pro', '苹果笔记本', 12999.00, 50, 2, 1, 30),
(3, '下架商品', '已下架', 999.00, 10, 1, 0, 0);
