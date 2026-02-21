# 商业购物管理系统

基于 SSM + SpringBoot + SpringAI + Vue + ElementUI 的全栈电商管理系统。

## How to Run

### Docker启动（推荐）

```bash
# 克隆项目
git clone <repository-url>
cd shop-management-system

# 启动所有服务
docker-compose up --build -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

启动后访问：
- 前端：http://localhost:8081
- 后端API：http://localhost:8238
- MySQL：localhost:3307

### 本地启动

#### 后端
```bash
cd backend

# 确保MySQL已启动并创建数据库
mysql -u root -p < src/main/resources/db/init.sql

# 启动后端
mvn spring-boot:run
```

#### 前端
```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 生产环境配置

创建 `.env` 文件配置敏感信息：

```bash
MYSQL_ROOT_PASSWORD=your_secure_password_here
JWT_SECRET=your_secure_random_jwt_secret_at_least_32_chars
OPENAI_API_KEY=your_openai_api_key
```

然后启动：
```bash
docker-compose --env-file .env up --build -d
```

## Services

| 服务 | 端口 | 说明 |
|------|------|------|
| Frontend | 8081 | Vue + ElementUI 前端 |
| Backend | 8238 | Spring Boot 后端 |
| MySQL | 3307 | 数据库 |

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | user | user123 |

## 题目内容

### 项目简介
帮我编写一个Java项目，项目为 商业购物管理系统 ，整合 SSM（Spring+SpringMVC+MyBatis）、SpringBoot、SpringAI，基于 MySQL 构建，前端采用 Vue+ElementUI。核心功能包括：商品管理、订单管理、用户管理、购物车、AI 智能客服（基于 SpringAI）、订单数据分析等，覆盖电商全流程管理。

### 核心功能
1. **商品管理** - 商品的增删改查、分类管理、库存管理
2. **订单管理** - 订单创建、状态流转、订单查询
3. **用户管理** - 用户注册登录、权限控制、用户信息管理
4. **购物车** - 添加商品、数量修改、结算功能
5. **AI智能客服** - 基于 SpringAI 的智能问答系统
6. **数据分析** - 订单统计、销售趋势、热销商品分析

### 技术栈
- **后端**: Spring Boot 3.2 + MyBatis + MySQL 8.0 + Spring AI
- **前端**: Vue 3 + Vite + Element Plus + ECharts + Pinia
- **部署**: Docker + Docker Compose + Nginx

### 项目结构
```
├── backend/                 # 后端项目
│   ├── src/main/java/      # Java源码
│   ├── src/main/resources/ # 配置文件
│   ├── Dockerfile          # 后端Docker配置
│   └── pom.xml             # Maven配置
├── frontend/               # 前端项目
│   ├── src/                # Vue源码
│   ├── Dockerfile          # 前端Docker配置
│   └── package.json        # npm配置
├── docker-compose.yml      # Docker编排配置
└── README.md               # 项目说明
```

## API 接口文档

Base URL: `http://localhost:8238/api`

### 认证相关

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| POST | `/auth/login` | 用户登录 | `{username, password}` |
| POST | `/auth/register` | 用户注册 | `{username, password, nickname, email, phone}` |
| GET | `/auth/info` | 获取当前用户信息 | Header: `Authorization: Bearer <token>` |

### 商品管理

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/product/list` | 获取上架商品列表 | - |
| GET | `/product/all` | 获取所有商品(管理员) | - |
| GET | `/product/detail/{id}` | 获取商品详情 | - |
| GET | `/product/category/{categoryId}` | 按分类获取商品 | - |
| GET | `/product/search` | 搜索商品 | `?keyword=xxx` |
| GET | `/product/hot` | 获取热销商品 | `?limit=10` |
| POST | `/product/add` | 新增商品 | `{name, categoryId, price, stock, image, description, status}` |
| PUT | `/product/update` | 更新商品 | `{id, name, categoryId, price, stock, image, description, status}` |
| DELETE | `/product/delete/{id}` | 删除商品 | - |

### 分类管理

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/category/list` | 获取分类列表 | - |
| POST | `/category/add` | 新增分类 | `{name, description, sort, status}` |
| PUT | `/category/update` | 更新分类 | `{id, name, description, sort, status}` |
| DELETE | `/category/delete/{id}` | 删除分类 | - |

### 购物车

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/cart/list` | 获取购物车列表 | - |
| POST | `/cart/add` | 添加到购物车 | `{productId, quantity}` |
| PUT | `/cart/update/{id}` | 更新数量 | `{quantity}` |
| PUT | `/cart/select/{id}` | 更新选中状态 | `{selected}` |
| PUT | `/cart/selectAll` | 全选/取消全选 | `{selected}` |
| DELETE | `/cart/delete/{id}` | 删除购物车项 | - |
| DELETE | `/cart/clear` | 清空购物车 | - |

### 订单管理

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/order/list` | 获取当前用户订单 | - |
| GET | `/order/all` | 获取所有订单(管理员) | - |
| GET | `/order/detail/{id}` | 获取订单详情 | - |
| POST | `/order/create` | 创建订单 | `{address, receiver, phone, remark}` |
| PUT | `/order/status/{id}` | 更新订单状态 | `{status}` (0待支付/1已支付/2已发货/3已完成/4已取消) |
| DELETE | `/order/delete/{id}` | 删除订单 | - |

### 用户管理

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/user/list` | 获取用户列表(管理员) | - |
| GET | `/user/detail/{id}` | 获取用户详情 | - |
| POST | `/user/add` | 新增用户 | `{username, password, nickname, email, phone}` |
| PUT | `/user/update` | 更新用户 | `{id, nickname, email, phone, role}` |
| PUT | `/user/status/{id}` | 更新用户状态 | `{status}` (0禁用/1启用) |
| DELETE | `/user/delete/{id}` | 删除用户 | - |

### 数据统计

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/statistics/overview` | 获取概览数据 | - |
| GET | `/statistics/orderStatus` | 订单状态统计 | - |
| GET | `/statistics/recentOrders` | 近期订单统计 | `?days=7` |
| GET | `/statistics/monthlySales` | 月度销售统计 | `?months=6` |
| GET | `/statistics/hotProducts` | 热销商品排行 | `?limit=10` |

### AI 客服

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| POST | `/ai/chat` | AI对话 | `{message}` |

### 响应格式

所有接口统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 500 | 服务器错误 |
