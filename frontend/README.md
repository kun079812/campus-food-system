# 食堂小站 · 校园点餐系统

按参考图制作的 Vue 3 网站，包含学生登录、点餐首页、个人中心、商家管理和手机布局，已接入 `kun079812/campus-food-system` 仓库的 **B-system** 分支。

第一次使用请阅读 [启动说明、测试账号和代码导览](./START-HERE.md)。

## 启动前端

```powershell
npm install
npm run dev
```

浏览器打开终端显示的地址，默认 `http://127.0.0.1:5173/`。

后端项目位于相邻的 `campus-food-system-backend-B/backend` 文件夹，在该文件夹执行 `./start-local.ps1`。

前端的 `/api` 由 Vite 转发到 `http://localhost:8080`，使用后端的 Session Cookie 登录。修改后端地址时，把 `.env.example` 复制为 `.env.local` 后设置：

```text
VITE_BACKEND_URL=http://localhost:8080
```

## 已实现

- 学生/商家登录、注册、会话恢复和退出。
- 菜品搜索与分类、同档口购物车、自取或配送、模拟支付。
- 订单状态、详情、确认收餐、用餐评价、地址和个人资料。
- 商家菜品发布/编辑/上下架、分类、接单出餐、营业设置、实际销售统计。
- 独立的本机体验模式，真实接口失败不会伪装成示例成功。

新版入口是 `src/CampusApp.vue`；原来的练习文件仍然保留。

## 验证

```powershell
npm run build
npm run test:ui
npm run test:live
```

浏览器测试使用本机 Chrome。真实联调测试只应在独立开发数据库上执行，会新增测试记录。详细说明见 [START-HERE.md](./START-HERE.md)。

校园水彩背景由内置 imagegen 生成，项目路径、来源和完整提示词见 [ASSET-CREDITS.md](./ASSET-CREDITS.md)。
