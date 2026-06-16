# 屋顶菜园认领平台

一个面向社区居民、物业、园艺师和餐厨回收员的屋顶菜园认领管理平台，实现从菜畦认领、播种、日常照料、厨余入桶、堆肥成熟、回填菜畦到收成分配的完整业务闭环。

## 原始需求

> 请实现一个给社区居民、物业、园艺师和餐厨回收员共同使用的屋顶菜园认领平台，页面用 Vue3 呈现菜畦分布、日照时段、认领家庭、厨余桶、堆肥批次和采收记录，接口用 Spring Boot 保存菜畦状态、投放重量、巡园建议和收成交接。居民认领菜畦后登记种植品类、浇水安排、家庭厨余投放意愿和采收偏好；物业负责屋顶开放时间、防水巡查、工具柜钥匙和访客进入；园艺师记录土壤情况、虫害、补苗、施肥和堆肥回填；回收员把厨余称重、污染情况、发酵桶编号和成熟去向写入台账。整条业务要从认领、播种、日常照料、厨余入桶、堆肥成熟、回填菜畦到收成分配闭合起来，不能只做成一个预约表。

## 项目简介

本平台是一个多方协作的屋顶菜园管理系统，涵盖以下核心角色和功能：

### 角色说明

- **居民**：认领菜畦、登记种植信息、投放厨余、记录采收
- **物业**：管理认领审核、防水巡查、工具钥匙、访客登记、系统设置
- **园艺师**：土壤检测、虫害记录、巡园建议、补苗记录、堆肥管理
- **回收员**：厨余桶管理、投放台账、堆肥批次管理

### 业务闭环

```
认领菜畦 → 播种种植 → 日常照料（浇水/施肥/除虫） → 厨余投放 → 堆肥发酵 → 堆肥成熟 → 回填菜畦 → 采收分配
     ↑                                                                                                    ↓
     └────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

## 技术栈

### 后端
- Spring Boot 3.2.0
- Spring Data JPA
- H2 内存数据库
- Lombok
- Maven

### 前端
- Vue 3.4
- Vue Router 4
- Pinia 状态管理
- Element Plus UI 组件库
- Axios
- Vite

## 启动方式

### 前置要求

- JDK 17+
- Maven 3.6+
- Node.js 18+
- npm 或 pnpm

### 方式一：Docker 一键启动（推荐）

#### 1. 启动服务

在项目根目录执行：

```bash
docker compose up --build
```

如需后台运行：

```bash
docker compose up --build -d
```

#### 2. 访问地址

- 前端页面：http://localhost
- 后端 API：http://localhost:8080/api
- H2 控制台：http://localhost:8080/api/h2-console

#### 3. 停止服务

```bash
docker compose down
```

### 方式二：本地开发启动

#### 1. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080/api` 启动

#### 2. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端页面访问地址：`http://localhost:5173`

### 方式三：打包部署

#### 后端打包

```bash
cd backend
mvn clean package -DskipTests
java -jar target/garden-platform-1.0.0.jar
```

#### 前端打包

```bash
cd frontend
npm install
npm run build
```

打包产物在 `frontend/dist` 目录，可部署到 Nginx 等静态服务器。

## 功能模块

### 居民功能
- 菜畦分布图：查看所有菜畦状态、日照时段
- 我的认领：查看认领记录、种植信息
- 厨余投放：选择厨余桶、记录投放重量和污染情况
- 采收记录：记录采收量、分配方式

### 物业功能
- 认领管理：审核认领申请、管理认领状态
- 巡查记录：防水巡查、安全巡查、问题跟踪
- 工具钥匙：钥匙借出归还管理
- 访客管理：访客登记、签到签退
- 系统设置：开放时间、认领限制等配置

### 园艺师功能
- 土壤检测：记录土壤 PH、湿度、氮磷钾含量
- 虫害记录：记录虫害类型、严重程度、处理方法
- 巡园建议：发布种植、施肥、浇水等建议
- 补苗记录：记录死苗、补苗情况
- 堆肥批次：管理堆肥发酵全流程

### 回收员功能
- 厨余桶：查看各桶投放量和状态
- 堆肥批次：翻堆、标记成熟、回填菜畦
- 投放台账：查看所有投放记录、收集确认

## 数据说明

系统使用 H2 内存数据库，启动时自动初始化示例数据，包括：
- 6 个示例用户（3位居民、1位物业、1位园艺师、1位回收员）
- 9 块菜畦（3x3 布局）
- 3 个厨余桶
- 3 个堆肥批次
- 3 把工具钥匙
- 3 条系统设置
- 若干认领、种植、采收记录

### H2 控制台

访问：`http://localhost:8080/api/h2-console`
- JDBC URL: `jdbc:h2:mem:gardendb`
- 用户名: `sa`
- 密码: （空）

## 目录结构

```
wl-306/
├── backend/                    # Spring Boot 后端
│   ├── src/
│   │   └── main/
│   │       ├── java/com/rooftop/garden/
│   │       │   ├── controller/     # REST 控制器
│   │       │   ├── service/        # 业务服务
│   │       │   ├── repository/     # 数据访问
│   │       │   ├── entity/         # 实体类
│   │       │   └── config/         # 配置类
│   │       └── resources/
│   │           └── application.yml
│   ├── Dockerfile
│   ├── .dockerignore
│   └── pom.xml
├── frontend/                   # Vue3 前端
│   ├── src/
│   │   ├── views/               # 页面组件
│   │   ├── api/                 # API 接口
│   │   ├── store/               # 状态管理
│   │   ├── router/              # 路由配置
│   │   ├── utils/               # 工具函数
│   │   ├── assets/              # 静态资源
│   │   ├── App.vue
│   │   └── main.js
│   ├── Dockerfile
│   ├── .dockerignore
│   ├── nginx.conf
│   ├── vite.config.js
│   └── package.json
├── Dockerfile                   # 根目录 Dockerfile
├── docker-compose.yml           # Docker Compose 配置
├── .dockerignore
└── README.md
```

## API 接口概览

### 菜畦管理
- `GET /api/plots` - 获取所有菜畦
- `GET /api/plots/{id}` - 获取菜畦详情
- `POST /api/plots` - 新增菜畦
- `PATCH /api/plots/{id}/status` - 更新菜畦状态

### 认领管理
- `GET /api/claims` - 获取所有认领
- `POST /api/claims` - 提交认领申请
- `POST /api/claims/{id}/approve` - 通过认领
- `POST /api/claims/{id}/reject` - 拒绝认领

### 厨余管理
- `GET /api/compost/bins` - 获取所有厨余桶
- `POST /api/compost/deliveries` - 提交厨余投放
- `POST /api/compost/bins/{id}/empty` - 清空厨余桶

### 堆肥批次
- `GET /api/compost-batches` - 获取所有批次
- `POST /api/compost-batches` - 新建批次
- `POST /api/compost-batches/{id}/ferment` - 开始发酵
- `POST /api/compost-batches/{id}/turn` - 翻堆
- `POST /api/compost-batches/{id}/mature` - 标记成熟
- `POST /api/compost-batches/{id}/apply` - 回填菜畦

### 采收管理
- `GET /api/harvests` - 获取采收记录
- `POST /api/harvests` - 新增采收记录

### 物业管理
- `GET /api/property/inspections` - 获取巡查记录
- `GET /api/property/tool-keys` - 获取工具钥匙
- `GET /api/property/visitors` - 获取访客记录
- `GET /api/property/settings` - 获取系统设置

### 园艺师功能
- `GET /api/gardener/soil-reports` - 获取土壤报告
- `GET /api/gardener/pest-records` - 获取虫害记录
- `GET /api/gardener/suggestions` - 获取巡园建议
- `GET /api/gardener/seedling-records` - 获取补苗记录

### 厨余污染处理
- `GET /api/contamination` - 获取所有污染记录
- `GET /api/contamination/{id}` - 获取污染记录详情
- `GET /api/contamination/user/{userId}` - 获取指定用户的污染记录
- `GET /api/contamination/pending` - 获取待处理的污染记录
- `POST /api/contamination/report` - 回收员上报污染并隔离桶/批次
- `POST /api/contamination/{id}/assign-gardener` - 分配园艺师处理
- `POST /api/contamination/{id}/gardener-dispose` - 园艺师处理判定
- `POST /api/contamination/{id}/pause-points` - 物业暂停绿色积分
- `POST /api/contamination/{id}/recover-points` - 物业恢复绿色积分
- `POST /api/contamination/{id}/update-final-result` - 更新堆肥最终结果

## 注意事项

1. H2 数据库为内存数据库，重启后数据会丢失，如需持久化请修改配置
2. 系统内置角色切换功能，可在页面右上角切换不同角色体验功能
3. 默认用户信息：
   - 居民：resident1 / 张小明
   - 物业：property / 物业管理员
   - 园艺师：gardener / 园艺师老陈
   - 回收员：collector / 回收员老赵
4. 本系统为演示版本，未实现完整的用户认证和权限控制，生产环境请自行添加
5. 后端需要 JDK 17 或 JDK 21 编译运行，JDK 25 及以上版本可能与 Lombok 存在兼容性问题
6. 根目录 `Dockerfile` 仅用于构建前端静态页面，完整项目请使用 `docker-compose.yml` 启动
7. 推荐使用 Docker Compose 方式一键启动完整项目

## 开发说明

### 添加新实体
1. 在 `entity` 包创建实体类
2. 在 `repository` 包创建 Repository 接口
3. 在 `service` 包创建 Service 类
4. 在 `controller` 包创建 Controller 类

### 添加新页面
1. 在 `views` 目录创建 Vue 组件
2. 在 `router/index.js` 添加路由配置
3. 在 `App.vue` 的菜单中添加对应入口
4. 在 `api` 目录创建对应接口文件
