# Docker 部署说明

本项目包含两个镜像：

- `backend`：Spring Boot 后端，容器内端口 `8123`，接口前缀 `/api`
- `frontend`：Vue 前端，由 Nginx 托管，容器内端口 `80`，并把 `/api/` 反向代理到后端容器

## 1. 准备环境变量

在服务器项目根目录复制环境变量模板：

```bash
cp .env.example .env
```

编辑 `.env`，填写生产环境数据库和 API Key：

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://your-postgres-host:5432/yu_ai_agent
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_AI_DASHSCOPE_API_KEY=your_dashscope_api_key
AMAP_MCP_API_KEY=your_amap_mcp_api_key
SEARCH_API_API_KEY=your_search_api_key
```

如需修改公网访问端口，调整：

```bash
FRONTEND_PORT=80
```

## 2. 构建并启动

```bash
docker compose up -d --build
```

访问：

```text
http://你的服务器IP/
```

## 3. 查看日志

```bash
docker compose logs -f backend
docker compose logs -f frontend
```

## 4. 停止服务

```bash
docker compose down
```

后端运行时生成的临时文件挂载在 Docker volume `backend-tmp`，普通 `docker compose down` 不会删除该数据。
