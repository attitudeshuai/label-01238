# 商业购物管理系统前端 Dockerfile
# 多阶段构建，支持 ARM 和 X86 架构

# 第一阶段：构建
FROM --platform=$BUILDPLATFORM node:20-alpine AS builder

WORKDIR /app

# 复制package.json并安装依赖
COPY package*.json ./
RUN npm install

# 复制源代码并构建
COPY . .
RUN npm run build

# 第二阶段：运行
FROM nginx:alpine

# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制nginx配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]
