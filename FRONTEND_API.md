# 校园食堂外卖系统：前端接口交接

## 访问规则

- 后端基础地址：`http://localhost:8080/api`
- 所有请求体和响应体使用 JSON；查询条件使用 URL 参数。登录、注册也使用 JSON。
- 登录后端使用 Session。前端 `fetch` 必须传 `credentials: "include"`；Axios 必须设 `withCredentials: true`。
- 所有成功响应统一为：

```json
{
  "code": 200,
  "message": "成功",
  "data": {}
}
```

- 失败时 HTTP 状态码和 `code` 一致，例如 401、403、400、404。
- 旧的 `/user/login`、`/order/list` 等地址已停用，必须使用本文件的 `/api/...` 地址。

## 公开接口

| 方法 | 地址 | 说明 |
|---|---|---|
| GET | `/canteens` | 食堂列表 |
| GET | `/canteens/{canteenId}/stalls` | 某食堂的营业档口；每项含 `dishCount` |
| GET | `/dishes?keyword=&page=&pageSize=` | 菜品浏览、关键词搜索、分页 |
| GET | `/dishes/{id}` | 菜品详情 |
| GET | `/stalls/{stallId}/dishes` | 某档口已上架菜品 |
| GET | `/stalls/{stallId}/dish-categories` | 某档口菜品分类 |
| GET | `/dish-categories/{categoryId}/dishes` | 某分类已上架菜品 |
| GET | `/reviews` | 公开评价列表 |

## 认证与个人资料

| 方法 | 地址 | 请求体 |
|---|---|---|
| POST | `/auth/register` | `username`、`password`、`realName`、`phone`、`roleCode` |
| POST | `/auth/login` | `username`、`password` |
| POST | `/auth/logout` | 无 |
| GET | `/users/me` | 无 |
| PUT | `/users/me` | 可修改的用户资料字段 |
| PUT | `/users/me/password` | `oldPassword`、`newPassword` |

登录示例：

```json
{
  "username": "address_student_0922",
  "password": "Test12345"
}
```

## 学生端接口

| 方法 | 地址 | 请求体或参数 |
|---|---|---|
| GET | `/cart/items` | 无 |
| POST | `/cart/items` | `dishId`、`quantity` |
| PUT | `/cart/items/{dishId}` | `quantity` |
| DELETE | `/cart/items/{dishId}` | 无 |
| GET | `/addresses` | 无 |
| POST | `/addresses` | `contactName`、`phone`、`detail`、`isDefault` |
| PUT | `/addresses/{id}` | 地址字段 |
| DELETE | `/addresses/{id}` | 无 |
| POST | `/orders` | `itemIds`、`pickupType`、`addressId`、`remark` |
| GET | `/orders?status=` | 当前学生订单列表；`status` 可不传 |
| GET | `/orders/{orderId}` | 订单详情、订单项、地址、状态记录 |
| POST | `/orders/{orderId}/pay` | `paymentMethod: "MOCK"` |
| POST | `/orders/{orderId}/cancel` | 无 |
| POST | `/orders/{orderId}/confirm-pickup` | 到店自取确认 |
| POST | `/orders/{orderId}/confirm-receipt` | 配送收货确认 |
| POST | `/reviews` | `orderId`、`rating`、`content` |
| GET | `/student/reviews` | 当前学生的评价 |

创建订单示例：

```json
{
  "itemIds": [7, 8],
  "pickupType": "PICKUP",
  "addressId": null,
  "remark": "少放辣"
}
```

`pickupType` 只能是 `PICKUP` 或 `DELIVERY`。`DELIVERY` 时必须提供当前学生自己的 `addressId`。

## 商家端接口

| 方法 | 地址 | 请求体或参数 |
|---|---|---|
| GET | `/merchant/stall` | 当前商家档口资料 |
| PUT | `/merchant/stall` | `name`、`businessHours` |
| PUT | `/merchant/stall/status?status=0/1` | `0` 休息，`1` 营业 |
| GET | `/merchant/dish-categories` | 当前商家分类 |
| POST | `/merchant/dish-categories` | `name` |
| PUT | `/merchant/dish-categories/{id}` | `name` |
| DELETE | `/merchant/dish-categories/{id}` | 无 |
| GET | `/merchant/dishes` | 当前商家全部菜品 |
| POST | `/merchant/dishes` | 菜品字段 |
| PUT | `/merchant/dishes/{id}` | 菜品字段 |
| PUT | `/merchant/dishes/{id}/status?status=0/1` | 下架或上架 |
| GET | `/merchant/orders?status=` | 当前商家订单；`status` 可不传 |
| GET | `/merchant/orders/{orderId}` | 商家可见的订单详情 |
| PUT | `/merchant/orders/{id}/status?status=...` | 更新订单状态 |
| GET | `/merchant/dashboard/summary` | 今日订单、营业额、待处理订单和最近订单 |
| GET | `/merchant/dashboard/dish-sales` | 菜品销量与销售额 |
| GET | `/merchant/reviews` | 当前商家档口评价 |

菜品新增或修改时使用 JSON，例如：

```json
{
  "categoryId": 2,
  "name": "鸡腿盖饭",
  "price": 15.8,
  "stock": 30,
  "description": "含卤鸡腿和配菜",
  "imageUrl": null
}
```

## 状态值

| 字段 | 可用值 |
|---|---|
| 菜品、档口状态 | `1` 上架或营业；`0` 下架或休息 |
| 下单方式 | `PICKUP`、`DELIVERY` |
| 支付方式 | `MOCK` |
| 订单状态 | `WAIT_PAY`、`WAIT_ACCEPT`、`MAKING`、`DELIVERING`、`DELIVERED`、`COMPLETED`、`CANCELLED` |

## 前端同学独立启动后端的流程

前端同学不需要你的 MySQL 账号和密码，但可以在自己的电脑独立运行后端。

1. 克隆仓库：

   ```bash
   git clone https://github.com/kun079812/campus-food-system.git
   cd campus-food-system
   git checkout B-system
   ```

2. 启动自己的 MySQL 服务，然后创建空数据库：

   ```sql
   CREATE DATABASE campus_food_order DEFAULT CHARACTER SET utf8mb4;
   ```

3. 在 MySQL Workbench 中选择 `campus_food_order` 数据库，打开并执行项目内的 `sql/campus_food_order.sql`。该文件包含表结构和课程项目测试数据。

4. 将 `backend/src/main/resources/application-example.yaml` 复制为同目录下的 `application-local.yaml`，只填写自己电脑 MySQL 的密码：

   ```yaml
   spring:
     datasource:
       password: 自己的MySQL密码
   ```

5. 用 IntelliJ 打开 `backend` 模块，运行 `CampusFoodBackendApplication`。启动成功后的基础地址是：

   ```text
   http://localhost:8080/api
   ```

6. 前端请求必须携带 Session Cookie：`fetch` 使用 `credentials: "include"`，Axios 使用 `withCredentials: true`。

`application-local.yaml` 只在本机使用，已经被 Git 忽略，因此不会泄露任何人的数据库密码。

## 前端只调试页面时需要什么

如果后端已经由其他人启动，前端同学只需要本文件、后端地址、学生和商家测试账号，以及 Session Cookie 请求配置；不需要安装或配置数据库。