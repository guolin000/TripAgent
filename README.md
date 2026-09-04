# AI 旅游助手项目

这是一个基于 Spring Boot、Spring AI 和 Vue 的 AI 应用项目，包含 `AI 旅游助手` 和 `AI 超级智能体` 两个核心能力。项目支持多轮对话、RAG 知识库问答、工具调用、MCP 服务调用、SSE 流式响应，并提供前后端 Docker 部署配置。

## 项目介绍

`AI 旅游助手` 面向旅行规划场景，可以根据用户的目的地、预算、出行偏好、交通住宿需求等信息，提供行程建议、路线规划、景点推荐和旅行问答。系统支持基于本地旅游知识文档的 RAG 检索，也可以调用地图、搜索、网页抓取等工具补充实时信息。

`AI 超级智能体 YuManus` 基于 ReAct 模式，可以根据用户目标进行自主推理和行动，结合联网搜索、资源下载、文件操作和 PDF 生成等工具，完成更复杂的旅行规划任务。

## 功能特性

- AI 旅游助手：支持旅行规划问答、多轮对话、SSE 流式输出。
- RAG 知识库：基于旅游相关文档进行检索增强生成。
- AI 超级智能体：支持自主规划、工具调用和任务执行。
- 工具调用：包含联网搜索、网页抓取、资源下载、文件操作、终端操作、PDF 生成等能力。
- MCP 服务：支持接入地图等外部 MCP 服务。
- 前后端分离：后端提供 REST/SSE 接口，前端提供 Vue 页面。
- Docker 部署：提供后端、前端镜像和 `docker-compose.yml` 一键编排。

## 技术栈

后端：

- Java 21
- Spring Boot 3
- Spring AI
- LangChain4j
- PGvector
- MCP Client
- Knife4j / OpenAPI
- Jsoup、iText、Kryo、Hutool

前端：

- Vue 3
- Vue Router
- Axios
- Vite
- Nginx

部署：

- Docker
- Docker Compose

## 项目结构

```text
.
├── Dockerfile                         # 后端 Dockerfile
├── docker-compose.yml                 # 前后端容器编排
├── .env.example                       # 服务器环境变量模板
├── DOCKER_DEPLOY.md                   # Docker 部署说明
├── pom.xml                            # 后端 Maven 配置
├── src                                # 后端源码
│   └── main
│       ├── java/com/yupi/yuaiagent
│       └── resources
│           ├── application.yml
│           ├── application-prod.yml
│           └── document               # RAG 文档
├── yu-ai-agent-frontend               # 前端项目
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── src
└── yu-image-search-mcp-server         # 图片搜索 MCP 服务
```

## 配置说明

敏感信息不要写入 `application.yml`，应通过环境变量、本地私有配置或服务器 `.env` 注入。

需要配置的核心环境变量：

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://your-postgres-host:5432/yu_ai_agent
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_AI_DASHSCOPE_API_KEY=your_dashscope_api_key
AMAP_MCP_API_KEY=your_amap_mcp_api_key
SEARCH_API_API_KEY=your_search_api_key
```

本地开发建议使用：

```text
src/main/resources/application-local.yml
```

该文件已被 `.gitignore` 忽略，可以放本地数据库和 API Key。

示例：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/yu_ai_agent
    username: your_username
    password: your_password
  ai:
    dashscope:
      api-key: your_dashscope_api_key
    mcp:
      client:
        streamable-http:
          connections:
            amap:
              endpoint: /mcp?key=your_amap_mcp_api_key

search-api:
  api-key: your_search_api_key
```

## 本地运行

### 后端

确保已安装 JDK 21。

Windows PowerShell：

```powershell
.\mvnw.cmd spring-boot:run
```

macOS / Linux：

```bash
./mvnw spring-boot:run
```

后端默认地址：

```text
http://localhost:8123/api
```

接口文档地址：

```text
http://localhost:8123/api/swagger-ui.html
```

### 前端

进入前端目录：

```bash
cd yu-ai-agent-frontend
npm install
npm run dev
```

前端开发环境默认会请求：

```text
http://localhost:8123/api
```

## Docker 部署

服务器项目根目录复制环境变量模板：

```bash
cp .env.example .env
```

编辑 `.env`，填写生产环境数据库和 API Key。

构建并启动：

```bash
docker compose up -d --build
```

查看服务状态：

```bash
docker compose ps
```

查看日志：

```bash
docker compose logs -f backend
docker compose logs -f frontend
```

默认访问地址：

```text
http://服务器IP/
```

如果需要修改公网访问端口，在 `.env` 中设置：

```env
FRONTEND_PORT=8080
```

然后访问：

```text
http://服务器IP:8080/
```

更多 Docker 部署细节见：

```text
DOCKER_DEPLOY.md
```

## 安全建议

- 不要把真实数据库密码、API Key 写入 `application.yml`。
- 不要提交 `.env`、`application-local.yml` 等私有配置文件。
- 如果密钥曾经被提交到 Git 或上传到公开仓库，应立即重置对应密钥。
- 生产环境只需要开放前端端口，后端 `8123` 端口可以只在 Docker 内部网络访问。
- 定期查看服务日志，避免将敏感请求参数打印到日志中。

## 常用命令

重新构建并启动：

```bash
docker compose up -d --build
```

停止服务：

```bash
docker compose down
```

重启后端：

```bash
docker compose restart backend
```

重启前端：

```bash
docker compose restart frontend
```
