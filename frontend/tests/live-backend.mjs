import { chromium } from '@playwright/test'
import assert from 'node:assert/strict'
import { mkdir } from 'node:fs/promises'

// Run only against the isolated B-system development database documented in START-HERE.md.
const browser = await chromium.launch({
  executablePath:
    process.env.CHROME_PATH || 'C:/Program Files/Google/Chrome/Application/chrome.exe',
  headless: true
})
const context = await browser.newContext({
  viewport: { width: 1440, height: 1000 },
  locale: 'zh-CN'
})
const page = await context.newPage()
const errors = []
page.on('pageerror', (error) => errors.push(error.message))
const origin = process.env.TEST_ORIGIN || 'http://127.0.0.1:5173'
const suffix = String(Date.now())
const dishName = `联调鸡腿饭${suffix.slice(-5)}`
const username = `web_student_${suffix}`
const password = 'WebTest12345'
const remark = `联调订单${suffix}`
async function get(path) {
  const response = await context.request.get(`${origin}/api${path}`)
  const result = await response.json()
  assert.equal(result.code, 200, `${path}: ${result.message}`)
  return result.data
}
async function login(role, name, secret) {
  await page.getByRole('tab', { name: `${role}登录`, exact: true }).click()
  await page
    .getByRole('textbox', { name: role === '学生' ? '学号或用户名' : '商家账号', exact: true })
    .fill(name)
  await page.getByRole('textbox', { name: '密码', exact: true }).fill(secret)
  await page.getByRole('button', { name: '登录', exact: true }).click()
  await page.getByRole('button', { name: '账号菜单' }).waitFor()
  await page.waitForFunction(() => !document.querySelector('.main-content[aria-busy="true"]'))
}
async function logout() {
  await page.getByRole('button', { name: '账号菜单' }).click()
  await page.getByRole('menuitem', { name: '退出登录' }).click()
  await page.getByRole('button', { name: '体验学生端' }).waitFor()
}
try {
  await page.goto(origin)
  await page.getByRole('button', { name: '体验学生端' }).waitFor()
  // The same merchant credentials in the student tab must not grant a student identity.
  await page.getByRole('textbox', { name: '学号或用户名' }).fill('merchant_test')
  await page.getByRole('textbox', { name: '密码', exact: true }).fill('12345678')
  await page.getByRole('button', { name: '登录', exact: true }).click()
  await page.getByText('这个账号属于商家，请切换对应登录入口。', { exact: true }).waitFor()
  await login('商家', 'merchant_test', '12345678')
  assert.equal(
    await page.locator('.preview-notice').count(),
    0,
    'Live mode never substitutes preview data'
  )
  await page.getByRole('button', { name: '发布新菜品' }).click()
  const dishDialog = page.getByRole('dialog', { name: '发布一道拿手好菜' })
  await dishDialog.getByLabel('菜品名称').fill(dishName)
  await dishDialog.getByLabel('价格（元）').fill('13.45')
  await dishDialog.getByLabel('库存（份）').fill('7')
  await dishDialog.getByRole('button', { name: '保存菜品', exact: true }).click()
  await page.getByText('菜品已保存', { exact: true }).waitFor()
  const created = (await get('/merchant/dishes')).find((d) => d.name === dishName)
  assert.ok(created?.id, 'Dish was persisted in MySQL')
  await page
    .getByRole('navigation', { name: '商家导航' })
    .getByRole('button', { name: '菜品管理' })
    .click()
  const row = page.getByRole('row').filter({ hasText: dishName })
  await row.getByRole('button', { name: '下架', exact: true }).click()
  await row.getByRole('button', { name: '上架', exact: true }).waitFor()
  assert.equal((await get('/merchant/dishes')).find((d) => d.id === created.id).status, 0)
  await row.getByRole('button', { name: '上架', exact: true }).click()
  await row.getByRole('button', { name: '下架', exact: true }).waitFor()
  await logout()
  await page.getByRole('button', { name: '立即注册', exact: true }).click()
  await page.getByRole('textbox', { name: '学号或用户名' }).fill(username)
  await page.getByRole('textbox', { name: '姓名', exact: true }).fill('联调同学')
  await page.getByRole('textbox', { name: '手机号', exact: true }).fill(`139${suffix.slice(-8)}`)
  await page.getByRole('textbox', { name: '密码', exact: true }).fill(password)
  await page.getByRole('button', { name: '创建账号', exact: true }).click()
  await page.getByText('注册成功，请使用新账号登录。', { exact: true }).waitFor()
  await login('学生', username, password)
  await page.getByRole('button', { name: '盖饭', exact: true }).waitFor()
  await page.getByRole('button', { name: '饮品', exact: true }).click()
  assert.ok(
    (await page.locator('.dish-card').count()) > 0,
    'Server category filters render real data'
  )
  await page.getByRole('button', { name: '全部', exact: true }).click()
  await page.reload()
  await page.getByRole('button', { name: '账号菜单' }).waitFor()
  assert.equal(
    (await get('/users/me')).username,
    username,
    'Login survives reload through the server cookie'
  )
  await page
    .getByRole('navigation', { name: '主导航' })
    .getByRole('button', { name: '食堂菜单' })
    .click()
  await page.getByRole('button', { name: `加入购物车：${dishName}`, exact: true }).click()
  await page.getByText('已加入购物车', { exact: true }).waitFor()
  const cart = await get('/cart/items')
  assert.equal(cart.length, 1)
  assert.equal(cart[0].dishId, created.id)
  await page.getByRole('button', { name: '打开购物车' }).click()
  await page.locator('.cart-checkout').getByRole('button', { name: '去结算' }).click()
  await page.getByRole('radio', { name: /送餐到家/ }).check()
  await page.getByRole('button', { name: '添加收餐地址' }).click()
  const addressDialog = page.getByRole('dialog', { name: '让美味找到你' })
  await addressDialog.getByLabel('详细地址').fill('联调专用地址：校园北区一号楼')
  await addressDialog.getByRole('button', { name: '保存地址' }).click()
  await page.getByText('地址已保存', { exact: true }).waitFor()
  const addresses = await get('/addresses')
  assert.equal(addresses.length, 1)
  await page
    .getByRole('dialog', { name: '离美味，只差一步' })
    .getByLabel('收餐地址', { exact: true })
    .selectOption(String(addresses[0].id))
  await page.getByPlaceholder('例如：少辣，不要香菜').fill(remark)
  await page.getByRole('button', { name: '确认创建订单', exact: true }).click()
  const orderCard = page.locator('.full-order-list .order-entry').filter({ hasText: remark })
  await orderCard.getByRole('button', { name: '模拟支付 ¥13.45', exact: true }).click()
  await orderCard.getByText('待接单', { exact: true }).waitFor()
  let order = (await get('/orders')).find((o) => o.remark === remark)
  assert.equal(order.status, 'WAIT_ACCEPT')
  assert.equal(order.totalAmount, 13.45)
  assert.equal(order.payAmount, 13.45)
  assert.equal((await get('/cart/items')).length, 0)
  await logout()
  await login('商家', 'merchant_test', '12345678')
  await page
    .getByRole('navigation', { name: '商家导航' })
    .getByRole('button', { name: /订单管理/ })
    .click()
  const merchantOrder = page.locator('.full-order-list .order-entry').filter({ hasText: remark })
  await merchantOrder.getByRole('button', { name: '接单并制作' }).click()
  await merchantOrder.getByRole('button', { name: '开始配送' }).click()
  await merchantOrder.getByRole('button', { name: '确认送达' }).click()
  await merchantOrder.getByText('已送达', { exact: true }).waitFor()
  assert.equal((await get('/merchant/dishes')).find((d) => d.id === created.id).stock, 6)
  const summary = await get('/merchant/dashboard/summary')
  assert.ok(summary.todaySales >= 13.45)
  await logout()
  await login('学生', username, password)
  await page
    .getByRole('navigation', { name: '主导航' })
    .getByRole('button', { name: '我的订单' })
    .click()
  const delivered = page.locator('.full-order-list .order-entry').filter({ hasText: remark })
  await delivered.getByRole('button', { name: '确认收餐' }).click()
  await delivered.getByRole('button', { name: '评价这餐' }).click()
  await page
    .getByPlaceholder('味道、分量、服务…说说你的真实感受吧')
    .fill('真实后端联调完成：菜品、库存、订单和评价均已保存。')
  await page.getByRole('button', { name: '提交评价' }).click()
  await page.getByText('感谢评价，愿你每一餐都开心。', { exact: true }).waitFor()
  assert.equal((await get('/orders')).find((o) => o.id === order.id).status, 'COMPLETED')
  assert.equal((await get('/student/reviews')).filter((r) => r.orderId === order.id).length, 1)
  await delivered.getByRole('button', { name: '订单详情' }).click()
  const detailDialog = page.getByRole('dialog', { name: '这份美味的旅程' })
  await detailDialog.getByText(dishName, { exact: false }).waitFor()
  await mkdir('artifacts/screenshots', { recursive: true })
  await page.waitForFunction(() => document.querySelectorAll('.el-message').length === 0)
  await page.screenshot({ path: 'artifacts/screenshots/08-real-backend-order.png', fullPage: true })
  const detail = await get(`/orders/${order.id}`)
  assert.ok(detail.statusLogs.length >= 6)
  assert.equal(detail.orderItems[0].dishId, created.id)
  assert.deepEqual(errors, [])
  console.log(
    JSON.stringify(
      {
        result: 'PASS',
        backend: 'B-system, real MySQL isolated database',
        checks: [
          'role mismatch rejected',
          'merchant publish/up-down',
          'student registration',
          'cookie reload',
          'cart row IDs',
          'delivery address',
          'order creation',
          'MOCK payment',
          'inventory decrement',
          'merchant fulfilment',
          'receipt confirmation',
          'review',
          'status timeline',
          'dashboard totals'
        ],
        orderId: order.id,
        dishId: created.id,
        student: username
      },
      null,
      2
    )
  )
} catch (error) {
  await mkdir('artifacts/screenshots', { recursive: true })
  await page.screenshot({ path: 'artifacts/screenshots/live-failure.png', fullPage: true })
  console.error('Browser errors:', errors)
  throw error
} finally {
  await browser.close()
}
