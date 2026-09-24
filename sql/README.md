# 数据库导入说明

本目录的 `campus_food_order.sql` 包含 `campus_food_order` 的表结构和课程项目测试数据。

```sql
CREATE DATABASE campus_food_order DEFAULT CHARACTER SET utf8mb4;
USE campus_food_order;
SOURCE campus_food_order.sql;
```

导入完成后，复制 `backend/src/main/resources/application-example.yaml` 为
`backend/src/main/resources/application-local.yaml`，填写自己本机的 MySQL 密码，随后启动后端。

`application-local.yaml` 已被 Git 忽略，不能提交个人数据库密码。