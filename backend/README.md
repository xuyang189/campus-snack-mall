# campus-snack-mall

基于 `Spring Boot 3 + MyBatis Plus + JWT` 的校园零食商城后端骨架工程。

## 已完成

- Maven 工程结构与基础配置
- 统一返回结构与全局异常处理
- JWT 鉴权与管理员权限拦截
- 按开发文档拆分的实体、Mapper、DTO、VO、Service、Controller
- 用户、地址、公告、分类、商品、购物车、订单、上传、后台管理、统计接口骨架
- 数据库初始化脚本 `campus_snack_mall.sql`

## 运行前准备

1. 创建 MySQL 数据库并执行 [campus_snack_mall.sql](../backend/campus_snack_mall.sql)
2. 按需设置环境变量：
   - `DB_HOST`
   - `DB_USER`
   - `DB_PASSWORD`
   - `JWT_SECRET`
   - `UPLOAD_PATH`
3. 默认端口：`8080`

## 启动

```bash
mvn spring-boot:run
```

## 说明

- 默认管理员账号：`admin`
- 默认管理员密码哈希已写入脚本，需要你按实际需求确认明文密码后再登录测试
- 当前版本以“框架完整搭建”为目标，复杂业务细节仍建议继续补单元测试、并发下单保护、更多 SQL 优化和接口联调验证
