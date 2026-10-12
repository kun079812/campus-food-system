import { chromium } from '@playwright/test'
import assert from 'node:assert/strict'
import { mkdir } from 'node:fs/promises'

const origin = process.env.TEST_ORIGIN || 'http://127.0.0.1:5173'
const browser = await chromium.launch({
  executablePath:
    process.env.CHROME_PATH || 'C:/Program Files/Google/Chrome/Application/chrome.exe',
  headless: true
})
const context = await browser.newContext({
  viewport: { width: 1440, height: 1050 },
  locale: 'zh-CN'
})
const page = await context.newPage()
const errors = []
page.on('pageerror', (error) => errors.push(error.message))
const shotDir = 'artifacts/screenshots'
await mkdir(shotDir, { recursive: true })
async function capture(name) {
  await page.waitForFunction(() => document.querySelectorAll('.el-message').length === 0)
  await page.screenshot({ path: `${shotDir}/${name}.png`, fullPage: true })
}
async function assertMobileLayout() {
  const overflow = await page.evaluate(() =>
    [
      ...document.querySelectorAll(
        '.dashboard-content,.side-panel,.merchant-stats,.merchant-panels,.personal-welcome,.dish-grid,.site-header'
      )
    ]
      .filter((el) => el.getBoundingClientRect().right > innerWidth + 1)
      .map((el) => el.className)
  )
  assert.deepEqual(
    overflow,
    [],
    'Important mobile panels must fit the viewport, even inside overflow:hidden parents'
  )
}
async function logout() {
  await page.getByRole('button', { name: '账号菜单' }).click()
  await page.getByRole('menuitem', { name: '退出登录' }).click()
  await page.getByRole('button', { name: '体验学生端' }).waitFor()
}
try {
  await page.goto(origin)
  await page.getByRole('button', { name: '体验学生端' }).waitFor()
  await capture('01-login-desktop')
  await page.getByRole('button', { name: '体验学生端' }).click()
  await page.locator('.dish-card').first().waitFor()
  assert.equal(await page.locator('.dish-card').count(), 5)
  await capture('02-student-home-desktop')
  await page.getByRole('button', { name: '收藏红烧肉盖饭', exact: true }).click()
  await page.getByRole('button', { name: '加入购物车：红烧肉盖饭', exact: true }).click()
  await page.getByRole('button', { name: '打开购物车' }).click()
  await page.locator('.cart-checkout').getByRole('button', { name: '去结算' }).click()
  await page.getByPlaceholder('例如：少辣，不要香菜').fill('验收订单：不要香菜')
  await page.getByRole('button', { name: '确认创建订单', exact: true }).click()
  const latestOrder = page.locator('.full-order-list .order-entry').first()
  await latestOrder.getByRole('button', { name: '模拟支付 ¥12.80', exact: true }).click()
  await page.getByText('模拟支付成功，食堂正在等待接单。', { exact: true }).waitFor()
  await latestOrder.getByRole('button', { name: '订单详情' }).click()
  await page
    .getByRole('dialog', { name: '这份美味的旅程' })
    .getByText('验收订单：不要香菜', { exact: false })
    .waitFor()
  await page
    .getByRole('dialog', { name: '这份美味的旅程' })
    .getByRole('button', { name: 'Close this dialog' })
    .click()
  await page
    .getByRole('navigation', { name: '主导航' })
    .getByRole('button', { name: '个人中心' })
    .click()
  await capture('03-student-center-desktop')
  await page.reload()
  await page.getByRole('heading', { name: '每一餐，都是小确幸。' }).waitFor()
  await logout()
  await page.getByRole('button', { name: '体验商家端' }).click()
  await page.getByRole('button', { name: '发布新菜品' }).waitFor()
  await capture('04-merchant-desktop')
  await page.getByRole('button', { name: '发布新菜品' }).click()
  const dishDialog = page.getByRole('dialog', { name: '发布一道拿手好菜' })
  await dishDialog.getByLabel('菜品名称').fill('浏览器验收鸡腿饭')
  await dishDialog.getByLabel('价格（元）').fill('15.80')
  await dishDialog.getByLabel('库存（份）').fill('12')
  await dishDialog.getByRole('button', { name: '保存菜品', exact: true }).click()
  await page.getByText('菜品已保存', { exact: true }).waitFor()
  await page
    .getByRole('navigation', { name: '商家导航' })
    .getByRole('button', { name: '菜品管理' })
    .click()
  const dishRow = page.getByRole('row').filter({ hasText: '浏览器验收鸡腿饭' })
  await dishRow.getByRole('button', { name: '下架', exact: true }).click()
  await dishRow.getByRole('button', { name: '上架', exact: true }).waitFor()
  await dishRow.getByRole('button', { name: '编辑', exact: true }).click()
  await page.getByRole('dialog').getByLabel('价格（元）').fill('16.80')
  await page.getByRole('dialog').getByRole('button', { name: '保存菜品', exact: true }).click()
  await dishRow.getByText('¥16.80', { exact: true }).waitFor()
  await dishRow.getByRole('button', { name: '上架', exact: true }).click()
  await page
    .getByRole('navigation', { name: '商家导航' })
    .getByRole('button', { name: /订单管理/ })
    .click()
  const merchantOrder = page
    .locator('.full-order-list .order-entry')
    .filter({ hasText: '验收订单：不要香菜' })
  await merchantOrder.getByRole('button', { name: '接单并制作' }).click()
  await merchantOrder.getByRole('button', { name: '通知取餐' }).click()
  await merchantOrder.getByText('待取餐', { exact: true }).waitFor()
  await logout()
  await page.getByRole('button', { name: '体验学生端' }).click()
  await page.getByRole('heading', { name: '浏览器验收鸡腿饭', exact: true }).waitFor()
  await page
    .getByRole('navigation', { name: '主导航' })
    .getByRole('button', { name: '我的订单' })
    .click()
  const finalOrder = page
    .locator('.full-order-list .order-entry')
    .filter({ hasText: '验收订单：不要香菜' })
  await finalOrder.getByRole('button', { name: '确认取餐' }).click()
  await finalOrder.getByRole('button', { name: '评价这餐' }).click()
  await page
    .getByPlaceholder('味道、分量、服务…说说你的真实感受吧')
    .fill('味道很好，测试流程已完成。')
  await page.getByRole('button', { name: '提交评价' }).click()
  await page.getByText('感谢评价，愿你每一餐都开心。').waitFor()
  await page
    .getByRole('navigation', { name: '主导航' })
    .getByRole('button', { name: '个人中心' })
    .click()
  await page.getByRole('button', { name: '我的地址', exact: true }).click()
  await page.getByRole('button', { name: '新增地址' }).click()
  const addressDialog = page.getByRole('dialog', { name: '让美味找到你' })
  await addressDialog.getByLabel('收餐人').fill('验收同学')
  await addressDialog.getByLabel('联系电话').fill('13800000001')
  await addressDialog.getByLabel('详细地址').fill('北区五号楼')
  await addressDialog.getByRole('button', { name: '保存地址' }).click()
  await page.getByText('北区五号楼', { exact: true }).waitFor()
  await page.setViewportSize({ width: 390, height: 844 })
  await page
    .getByRole('navigation', { name: '主导航' })
    .getByRole('button', { name: '首页', exact: true })
    .click()
  await capture('05-student-mobile')
  await assertMobileLayout()
  assert.equal(
    await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth),
    true,
    'Mobile page must not overflow horizontally'
  )
  await logout()
  await capture('06-login-mobile')
  assert.equal(
    await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth),
    true,
    'Mobile login must not overflow horizontally'
  )
  await page.getByRole('button', { name: '体验商家端' }).click()
  await capture('07-merchant-mobile')
  await assertMobileLayout()
  assert.equal(
    await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth),
    true,
    'Mobile merchant page must not overflow horizontally'
  )
  assert.deepEqual(errors, [], 'No uncaught browser JavaScript errors')
  console.log(
    'PASS: login, preview isolation, cart, checkout, payment, order details, persistence, merchant publish/edit/toggle, fulfilment, review, addresses, desktop and mobile layouts. No JS exceptions.'
  )
} catch (error) {
  await capture('failure')
  console.error('Browser errors:', errors)
  throw error
} finally {
  await browser.close()
}
