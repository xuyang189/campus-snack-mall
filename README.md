# 校园零食在线购物系统

一个前后端分离的校园零食电商项目，包含**用户端购物商城**、**商家端店铺管理**、**管理端后台**三套界面，覆盖商品、购物车、订单、地址、分类、公告、统计等完整业务链路。

适合作为课程设计 / 毕业设计参考，也可作为 Vue 3 + Spring Boot 3 前后端分离项目的脚手架。

## 技术栈

| 层 | 技术 |
| --- | --- |
| 前端 | Vue 3.5 · Vite 8 · Vue Router 5 · Pinia 3 · Axios |
| 后端 | Java 17 · Spring Boot 3.2.6 · MyBatis-Plus 3.5.7 · JWT (jjwt 0.12) · BCrypt · Lombok |
| 数据库 | MySQL 8.0 |
| 构建 | Maven 3.8+ / npm 10+ |

## 功能一览

**用户端**
- 首页推荐、商品列表（分类 / 关键字 / 价格排序 / 分页）、商品详情
- 购物车增删改、勾选结算
- 收货地址管理、下单、订单列表与详情、取消订单
- 注册 / 登录、个人资料与密码修改

**商家端**
- 商品管理：新增、编辑、上下架、删除（**提交即上架**，无需等待审核）
- 店铺订单查看与状态流转
- 店铺数据统计（销量、订单趋势、热销商品）

**管理端**
- 用户管理、商品管理（可下架违规商品）、分类管理、公告管理
- 全站订单查看与状态调整
- 平台统计概览

## 目录结构

```
校园零食购物/
├── backend/                     # Spring Boot 后端
│   ├── src/main/java/com/campus/snackmall/
│   │   ├── common/              # 统一返回 Result、分页、异常、枚举
│   │   ├── config/              # MyBatis-Plus、WebMvc、JWT、上传配置
│   │   ├── security/            # JWT 工具、登录拦截器、权限注解
│   │   └── module/              # auth / user / product / cart / order
│   │                            # category / notice / merchant / admin / statistics
│   ├── src/main/resources/
│   │   ├── application.yml      # 主配置（端口、数据源、JWT、上传路径）
│   │   ├── application-dev.yml
│   │   └── application-prod.yml
│   ├── campus_snack_mall.sql    # 建库建表 + 演示数据（含 11 个商品）
│   ├── upload/products/         # 演示商品图
│   └── pom.xml
└── frontend/                    # Vue 3 前端
    ├── src/
    │   ├── api/                 # 按模块封装的接口
    │   ├── layouts/             # 四种布局：主站 / 登录 / 商家 / 管理
    │   ├── views/               # 页面（user / merchant / admin / auth / product）
    │   ├── stores/              # Pinia：登录态、购物车
    │   ├── router/
    │   └── utils/               # http、图片地址、格式化、存储
    ├── .env.development         # 开发环境接口地址
    └── vite.config.js           # 端口 5173 + /api、/upload 代理
```

## 快速开始

### 1. 准备数据库

```sql
-- 用 MySQL 8 执行
source backend/campus_snack_mall.sql
```

脚本会创建数据库 `campus_snack_mall`、8 张表，并写入演示数据（管理员、分类、公告、11 个商品）。

### 2. 启动后端

```bash
cd backend
# 数据库密码默认 123456，可用环境变量覆盖：DB_HOST / DB_USER / DB_PASSWORD / JWT_SECRET
mvn spring-boot:run
```

也可以直接用 IntelliJ IDEA 打开 `backend` 目录，运行 `CampusSnackMallApplication`。

启动后后端监听 **http://localhost:8080**，接口前缀 `/api`。

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

打开 **http://localhost:5173**。

> Windows PowerShell 如果提示「禁止运行脚本」，把 `npm` 换成 `npm.cmd` 即可；cmd 里则直接用 `npm`。

### 4. 默认账号

| 账号 | 密码 | 角色 | 入口 |
| --- | --- | --- | --- |
| `admin` | `123456` | 管理员 | 登录后进入 `/admin` |
| `1234` | — | 普通用户 | 用户端商城 |
| `123456` | — | 商家 | 登录后进入 `/merchant` |
| `xy206304` | — | 商家 | 同上 |

密码在数据库中以 BCrypt 存储。忘记密码时，可把任意账号重置为 `123456`：

```sql
UPDATE sys_user
SET password = '$2a$10$YrShwQU4TtKdac2zNQ8HyeeeuTulI4aXTVho7VtsUuB4R7Q6xj0Sy'
WHERE username = 'your_account';
```

## 接口约定

- 统一返回：`{ "code": 200, "message": "查询成功", "data": ... }`
- 认证方式：请求头 `Authorization: Bearer <token>`，JWT 载荷包含 `userId`、`role`
- 权限注解（自定义拦截器实现）：
  - `@RequireLogin` 需登录
  - `@RequireMerchant` 需商家
  - `@RequireAdmin` 需管理员
- 图片访问：上传文件存放在 `backend/upload/`，通过 `/upload/**` 静态映射访问

部分接口示例：

```
GET  /api/products                 商品列表（分类、关键字、排序、分页）
GET  /api/products/{id}            商品详情
POST /api/auth/login               登录
GET  /api/cart                     购物车
POST /api/orders                   创建订单
GET  /api/merchant/products        商家商品列表
POST /api/merchant/products        商家发布商品（提交即上架）
PUT  /api/admin/products/{id}/audit 管理员审核 / 下架
```

更完整的字段说明见 `backend/校园零食在线购物系统前后端联调接口文档_修正版.md`。

## 数据库表

| 表 | 说明 |
| --- | --- |
| `sys_user` | 用户（用户 / 商家 / 管理员） |
| `sys_user_address` | 收货地址 |
| `snack_category` | 商品分类 |
| `snack_product` | 商品（含上下架 `status`、审核 `audit_status`、软删除） |
| `snack_cart` | 购物车 |
| `snack_order` | 订单主表 |
| `snack_order_item` | 订单明细 |
| `sys_notice` | 公告 |

## 常见问题

**端口被占用**：后端 8080、前端 5173。可用 `netstat -ano | findstr :8080` 找到 PID 后结束进程。

**页面空白 / 接口 404**：确认后端已启动，且 `frontend/.env.development` 中的 `VITE_API_BASE_URL` 指向正确的后端地址。

**商品不显示**：商城只展示 `status = 1`（上架）**且** `audit_status = 1`（审核通过）**且** `is_deleted = 0` 的商品。

**图片不显示**：确认 `backend/upload/products/` 下的文件存在，且后端 `upload.path` 指向 `./upload/`。

## 说明

- 商品图片来自公开电商页面，仅用于本项目功能演示，正式使用请替换为自有素材。
- 项目为教学演示用途，默认密码、JWT 密钥均为示例值，**部署到公网前请务必修改**。

## License

[MIT](LICENSE) © 2026 徐阳