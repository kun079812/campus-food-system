import { reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { api, ApiError } from './campusApi'
import { readDemo, DEMO_KEY } from './demoData'

export function useCampus() {
  let demo = readDemo()
  const state = reactive({
    user: null,
    preview: false,
    booting: true,
    loading: false,
    busy: false,
    authBusy: false,
    page: 'home',
    error: '',
    authError: '',
    dishes: [],
    categories: [],
    canteens: [],
    stalls: [],
    cart: [],
    orders: [],
    addresses: [],
    reviews: [],
    stall: null,
    summary: {},
    sales: [],
    keyword: '',
    selectedCategory: '全部',
    selectedStall: '',
    favorites: [],
    cartOpen: false,
    checkoutOpen: false,
    detail: null,
    detailOpen: false,
    dishModal: false,
    editingDish: null,
    addressModal: false,
    editingAddress: null,
    reviewOrder: null,
    helpOpen: false
  })
  const merchant = computed(() => state.user?.roleCode === 'MERCHANT')
  const cartCount = computed(() => state.cart.reduce((sum, item) => sum + item.quantity, 0))
  const cartTotal = computed(
    () =>
      state.cart.reduce((sum, item) => sum + Math.round(item.dishPrice * 100) * item.quantity, 0) /
      100
  )
  const favoriteKey = () =>
    `canteen-favorites-${state.preview ? 'preview' : 'live'}-${state.user?.id}`
  function persistDemo() {
    localStorage.setItem(DEMO_KEY, JSON.stringify(demo))
  }
  function readFavorites() {
    try {
      state.favorites = JSON.parse(localStorage.getItem(favoriteKey()) || '[]')
    } catch {
      state.favorites = []
    }
  }
  function navigate(page) {
    const valid = merchant.value
      ? ['overview', 'dishes', 'orders', 'statistics', 'settings', 'reviews']
      : ['home', 'menu', 'profile', 'orders', 'addresses', 'favorites', 'settings']
    state.page = valid.includes(page) ? page : valid[0]
    window.location.hash = `/${merchant.value ? 'merchant' : 'student'}/${state.page}`
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
  function handleHash() {
    if (!state.user) return
    const parts = window.location.hash.split('/')
    const valid = merchant.value
      ? ['overview', 'dishes', 'orders', 'statistics', 'settings', 'reviews']
      : ['home', 'menu', 'profile', 'orders', 'addresses', 'favorites', 'settings']
    if (parts[1] === (merchant.value ? 'merchant' : 'student') && valid.includes(parts[2]))
      state.page = parts[2]
  }
  function clearSession() {
    state.user = null
    state.preview = false
    state.cart = []
    state.orders = []
    state.dishes = []
    state.addresses = []
    state.categories = []
    state.stall = null
    state.summary = {}
    state.sales = []
    state.reviews = []
    state.error = ''
    state.authError = ''
    state.keyword = ''
    state.selectedCategory = '全部'
    state.selectedStall = ''
    state.cartOpen = false
    state.checkoutOpen = false
    state.detailOpen = false
    state.detail = null
    state.dishModal = false
    state.addressModal = false
    state.reviewOrder = null
    sessionStorage.removeItem('canteen-preview-role')
    window.location.hash = '/login'
  }
  function report(error) {
    if (error.status === 401 && state.user && !state.preview) {
      clearSession()
      state.authError = '登录已过期，请重新登录。'
    }
    ElMessage.error(error.message || '操作失败，请稍后重试。')
  }
  async function perform(task, success) {
    if (state.busy) return false
    state.busy = true
    try {
      await task()
      if (success) ElMessage.success(success)
      return true
    } catch (error) {
      report(error)
      return false
    } finally {
      state.busy = false
    }
  }
  function previewData() {
    state.dishes = structuredClone(demo.dishes.filter((d) => merchant.value || d.status === 1))
    state.categories = structuredClone(demo.categories)
    state.stall = structuredClone(demo.stall)
    state.stalls = [structuredClone(demo.stall)]
    state.canteens = [{ id: 1, name: '第一食堂', location: '校园北区' }]
    state.cart = demo.cart.map((item) => {
      const dish = demo.dishes.find((d) => d.id === item.dishId)
      return {
        ...item,
        dishName: dish.name,
        dishPrice: dish.price,
        dishStock: dish.stock,
        dishStatus: dish.status,
        dishImageUrl: dish.imageUrl
      }
    })
    state.orders = structuredClone(demo.orders)
    state.addresses = structuredClone(demo.addresses)
    state.reviews = structuredClone(demo.reviews)
    const today = demo.orders.filter(
      (order) => new Date(order.createdAt).toDateString() === new Date().toDateString()
    )
    state.summary = {
      todayOrderCount: today.length,
      todaySales: today.reduce((sum, order) => sum + Number(order.payAmount || 0), 0),
      pendingOrderCount: demo.orders.filter((order) => order.status === 'WAIT_ACCEPT').length,
      latestOrders: state.orders.slice(0, 5)
    }
    state.sales = demo.dishes
      .map((dish) => {
        const items = demo.orders
          .filter((o) => o.payAmount > 0)
          .flatMap((o) => o.orderItems)
          .filter((i) => i.dishId === dish.id)
        return {
          dishId: dish.id,
          dishName: dish.name,
          salesQuantity: items.reduce((s, i) => s + i.quantity, 0),
          salesAmount: items.reduce((s, i) => s + i.price * i.quantity, 0)
        }
      })
      .sort((a, b) => b.salesQuantity - a.salesQuantity)
  }
  async function loadPublic() {
    const [dishes, canteens] = await Promise.all([api.dishes(), api.canteens()])
    const stalls = (await Promise.all(canteens.map((c) => api.stalls(c.id)))).flat()
    const categories = (await Promise.all(stalls.map((s) => api.categories(s.id)))).flat()
    Object.assign(state, {
      dishes: dishes.filter((d) => d.status === 1),
      canteens,
      stalls,
      categories
    })
  }
  async function loadPrivate() {
    if (merchant.value) {
      const [dishes, categories, orders, summary, sales, stall, reviews] = await Promise.all([
        api.merchantDishes(),
        api.merchantCategories(),
        api.orders(true),
        api.summary(),
        api.sales(),
        api.stall(),
        api.reviews(true)
      ])
      Object.assign(state, { dishes, categories, orders, summary, sales, stall, reviews })
    } else {
      const [cart, orders, addresses, reviews] = await Promise.all([
        api.cart(),
        api.orders(),
        api.addresses(),
        api.reviews()
      ])
      Object.assign(state, { cart, orders, addresses, reviews })
      await loadPublic()
    }
  }
  async function refresh() {
    if (!state.user) return
    state.loading = true
    state.error = ''
    try {
      if (state.preview) {
        demo = readDemo()
        previewData()
      } else await loadPrivate()
    } catch (error) {
      state.error = error.message
      if (error.status === 401) report(error)
    } finally {
      state.loading = false
    }
  }
  async function login(form, role, remember) {
    if (state.authBusy) return
    state.authBusy = true
    state.authError = ''
    try {
      const user = await api.login({ username: form.username.trim(), password: form.password })
      if (user.roleCode !== role) {
        await api.logout()
        throw new Error(
          `这个账号属于${user.roleCode === 'MERCHANT' ? '商家' : '学生'}，请切换对应登录入口。`
        )
      }
      state.preview = false
      state.user = user
      sessionStorage.removeItem('canteen-preview-role')
      if (remember) localStorage.setItem('canteen-remembered-account', form.username.trim())
      else localStorage.removeItem('canteen-remembered-account')
      readFavorites()
      navigate(merchant.value ? 'overview' : 'home')
      await refresh()
    } catch (error) {
      state.authError = error.message
    } finally {
      state.authBusy = false
    }
  }
  async function register(form, role) {
    if (state.authBusy) return false
    state.authBusy = true
    state.authError = ''
    try {
      await api.register({
        username: form.username.trim(),
        password: form.password,
        realName: form.realName.trim(),
        phone: form.phone.trim(),
        roleCode: role
      })
      ElMessage.success('注册成功，请使用新账号登录。')
      return true
    } catch (error) {
      state.authError = error.message
      return false
    } finally {
      state.authBusy = false
    }
  }
  function enterPreview(role) {
    clearSession()
    state.preview = true
    state.user = {
      id: role === 'MERCHANT' ? 9002 : 9001,
      username: role === 'MERCHANT' ? 'demo_merchant' : '20230123456',
      realName: role === 'MERCHANT' ? '暖心小厨' : '林同学',
      roleCode: role,
      phone: '13800000000'
    }
    sessionStorage.setItem('canteen-preview-role', role)
    previewData()
    readFavorites()
    navigate(merchant.value ? 'overview' : 'home')
  }
  async function logout() {
    if (state.preview) {
      clearSession()
      return
    }
    await perform(async () => {
      await api.logout()
      clearSession()
    })
  }
  function favorite(id) {
    state.favorites = state.favorites.includes(id)
      ? state.favorites.filter((v) => v !== id)
      : [...state.favorites, id]
    localStorage.setItem(favoriteKey(), JSON.stringify(state.favorites))
  }
  function addCart(dish) {
    return perform(async () => {
      if (dish.stock <= 0 || dish.status !== 1)
        throw new Error('这道菜暂时售罄了，看看其他美味吧。')
      if (state.preview) {
        const existing = demo.cart.find((i) => i.dishId === dish.id)
        if (
          demo.cart.length &&
          demo.dishes.find((d) => d.id === demo.cart[0].dishId).stallId !== dish.stallId
        )
          throw new Error('购物车已有其他档口的菜品，请先结算或移除。')
        if ((existing?.quantity || 0) >= dish.stock) throw new Error('已经达到当前库存上限。')
        if (existing) existing.quantity++
        else demo.cart.push({ id: Date.now(), dishId: dish.id, quantity: 1 })
        persistDemo()
        previewData()
      } else {
        await api.addCart(dish.id)
        state.cart = await api.cart()
      }
    }, '已加入购物车')
  }
  function changeQuantity(item, delta) {
    return perform(async () => {
      const quantity = item.quantity + delta
      if (quantity > item.dishStock) throw new Error('已经达到当前库存上限。')
      if (state.preview) {
        if (quantity <= 0) demo.cart = demo.cart.filter((i) => i.dishId !== item.dishId)
        else demo.cart.find((i) => i.dishId === item.dishId).quantity = quantity
        persistDemo()
        previewData()
      } else {
        if (quantity <= 0) await api.removeCart(item.dishId)
        else await api.cartQuantity(item.dishId, quantity)
        state.cart = await api.cart()
      }
    })
  }
  async function checkout(form) {
    return perform(async () => {
      if (!state.cart.length) throw new Error('先选一道喜欢的菜吧。')
      if (state.cart.some((i) => i.dishStatus !== 1 || i.quantity > i.dishStock))
        throw new Error('部分菜品库存或上架状态有变化，请先调整购物车。')
      if (form.pickupType === 'DELIVERY' && !form.addressId) throw new Error('请选择收餐地址。')
      // itemIds are CART ROW IDs, never dish IDs. Prices are calculated by the backend.
      const body = {
        itemIds: state.cart.map((i) => i.id),
        pickupType: form.pickupType,
        addressId: form.pickupType === 'DELIVERY' ? Number(form.addressId) : null,
        remark: form.remark.trim()
      }
      if (state.preview) {
        const now = new Date().toISOString()
        demo.orders.unshift({
          id: Date.now(),
          orderNo: `DEMO${Date.now()}`,
          userId: 9001,
          stallId: 1,
          totalAmount: cartTotal.value,
          payAmount: 0,
          status: 'WAIT_PAY',
          deliveryType: body.pickupType,
          addressId: body.addressId,
          remark: body.remark,
          createdAt: now,
          orderItems: state.cart.map((i) => ({
            dishId: i.dishId,
            dishName: i.dishName,
            price: i.dishPrice,
            quantity: i.quantity
          })),
          statusLogs: [{ newStatus: 'WAIT_PAY', createdAt: now }]
        })
        demo.cart = []
        persistDemo()
        previewData()
      } else {
        const order = await api.createOrder(body)
        state.orders.unshift(order)
        state.cart = []
      }
      state.checkoutOpen = false
      state.cartOpen = false
      navigate('orders')
    }, '订单已创建，请点击“模拟支付”完成付款。')
  }
  function updatePreviewOrder(order, status) {
    const target = demo.orders.find((o) => o.id === order.id)
    target.statusLogs.push({
      oldStatus: target.status,
      newStatus: status,
      createdAt: new Date().toISOString()
    })
    target.status = status
    persistDemo()
    previewData()
  }
  function orderAction(order, action, status) {
    return perform(
      async () => {
        if (state.preview) {
          if (action === 'pay') {
            if (order.status !== 'WAIT_PAY') throw new Error('只有待支付订单可以付款。')
            const target = demo.orders.find((o) => o.id === order.id)
            for (const item of target.orderItems) {
              const dish = demo.dishes.find((d) => d.id === item.dishId)
              if (!dish || dish.stock < item.quantity || dish.status !== 1)
                throw new Error('菜品已售罄或下架，请取消订单重新选购。')
            }
            target.orderItems.forEach((item) => {
              demo.dishes.find((d) => d.id === item.dishId).stock -= item.quantity
            })
            target.payAmount = target.totalAmount
            updatePreviewOrder(order, 'WAIT_ACCEPT')
          } else
            updatePreviewOrder(
              order,
              action === 'cancel' ? 'CANCELLED' : action === 'confirm' ? 'COMPLETED' : status
            )
        } else {
          if (action === 'pay') await api.pay(order.id)
          else if (action === 'cancel') await api.cancel(order.id)
          else if (action === 'confirm') await api.confirm(order.id, order.deliveryType)
          else await api.orderStatus(order.id, status)
          await loadPrivate()
        }
        if (state.detailOpen) await showDetail(order)
      },
      action === 'pay' ? '模拟支付成功，食堂正在等待接单。' : '订单状态已更新'
    )
  }
  async function showDetail(order) {
    state.detail = null
    state.detailOpen = true
    try {
      if (state.preview) {
        const target = demo.orders.find((o) => o.id === order.id)
        state.detail = structuredClone({
          order: target,
          orderItems: target.orderItems,
          statusLogs: target.statusLogs,
          address: demo.addresses.find((a) => a.id === target.addressId)
        })
      } else state.detail = await api.orderDetail(order.id, merchant.value)
    } catch (error) {
      state.detailOpen = false
      report(error)
    }
  }
  function saveDish(form) {
    return perform(async () => {
      const dish = {
        ...form,
        name: form.name.trim(),
        price: Number(form.price),
        stock: Number(form.stock),
        categoryId: Number(form.categoryId),
        status: Number(form.status)
      }
      if (
        !dish.name ||
        !Number.isFinite(dish.price) ||
        dish.price <= 0 ||
        !Number.isInteger(dish.stock) ||
        dish.stock < 0 ||
        !dish.categoryId
      )
        throw new Error('请填写菜品名称、有效的价格、整数库存和分类。')
      if (state.preview) {
        if (dish.id)
          demo.dishes = demo.dishes.map((d) => (d.id === dish.id ? { ...d, ...dish } : d))
        else demo.dishes.unshift({ ...dish, id: Date.now(), stallId: 1 })
        persistDemo()
        previewData()
      } else {
        await api.saveDish(dish)
        state.dishes = await api.merchantDishes()
      }
      state.dishModal = false
    }, '菜品已保存')
  }
  function toggleDish(dish) {
    return perform(async () => {
      const status = dish.status === 1 ? 0 : 1
      if (state.preview) {
        demo.dishes.find((d) => d.id === dish.id).status = status
        persistDemo()
        previewData()
      } else {
        await api.dishStatus(dish.id, status)
        dish.status = status
      }
    }, '菜品上架状态已更新')
  }
  function addCategory(name) {
    return perform(async () => {
      if (!name.trim()) throw new Error('请填写分类名称。')
      if (state.preview) {
        demo.categories.push({ id: Date.now(), name: name.trim(), stallId: 1 })
        persistDemo()
        previewData()
      } else {
        await api.saveCategory(name.trim())
        state.categories = await api.merchantCategories()
      }
    }, '分类已添加')
  }
  function saveAddress(form) {
    return perform(async () => {
      const body = { ...form, isDefault: form.isDefault ? 1 : 0 }
      if (state.preview) {
        if (body.isDefault)
          demo.addresses.forEach((a) => {
            a.isDefault = 0
          })
        if (body.id) demo.addresses = demo.addresses.map((a) => (a.id === body.id ? body : a))
        else demo.addresses.push({ ...body, id: Date.now(), status: 1 })
        persistDemo()
        previewData()
      } else {
        await api.saveAddress(body)
        state.addresses = await api.addresses()
      }
      state.addressModal = false
    }, '地址已保存')
  }
  function deleteAddress(id) {
    return perform(async () => {
      if (state.preview) {
        demo.addresses = demo.addresses.filter((a) => a.id !== id)
        persistDemo()
        previewData()
      } else {
        await api.deleteAddress(id)
        state.addresses = await api.addresses()
      }
    }, '地址已删除')
  }
  function saveProfile(form) {
    return perform(async () => {
      if (state.preview) Object.assign(state.user, form)
      else state.user = await api.profile(form)
    }, '个人资料已更新')
  }
  function savePassword(form) {
    return perform(async () => {
      if (state.preview) throw new Error('体验账号无需修改密码，请登录真实账号后操作。')
      await api.password({ oldPassword: form.oldPassword, newPassword: form.newPassword })
      clearSession()
    }, '密码已修改，请重新登录。')
  }
  function saveStall(form) {
    return perform(async () => {
      if (state.preview) {
        Object.assign(demo.stall, form)
        persistDemo()
        previewData()
      } else {
        await api.saveStall(form)
        state.stall = await api.stall()
      }
    }, '店铺信息已保存')
  }
  function toggleStall() {
    return perform(async () => {
      const status = state.stall.status === 1 ? 0 : 1
      if (state.preview) {
        demo.stall.status = status
        persistDemo()
        previewData()
      } else {
        await api.stallStatus(status)
        state.stall.status = status
      }
    }, '营业状态已更新')
  }
  function saveReview(form) {
    return perform(async () => {
      const body = {
        orderId: state.reviewOrder.id,
        rating: Number(form.rating),
        content: form.content.trim()
      }
      if (state.preview) {
        demo.reviews.unshift({ ...body, id: Date.now(), createdAt: new Date().toISOString() })
        persistDemo()
        previewData()
      } else {
        await api.review(body)
        state.reviews = await api.reviews()
      }
      state.reviewOrder = null
    }, '感谢评价，愿你每一餐都开心。')
  }
  async function init() {
    window.addEventListener('hashchange', handleHash)
    const role = sessionStorage.getItem('canteen-preview-role')
    const hash = window.location.hash
    try {
      if (role === 'STUDENT' || role === 'MERCHANT') enterPreview(role)
      else {
        try {
          state.user = await api.me()
        } catch (error) {
          if (error.status !== 401)
            state.authError = '登录真实账号前，请先启动 Spring Boot 后端；也可以先体验页面。'
        }
        if (state.user) {
          readFavorites()
          navigate(merchant.value ? 'overview' : 'home')
          await refresh()
        }
      }
      if (state.user && hash.includes(merchant.value ? '/merchant/' : '/student/')) {
        window.location.hash = hash
        handleHash()
      }
    } finally {
      state.booting = false
    }
  }
  const categoryName = (dish) =>
    state.categories.find((c) => c.id === dish.categoryId)?.name || '食堂精选'
  const stallName = (id) =>
    state.stalls.find((s) => s.id === id)?.name || state.stall?.name || '校园食堂'
  function openAddress(address = null) {
    state.editingAddress = address
    state.addressModal = true
  }
  function openDish(dish = null) {
    state.editingDish = dish
    state.dishModal = true
  }
  function browseMenu() {
    state.cartOpen = false
    navigate('menu')
  }
  return Object.assign(state, {
    merchant,
    cartCount,
    cartTotal,
    init,
    navigate,
    refresh,
    login,
    register,
    enterPreview,
    logout,
    favorite,
    addCart,
    changeQuantity,
    checkout,
    orderAction,
    showDetail,
    saveDish,
    toggleDish,
    addCategory,
    saveAddress,
    deleteAddress,
    saveProfile,
    savePassword,
    saveStall,
    toggleStall,
    saveReview,
    categoryName,
    stallName,
    openAddress,
    openDish,
    browseMenu
  })
}
