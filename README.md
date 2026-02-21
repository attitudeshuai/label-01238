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
商业购物管理系统是一个完整的电商解决方案，整合了 SSM（Spring+SpringMVC+MyBatis）、SpringBoot、SpringAI 技术栈，基于 MySQL 构建数据存储，前端采用 Vue3 + ElementUI Plus 实现现代化界面。

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
