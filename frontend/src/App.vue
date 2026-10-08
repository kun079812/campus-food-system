<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from './api'

const roles = {
  STUDENT: { label: '消费者', title: '今天想吃点什么？', accent: '学生点餐中心' },
  MERCHANT: { label: '商家', title: '让每一笔订单都准时出餐', accent: '商家运营中心' },
  ADMIN: { label: '管理员', title: '掌握平台运行状态', accent: '平台管理中心' }
}

const demoDishes = [
  { id: 1, name: '香辣鸡腿饭', price: 15.9, stock: 50, description: '外酥里嫩的鸡腿，搭配时蔬和米饭', category: '套餐', stallName: '快餐档口', emoji: '🍗' },
  { id: 2, name: '番茄牛肉面', price: 18.0, stock: 28, description: '番茄汤底，牛肉片与手擀面', category: '面食', stallName: '面食档口', emoji: '🍜' },
  { id: 3, name: '鱼香肉丝盖饭', price: 14.5, stock: 32, description: '酸甜咸香，下饭首选', category: '套餐', stallName: '家常菜档口', emoji: '🍚' },
  { id: 4, name: '鲜肉小笼包', price: 8.0, stock: 16, description: '一笼八只，现点现蒸', category: '小吃', stallName: '早餐档口', emoji: '🥟' },
  { id: 5, name: '柠檬茶', price: 6.5, stock: 80, description: '清爽解腻，冰爽好喝', category: '饮品', stallName: '饮品档口', emoji: '🍋' },
  { id: 6, name: '照烧鸡排饭', price: 16.9, stock: 0, description: '照烧酱汁搭配大块鸡排', category: '套餐', stallName: '快餐档口', emoji: '🍱' }
]

const user = ref(readStorage('campus_food_user'))
const token = ref(localStorage.getItem('campus_food_token'))
const selectedRole = ref('STUDENT')
const loginForm = ref({ username: '', password: '' })
const loginLoading = ref(false)
const loginError = ref('')
const apiNotice = ref('')
const demoMode = ref(false)
const studentTab = ref('home')
const merchantTab = ref('overview')
const adminTab = ref('overview')
const keyword = ref('')
const category = ref('全部')
const dishes = ref([])
const cart = ref([])
const orders = ref([])
const merchantOrders = ref([])
const merchantDishes = ref([])
const showCheckout = ref(false)
const pickupType = ref('SELF_PICKUP')
const address = ref('东区 3 号楼 101 室')
const selectedOrder = ref(null)
const showOrderDetail = ref(false)
const newDish = ref({ name: '', price: 12, stock: 20, description: '' })

function readStorage(key) {
  try {
    return JSON.parse(localStorage.getItem(key) || 'null')
  } catch {
    return null
  }
}

const isLoggedIn = computed(() => Boolean(token.value && user.value))
const role = computed(() => roles[user.value?.roleCode || selectedRole.value])
const categories = computed(() => ['全部', ...new Set(dishes.value.map((dish) => dish.category).filter(Boolean))])
const filteredDishes = computed(() => dishes.value.filter((dish) => {
  const matchesKeyword = !keyword.value || `${dish.name}${dish.description}`.includes(keyword.value)
  const matchesCategory = category.value === '全部' || dish.category === category.value
  return matchesKeyword && matchesCategory
}))
const cartCount = computed(() => cart.value.reduce((sum, item) => sum + item.quantity, 0))
const cartTotal = computed(() => cart.value.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0).toFixed(2))
const completedOrders = computed(() => orders.value.filter((order) => order.status === 'COMPLETED').length)

function persistUser(loginData) {
  token.value = loginData.token || `demo-${Date.now()}`
  user.value = {
    userId: loginData.userId || Date.now(),
    username: loginData.username || loginForm.value.username,
    roleCode: loginData.roleCode || selectedRole.value,
    realName: loginData.realName || loginForm.value.username
  }
  localStorage.setItem('campus_food_token', token.value)
  localStorage.setItem('campus_food_user', JSON.stringify(user.value))
}

async function login() {
  loginError.value = ''
  if (!loginForm.value.username || !loginForm.value.password) {
    loginError.value = '请输入用户名和密码'
    return
  }

  loginLoading.value = true
  try {
    const { data } = await api.post('/auth/login', {
      ...loginForm.value,
      roleCode: selectedRole.value
    })
    persistUser(data)
    demoMode.value = false
    await loadRoleData()
    ElMessage.success(`欢迎回来，${roles[selectedRole.value].label}`)
  } catch (error) {
    loginError.value = error.response?.data?.message || '登录失败，请确认后端已启动并存在该账号'
  } finally {
    loginLoading.value = false
  }
}

function enterDemo() {
  persistUser({ username: 'demo_user', roleCode: selectedRole.value })
  demoMode.value = true
  dishes.value = [...demoDishes]
  merchantDishes.value = [...demoDishes]
  if (selectedRole.value === 'STUDENT') studentTab.value = 'home'
  if (selectedRole.value === 'MERCHANT') merchantTab.value = 'overview'
  if (selectedRole.value === 'ADMIN') adminTab.value = 'overview'
}

function logout() {
  token.value = null
  user.value = null
  demoMode.value = false
  cart.value = []
  localStorage.removeItem('campus_food_token')
  localStorage.removeItem('campus_food_user')
}

async function loadRoleData() {
  if (user.value?.roleCode === 'STUDENT') {
    try {
      const { data } = await api.get('/dishes')
      dishes.value = data
      apiNotice.value = ''
    } catch {
      dishes.value = [...demoDishes]
      apiNotice.value = '后端菜品接口暂不可用，当前显示演示数据。'
    }
  }
  if (user.value?.roleCode === 'MERCHANT') {
    try {
      const [dishResponse, orderResponse] = await Promise.all([
        api.get('/merchant/dishes'),
        api.get('/merchant/orders')
      ])
      merchantDishes.value = dishResponse.data
      merchantOrders.value = orderResponse.data
      apiNotice.value = ''
    } catch {
      merchantDishes.value = [...demoDishes]
      merchantOrders.value = demoMerchantOrders()
      apiNotice.value = '商家接口暂不可用，当前显示演示数据。'
    }
  }
  if (user.value?.roleCode === 'ADMIN') {
    try {
      const { data } = await api.get('/admin/orders')
      orders.value = data
      apiNotice.value = ''
    } catch {
      orders.value = demoAdminOrders()
      apiNotice.value = '管理员接口暂不可用，当前显示演示数据。'
    }
  }
}

function demoMerchantOrders() {
  return [
    { id: 1001, orderNo: 'CO202609190001', customer: '张同学', amount: 31.8, pickupType: 'SELF_PICKUP', status: 'WAIT_ACCEPT', createdAt: '今天 12:05' },
    { id: 1002, orderNo: 'CO202609190002', customer: '李同学', amount: 18.0, pickupType: 'DELIVERY', status: 'MAKING', createdAt: '今天 12:12' },
    { id: 1003, orderNo: 'CO202609190003', customer: '王同学', amount: 14.5, pickupType: 'READY_PICKUP', status: 'READY_PICKUP', createdAt: '今天 12:18' }
  ]
}

function demoAdminOrders() {
  return [
    { id: 1, orderNo: 'CO202609190001', customer: '张同学', stallName: '快餐档口', amount: 31.8, status: 'COMPLETED', createdAt: '12:05' },
    { id: 2, orderNo: 'CO202609190002', customer: '李同学', stallName: '面食档口', amount: 18.0, status: 'MAKING', createdAt: '12:12' },
    { id: 3, orderNo: 'CO202609190003', customer: '王同学', stallName: '家常菜档口', amount: 14.5, status: 'WAIT_ACCEPT', createdAt: '12:18' }
  ]
}

function addToCart(dish) {
  if (dish.stock <= 0) return ElMessage.warning('该菜品已售罄')
  const existing = cart.value.find((item) => item.id === dish.id)
  if (existing) existing.quantity++
  else cart.value.push({ ...dish, quantity: 1 })
  ElMessage.success('已加入购物车')
}

function changeQuantity(item, delta) {
  item.quantity += delta
  if (item.quantity <= 0) cart.value = cart.value.filter((cartItem) => cartItem.id !== item.id)
}

async function submitOrder() {
  if (!cart.value.length) return ElMessage.warning('购物车还是空的')
  try {
    if (!demoMode.value) {
      await api.post('/orders', {
        itemIds: cart.value.map((item) => item.id),
        pickupType: pickupType.value,
        addressId: pickupType.value === 'DELIVERY' ? 1 : null
      })
    }
    orders.value.unshift({
      id: Date.now(),
      orderNo: `CO${Date.now()}`,
      amount: Number(cartTotal.value),
      status: 'WAIT_PAY',
      pickupType: pickupType.value,
      createdAt: '刚刚',
      items: cart.value.map((item) => ({ name: item.name, quantity: item.quantity }))
    })
    cart.value = []
    showCheckout.value = false
    studentTab.value = 'orders'
    ElMessage.success('订单已创建，请完成模拟支付')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '订单创建失败')
  }
}

async function payOrder(order) {
  try {
    if (!demoMode.value) await api.post(`/orders/${order.id}/pay`, { paymentMethod: 'MOCK' })
    order.status = 'WAIT_ACCEPT'
    ElMessage.success('模拟支付成功')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '支付失败')
  }
}

async function updateMerchantOrder(order, nextStatus) {
  try {
    if (!demoMode.value) await api.post(`/merchant/orders/${order.id}/status`, { status: nextStatus })
    order.status = nextStatus
    ElMessage.success('订单状态已更新')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '状态更新失败')
  }
}

async function saveDish() {
  if (!newDish.value.name) return ElMessage.warning('请输入菜品名称')
  try {
    if (!demoMode.value) await api.post('/merchant/dishes', { ...newDish.value, stallId: 1, categoryId: 1 })
    merchantDishes.value.unshift({ ...newDish.value, id: Date.now(), stock: Number(newDish.value.stock), price: Number(newDish.value.price) })
    newDish.value = { name: '', price: 12, stock: 20, description: '' }
    ElMessage.success('菜品已保存')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '菜品保存失败')
  }
}

function showDetail(order) {
  selectedOrder.value = order
  showOrderDetail.value = true
}

function statusLabel(status) {
  return {
    WAIT_PAY: '待支付', WAIT_ACCEPT: '待接单', MAKING: '制作中',
    READY_PICKUP: '待取餐', DELIVERING: '配送中', DELIVERED: '已送达',
    COMPLETED: '已完成', CANCELED: '已取消', REJECTED: '已拒单'
  }[status] || status
}

function formatMoney(value) {
  return Number(value || 0).toFixed(2)
}

function startFromStoredUser() {
  if (!isLoggedIn.value) return
  demoMode.value = token.value?.startsWith('demo-') || false
  if (demoMode.value) enterDemo()
  else loadRoleData()
}

onMounted(startFromStoredUser)
</script>

<template>
  <div v-if="!isLoggedIn" class="auth-shell">
    <div class="auth-decoration decoration-one"></div>
    <div class="auth-decoration decoration-two"></div>
    <main class="auth-card">
      <div class="brand-mark">食</div>
      <p class="eyebrow">CAMPUS FOOD ORDER</p>
      <h1>校园食堂外卖</h1>
      <p class="auth-subtitle">少排队一点，多享受一会儿</p>

      <div class="role-switcher">
        <button v-for="(item, code) in roles" :key="code" :class="{ active: selectedRole === code }" @click="selectedRole = code">
          <span>{{ code === 'STUDENT' ? '◉' : code === 'MERCHANT' ? '▣' : '◆' }}</span>
          {{ item.label }}
        </button>
      </div>

      <form class="auth-form" @submit.prevent="login">
        <label>登录账号<input v-model="loginForm.username" placeholder="请输入用户名" /></label>
        <label>登录密码<input v-model="loginForm.password" type="password" placeholder="请输入密码" /></label>
        <p v-if="loginError" class="form-error">{{ loginError }}</p>
        <button class="primary-button full" :disabled="loginLoading">
          {{ loginLoading ? '登录中...' : `进入${roles[selectedRole].label}端` }}
        </button>
      </form>

      <button class="demo-button" @click="enterDemo">先看看页面演示</button>
      <p class="auth-footnote">真实登录会连接 Spring Boot 后端；演示模式只用于预览页面。</p>
    </main>
  </div>

  <div v-else class="app-shell">
    <aside class="sidebar">
      <div class="sidebar-brand"><span class="brand-mark small">食</span><span>校园食堂</span></div>
      <div class="role-chip">{{ role.accent }}</div>

      <nav v-if="user.roleCode === 'STUDENT'" class="nav-list">
        <button :class="{ selected: studentTab === 'home' }" @click="studentTab = 'home'">⌂ <span>发现菜品</span></button>
        <button :class="{ selected: studentTab === 'cart' }" @click="studentTab = 'cart'">▱ <span>购物车</span><b v-if="cartCount">{{ cartCount }}</b></button>
        <button :class="{ selected: studentTab === 'orders' }" @click="studentTab = 'orders'">▤ <span>我的订单</span></button>
      </nav>
      <nav v-else-if="user.roleCode === 'MERCHANT'" class="nav-list">
        <button :class="{ selected: merchantTab === 'overview' }" @click="merchantTab = 'overview'">⌂ <span>经营概览</span></button>
        <button :class="{ selected: merchantTab === 'orders' }" @click="merchantTab = 'orders'">▤ <span>订单处理</span></button>
        <button :class="{ selected: merchantTab === 'dishes' }" @click="merchantTab = 'dishes'">◈ <span>菜品库存</span></button>
      </nav>
      <nav v-else class="nav-list">
        <button :class="{ selected: adminTab === 'overview' }" @click="adminTab = 'overview'">⌂ <span>平台概览</span></button>
        <button :class="{ selected: adminTab === 'users' }" @click="adminTab = 'users'">◎ <span>用户管理</span></button>
        <button :class="{ selected: adminTab === 'orders' }" @click="adminTab = 'orders'">▤ <span>订单监管</span></button>
      </nav>

      <div class="sidebar-bottom">
        <div class="user-mini"><span class="avatar">{{ (user.realName || user.username).slice(0, 1) }}</span><div><strong>{{ user.realName || user.username }}</strong><small>{{ roles[user.roleCode].label }}</small></div></div>
        <button class="logout-button" @click="logout">退出登录</button>
      </div>
    </aside>

    <main class="workspace">
      <div v-if="demoMode" class="demo-banner">当前为页面演示模式，连接后端并登录后会显示真实数据。</div>
      <div v-if="apiNotice" class="api-notice">{{ apiNotice }}</div>

      <template v-if="user.roleCode === 'STUDENT'">
        <section v-if="studentTab === 'home'" class="page-content">
          <div class="student-hero"><div><p class="eyebrow light">GOOD FOOD GOOD MOOD</p><h1>{{ role.title }}</h1><p>今天也要好好吃饭，附近的食堂美味已经为你准备好了。</p></div><div class="hero-illustration">🍜</div></div>
          <div class="section-heading"><div><p class="eyebrow">DISCOVER</p><h2>今日菜单</h2></div><div class="search-box"><span>⌕</span><input v-model="keyword" placeholder="搜索菜品或档口" /></div></div>
          <div class="category-row"><button v-for="item in categories" :key="item" :class="{ active: category === item }" @click="category = item">{{ item }}</button></div>
          <div class="dish-grid">
            <article v-for="dish in filteredDishes" :key="dish.id" class="dish-card">
              <div class="dish-visual"><span>{{ dish.emoji || '🍱' }}</span><i v-if="dish.stock <= 0">售罄</i></div>
              <div class="dish-copy"><div class="dish-meta"><span>{{ dish.stallName || '校园档口' }}</span><span>{{ dish.category || '精选' }}</span></div><h3>{{ dish.name }}</h3><p>{{ dish.description || '今日新鲜制作' }}</p><div class="dish-footer"><strong>¥{{ formatMoney(dish.price) }}</strong><button class="add-button" :disabled="dish.stock <= 0" @click="addToCart(dish)">{{ dish.stock > 0 ? '+' : '—' }}</button></div></div>
            </article>
          </div>
        </section>

        <section v-else-if="studentTab === 'cart'" class="page-content narrow-page">
          <div class="section-heading"><div><p class="eyebrow">YOUR BASKET</p><h1>购物车</h1></div></div>
          <div v-if="!cart.length" class="empty-state"><span>🛒</span><h2>购物车还是空的</h2><p>去发现一些今天想吃的东西吧。</p><button class="primary-button" @click="studentTab = 'home'">去逛逛</button></div>
          <div v-else class="cart-layout"><div class="cart-list"><div v-for="item in cart" :key="item.id" class="cart-row"><div class="cart-thumb">{{ item.emoji || '🍱' }}</div><div class="cart-info"><h3>{{ item.name }}</h3><p>{{ item.stallName || '校园档口' }}</p></div><strong>¥{{ formatMoney(item.price * item.quantity) }}</strong><div class="quantity-control"><button @click="changeQuantity(item, -1)">−</button><span>{{ item.quantity }}</span><button @click="changeQuantity(item, 1)">+</button></div></div></div><div class="checkout-card"><p class="eyebrow">ORDER SUMMARY</p><h2>结算</h2><div class="summary-line"><span>商品数量</span><strong>{{ cartCount }} 件</strong></div><div class="summary-line total-line"><span>应付金额</span><strong>¥{{ cartTotal }}</strong></div><button class="primary-button full" @click="showCheckout = true">去结算</button></div></div>
        </section>

        <section v-else class="page-content narrow-page"><div class="section-heading"><div><p class="eyebrow">ORDER HISTORY</p><h1>我的订单</h1></div></div><div v-if="!orders.length" class="empty-state compact"><span>◫</span><h2>还没有订单</h2><p>下单后，你可以在这里追踪进度。</p></div><div v-else class="order-list"><article v-for="order in orders" :key="order.id" class="order-card"><div class="order-top"><div><small>{{ order.orderNo }} · {{ order.createdAt }}</small><h3>{{ order.items?.map((item) => `${item.name} × ${item.quantity}`).join('、') || '校园餐订单' }}</h3></div><span class="status-badge">{{ statusLabel(order.status) }}</span></div><div class="order-bottom"><strong>¥{{ formatMoney(order.amount) }}</strong><div><button class="text-button" @click="showDetail(order)">查看详情</button><button v-if="order.status === 'WAIT_PAY'" class="primary-button small-button" @click="payOrder(order)">模拟支付</button></div></div></article></div></section>
      </template>

      <template v-else-if="user.roleCode === 'MERCHANT'">
        <section class="page-content" v-if="merchantTab === 'overview'"><div class="section-heading"><div><p class="eyebrow">MERCHANT CENTER</p><h1>经营概览</h1><p class="muted">欢迎回来，今天也要高效出餐。</p></div><span class="date-pill">2026 / 09 / 19</span></div><div class="stats-grid"><div class="stat-card teal"><small>今日营业额</small><strong>¥428.50</strong><span>较昨日 ↑ 12.6%</span></div><div class="stat-card orange"><small>今日订单</small><strong>32</strong><span>待处理 3 单</span></div><div class="stat-card purple"><small>在售菜品</small><strong>{{ merchantDishes.filter((dish) => dish.stock > 0).length }}</strong><span>库存需要关注 1 项</span></div></div><div class="panel-grid"><div class="panel"><div class="panel-title"><h2>待处理订单</h2><button class="text-button" @click="merchantTab = 'orders'">查看全部 →</button></div><div v-for="order in merchantOrders.slice(0, 3)" :key="order.id" class="mini-order"><span class="order-dot"></span><div><strong>{{ order.orderNo }}</strong><p>{{ order.customer }} · ¥{{ formatMoney(order.amount) }}</p></div><span class="status-badge">{{ statusLabel(order.status) }}</span></div></div><div class="panel quick-panel"><div class="panel-title"><h2>快捷操作</h2></div><button @click="merchantTab = 'dishes'">＋ 新增菜品 <span>管理菜单与库存</span></button><button @click="merchantTab = 'orders'">▤ 处理订单 <span>查看待接单任务</span></button></div></div></section>
        <section class="page-content" v-else-if="merchantTab === 'orders'"><div class="section-heading"><div><p class="eyebrow">ORDER BOARD</p><h1>订单处理</h1></div></div><div class="panel table-panel"><div class="filter-row"><button class="filter-active">全部订单</button><button>待接单</button><button>制作中</button><button>配送中</button></div><div v-for="order in merchantOrders" :key="order.id" class="merchant-order-row"><div><small>{{ order.orderNo }} · {{ order.createdAt }}</small><h3>{{ order.customer }}</h3><p>{{ order.pickupType === 'DELIVERY' ? '商家配送' : '到店自取' }} · ¥{{ formatMoney(order.amount) }}</p></div><span class="status-badge">{{ statusLabel(order.status) }}</span><div class="order-actions"><button v-if="order.status === 'WAIT_ACCEPT'" class="primary-button small-button" @click="updateMerchantOrder(order, 'MAKING')">接单</button><button v-if="order.status === 'MAKING'" class="primary-button small-button" @click="updateMerchantOrder(order, order.pickupType === 'DELIVERY' ? 'DELIVERING' : 'READY_PICKUP')">出餐</button><button v-if="order.status === 'READY_PICKUP'" class="outline-button small-button" @click="updateMerchantOrder(order, 'COMPLETED')">完成</button></div></div></div></section>
        <section class="page-content" v-else><div class="section-heading"><div><p class="eyebrow">MENU MANAGEMENT</p><h1>菜品与库存</h1></div></div><div class="panel dish-form-panel"><h2>新增菜品</h2><div class="inline-form"><input v-model="newDish.name" placeholder="菜品名称" /><input v-model.number="newDish.price" type="number" placeholder="价格" /><input v-model.number="newDish.stock" type="number" placeholder="库存" /><input v-model="newDish.description" placeholder="菜品简介" /><button class="primary-button" @click="saveDish">保存菜品</button></div></div><div class="merchant-dish-grid"><div v-for="dish in merchantDishes" :key="dish.id" class="inventory-card"><span class="inventory-emoji">{{ dish.emoji || '🍱' }}</span><div><h3>{{ dish.name }}</h3><p>¥{{ formatMoney(dish.price) }} · 库存 {{ dish.stock }}</p></div><span :class="['stock-label', { danger: dish.stock <= 5 }]">{{ dish.stock > 0 ? '在售' : '售罄' }}</span></div></div></section>
      </template>

      <template v-else>
        <section class="page-content" v-if="adminTab === 'overview'"><div class="section-heading"><div><p class="eyebrow">ADMIN CONSOLE</p><h1>平台概览</h1><p class="muted">实时查看校园点餐平台运行状态。</p></div><span class="date-pill">系统运行正常</span></div><div class="stats-grid"><div class="stat-card teal"><small>今日订单</small><strong>1,286</strong><span>较昨日 ↑ 8.4%</span></div><div class="stat-card orange"><small>入驻档口</small><strong>24</strong><span>覆盖 3 个食堂</span></div><div class="stat-card purple"><small>平台用户</small><strong>8,492</strong><span>今日活跃 1,204</span></div></div><div class="panel-grid"><div class="panel"><div class="panel-title"><h2>订单动态</h2><button class="text-button" @click="adminTab = 'orders'">查看监管 →</button></div><div class="admin-bars"><div><span style="height: 42%"></span><small>周一</small></div><div><span style="height: 64%"></span><small>周二</small></div><div><span style="height: 52%"></span><small>周三</small></div><div><span style="height: 78%"></span><small>周四</small></div><div><span style="height: 92%"></span><small>周五</small></div><div><span style="height: 68%"></span><small>周六</small></div><div><span style="height: 58%"></span><small>周日</small></div></div></div><div class="panel quick-panel"><div class="panel-title"><h2>平台设置</h2></div><button @click="adminTab = 'users'">◎ 用户账号管理 <span>启用、停用、查看角色</span></button><button @click="adminTab = 'orders'">▤ 全局订单监管 <span>查看平台所有订单</span></button></div></div></section>
        <section class="page-content" v-else-if="adminTab === 'users'"><div class="section-heading"><div><p class="eyebrow">USER MANAGEMENT</p><h1>用户管理</h1></div></div><div class="panel table-panel"><div class="table-head"><span>用户</span><span>角色</span><span>状态</span><span>操作</span></div><div v-for="item in [{name:'张同学',account:'student001',role:'消费者',status:'正常'},{name:'李老板',account:'merchant001',role:'商家',status:'正常'},{name:'系统管理员',account:'admin001',role:'管理员',status:'正常'}]" :key="item.account" class="table-row"><div><strong>{{ item.name }}</strong><small>{{ item.account }}</small></div><span>{{ item.role }}</span><span class="stock-label">{{ item.status }}</span><button class="text-button">查看</button></div></div></section>
        <section class="page-content" v-else><div class="section-heading"><div><p class="eyebrow">ORDER SUPERVISION</p><h1>订单监管</h1></div></div><div class="panel table-panel"><div v-for="order in orders" :key="order.id" class="merchant-order-row"><div><small>{{ order.orderNo }} · {{ order.createdAt }}</small><h3>{{ order.customer }} <span class="muted">{{ order.stallName }}</span></h3></div><span>¥{{ formatMoney(order.amount) }}</span><span class="status-badge">{{ statusLabel(order.status) }}</span><button class="text-button" @click="showDetail(order)">详情</button></div></div></section>
      </template>
    </main>

    <el-dialog v-model="showCheckout" title="确认订单" width="520px"><div class="checkout-dialog"><div class="checkout-option"><label><input v-model="pickupType" type="radio" value="SELF_PICKUP" /> 到店自取</label><span>备餐完成后到档口领取</span></div><div class="checkout-option"><label><input v-model="pickupType" type="radio" value="DELIVERY" /> 商家配送</label><span>配送至校内地址</span></div><input v-if="pickupType === 'DELIVERY'" v-model="address" class="dialog-input" placeholder="请输入校内配送地址" /><div class="dialog-total"><span>应付金额</span><strong>¥{{ cartTotal }}</strong></div><button class="primary-button full" @click="submitOrder">提交订单</button></div></el-dialog>
    <el-dialog v-model="showOrderDetail" title="订单详情" width="520px"><div v-if="selectedOrder" class="order-detail"><p><span>订单号</span><strong>{{ selectedOrder.orderNo }}</strong></p><p><span>订单状态</span><strong>{{ statusLabel(selectedOrder.status) }}</strong></p><p><span>订单金额</span><strong>¥{{ formatMoney(selectedOrder.amount) }}</strong></p><p><span>取餐方式</span><strong>{{ selectedOrder.pickupType === 'DELIVERY' ? '商家配送' : '到店自取' }}</strong></p></div></el-dialog>
  </div>
</template>
