# 食堂小站：先看这份启动说明

这是按参考图实现的 Vue 3 点餐网站。包含学生登录、点餐首页、个人中心和商家中心，使用浅蓝、奶油白、粉色和校园水彩插画。

## 1. 现在使用哪两个项目？

前端（Vue）：

```text
E:\university\project_term_four\homework\campus-food-order-web
```

后端（你指定的 GitHub 仓库）：

```text
E:\university\project_term_four\homework\campus-food-system-backend-B\backend
```

实际分支名称是 **B-system**，不是 B。下载时提交为 `9ac50cbb56409d58bb0c99b774b00a50f6f73855`。

来源：https://github.com/kun079812/campus-food-system/tree/B-system

原来的 `daily-plan` 后端仍然保留。运行新页面时，应启动上面的 B-system 后端；两个后端都默认占用 8080，不能同时使用同一个端口。

## 2. 在你的电脑上启动

先确认 Windows 中的 MySQL80 服务正在运行。

打开第一个 PowerShell 窗口，启动后端：

```powershell
cd E:\university\project_term_four\homework\campus-food-system-backend-B\backend
.\start-local.ps1
```

出现 `Started CampusFoodBackendApplication`，表示后端已启动。这个窗口先不要关闭。

`start-local.ps1` 会读取你原来 `daily-plan` 配置里的数据库用户名和密码，并只在当前进程中使用；没有把密码写入前端。它启动的是已打包好的后端 JAR。如果以后修改了 Java 代码，请先在 IDEA 的 Maven 窗口重新执行 `package`，再启动。

打开第二个 PowerShell 窗口，启动前端：

```powershell
cd E:\university\project_term_four\homework\campus-food-order-web
npm run dev
```

浏览器访问：

```text
http://127.0.0.1:5173/
```

以终端显示的地址为准。如果 5173 被其他程序占用，Vite 会显示下一个可用端口。前端通过 `/api` 把请求转到 `http://localhost:8080`。

停止时，在对应窗口按 `Ctrl+C`。以后重新打开电脑，重复上述两组启动步骤即可。

## 3. 可以直接使用的真实测试账号

以下账号来自 B-system 的测试数据库，已验证可以登录。请在登录页选择对应身份。

| 身份 | 用户名 | 密码 |
| --- | --- | --- |
| 学生 | `address_student_0922` | `Test12345` |
| 商家 | `merchant_test` | `12345678` |

学生也可以点击“立即注册”，创建自己的账号。商家新注册后仍需由平台管理人员在数据库中关联 `stall.merchant_id`；B-system 没有提供自动创建档口的注册接口，因此首次体验商家功能请使用上面的已有商家账号。

## 4. 数据保存在哪里？

这次创建了一个独立的 MySQL 数据库：

```text
campus_food_web_demo_20261002
```

它导入了 B-system 的表结构和课程测试数据，并包含联调时新建的测试用户、菜品、地址、订单和评价。

你原有的 `campus_food_order` 数据库没有被覆盖。原数据库当时只有早期的 5 张表，不足以运行完整订单流程。不要直接向原数据库执行仓库中的导入脚本，因为该脚本含有删除旧表的语句。

若以后切换到已经准备好的其他数据库：

```powershell
.\start-local.ps1 -Database 你准备好的数据库名
```

修改后端端口时，先把前端 `.env.example` 复制为 `.env.local`，修改 `VITE_BACKEND_URL`，然后重启前端。

## 5. 怎样体验完整流程？

1. 用学生账号登录，选择菜品，点击“＋”加入购物车。
2. 点击右上角购物车，进入结算。一次只能购买同一档口的菜品，这是后端的规则。
3. 选择食堂自取或配送；配送时先添加收餐地址。
4. 创建订单后，去“我的订单”点击“模拟支付”。不会扣除真实资金。
5. 退出学生账号，用商家账号登录。选择自己档口的订单，点击“接单并制作”。
6. 自取订单点击“通知取餐”；配送订单点击“开始配送”，之后点击“确认送达”。
7. 切回学生账号，确认取餐或收餐，完成后可以评价。

商家可以在“菜品管理”中新增分类、发布菜品、修改名称/价格/库存/图片链接、上下架菜品；“店铺设置”中可以修改营业信息和开关店铺；“数据统计”使用后端返回的实际统计。

## 6. 不启动后端也能看页面吗？

可以。登录页有“体验学生端”和“体验商家端”。

这两个入口使用当前浏览器的示例数据，页面顶部会明确标注“页面体验”。同一浏览器中切换体验身份，可以体验双方订单流转；刷新也会保留示例数据。数据保存在浏览器 localStorage 中，不写入 MySQL。

真实登录失败时不会自动切换成示例数据。要退出体验，请点击顶部“登录真实账号”，或账号菜单中的“退出登录”。

收藏保存在当前浏览器，按账号隔离；后端暂无收藏和优惠券接口，因此这里没有实现可兑换优惠券或云端收藏。

## 7. 从哪个文件开始看代码？

| 文件 | 作用 |
| --- | --- |
| `src/main.js` | 启动 Vue，现在加载新版页面 |
| `src/CampusApp.vue` | 网站总入口、顶部导航、整体布局 |
| `src/campus.css` | 页面颜色、排版、卡片和手机适配 |
| `src/views/AuthView.vue` | 学生/商家登录、注册、体验入口 |
| `src/views/StudentHome.vue` | 首页、分类筛选、搜索、菜品列表 |
| `src/views/UserCenter.vue` | 个人中心、地址、收藏、账号设置 |
| `src/views/MerchantCenter.vue` | 商家首页、菜品、订单、统计、店铺设置 |
| `src/components/AppDialogs.vue` | 购物车、结算、菜品编辑等弹窗 |
| `src/components/OrderList.vue` | 双方共用的订单列表和订单操作 |
| `src/components/DishCard.vue` | 单个菜品卡片 |
| `src/components/FoodArt.vue` | 菜品插画，图片缺失时也能显示 |
| `src/components/UiIcon.vue` | 页面图标 |
| `src/services/campusApi.js` | 向 B-system 发请求，携带登录 Cookie，读取统一返回结构 |
| `src/services/useCampus.js` | 登录、购物车、订单等页面行为和数据 |
| `src/services/demoData.js` | 明确进入体验模式后使用的示例数据 |
| `public/images/campus-spring.png` | 校园水彩背景图 |
| `vite.config.js` | Vue 编译插件和后端请求转发配置 |

原来的 `App.vue`、`App_practice.vue`、`App_practice_simple.vue` 和练习 CSS 都保留了。现在打开网站看到的是 `CampusApp.vue`。继续学习旧练习时，可以把 `src/main.js` 的页面与样式导入改回 `App_practice_simple.vue`、`style_practice_simple.css`。

## 8. 验证与构建

```powershell
npm run build
npm run test:ui
```

`test:ui` 使用本机 Chrome 测试体验模式和手机布局，运行前需要启动前端。截图输出到 `artifacts/screenshots`。

```powershell
npm run test:live
```

`test:live` 必须同时启动前后端，使用上述独立测试库，创建测试用户、菜品、地址和订单，验证真实接口及库存流转。它会向测试库写数据，不要在正式业务数据库上运行。测试默认使用 B-system 的已有测试商家账号。

测试默认查找 `C:/Program Files/Google/Chrome/Application/chrome.exe`，其他路径可通过 `CHROME_PATH` 环境变量指定。

要整理新版代码格式：

```powershell
npm run format:campus
```

上线部署时，需要由网页服务器把 `/api` 转发给后端，并配置 HTTPS；Vite 的开发代理只用于本地开发与预览。此交付已经完成本机开发与联调，没有部署到公网。

## 9. 本次验收记录（2026-10-03）

- `npm run build`：通过，生成生产文件；按需加载 UI 组件后，主 JavaScript 文件约 346 KB（gzip 约 120 KB）。
- `npm run test:ui`：通过，包括体验模式双方操作、刷新恢复、桌面与 390px 手机布局；没有未捕获的 JavaScript 异常。
- `npm run test:live`：通过，连接 B-system 和真实 MySQL 独立测试库，覆盖角色校验、商家发布与上下架、学生注册、Cookie 会话恢复、真实分类、购物车项下单、配送地址、模拟付款、库存扣减、商家配送、确认收餐、评价与订单状态日志。
- 两个上表中的真实测试账号均再次验证登录成功。
- 参考截图位于 `artifacts/screenshots`；其中 `08-real-backend-order.png` 是真实后端联调订单详情，其他页面截图来自明确标记的体验模式。
