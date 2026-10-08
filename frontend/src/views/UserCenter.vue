<script setup>
import { computed, inject, nextTick, reactive, ref, watch } from 'vue'
import { ElMessageBox } from 'element-plus'
import UiIcon from '../components/UiIcon.vue'
import FoodArt from '../components/FoodArt.vue'
import DishCard from '../components/DishCard.vue'
import OrderList from '../components/OrderList.vue'
const app = inject('campus')
const orderList = ref(null)
const nav = [
  { key: 'profile', label: '我的首页', icon: 'home' },
  { key: 'orders', label: '我的订单', icon: 'orders' },
  { key: 'addresses', label: '我的地址', icon: 'pin' },
  { key: 'favorites', label: '我的收藏', icon: 'heart' },
  { key: 'settings', label: '个人设置', icon: 'settings' }
]
const statuses = [
  { code: 'WAIT_PAY', label: '待支付', icon: 'wallet', color: 'peach' },
  { code: 'READY_PICKUP', label: '待取餐', icon: 'bag', color: 'mint' },
  { code: 'DELIVERING', label: '配送中', icon: 'truck', color: 'sky' },
  { code: 'COMPLETED', label: '已完成', icon: 'check', color: 'butter' }
]
const profile = reactive({ realName: app.user.realName || '', phone: app.user.phone || '' })
const password = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const passwordError = ref('')
const favorites = computed(() => app.dishes.filter((d) => app.favorites.includes(d.id)))
const dateLabel = new Intl.DateTimeFormat('zh-CN', {
  month: 'long',
  day: 'numeric',
  weekday: 'long'
}).format(new Date())
async function filterOrders(code) {
  app.navigate('orders')
  await nextTick()
  orderList.value?.setFilter(code)
}
async function deleteAddress(address) {
  try {
    await ElMessageBox.confirm(`删除“${address.detail}”这个地址？`, '删除地址', {
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    await app.deleteAddress(address.id)
  } catch {
    /* No deletion on dismiss. */
  }
}
async function changePassword() {
  passwordError.value = ''
  if (password.newPassword !== password.confirmPassword) {
    passwordError.value = '两次输入的新密码不一致。'
    return
  }
  await app.savePassword(password)
}
watch(
  () => app.user,
  (user) => {
    if (user) Object.assign(profile, { realName: user.realName || '', phone: user.phone || '' })
  }
)
</script>

<template>
  <div class="dashboard-layout">
    <aside class="side-panel student-sidebar">
      <div class="sidebar-profile">
        <span class="avatar large"><UiIcon name="user" :size="32" /></span
        ><strong>{{ app.user.realName || '同学' }}，你好！</strong
        ><small>账号：{{ app.user.username }}</small
        ><span class="student-tag"><UiIcon name="star" :size="12" /> 校园食客</span>
      </div>
      <nav aria-label="个人中心导航">
        <button
          v-for="item in nav"
          :key="item.key"
          :class="{ active: app.page === item.key }"
          @click="app.navigate(item.key)"
        >
          <UiIcon :name="item.icon" :size="19" />{{ item.label }}</button
        ><button @click="app.helpOpen = true"><UiIcon name="chat" :size="19" />帮助中心</button>
      </nav>
      <div class="sidebar-bottom">
        <UiIcon name="leaf" :size="30" />
        <p>再忙，也记得<br />好好吃饭。</p>
      </div>
    </aside>
    <div class="dashboard-content">
      <template v-if="app.page === 'profile'">
        <section class="personal-welcome">
          <div>
            <p class="eyebrow">A LITTLE HAPPINESS EVERY DAY</p>
            <h1>每一餐，都是小确幸。</h1>
            <p>今天的好心情，从一份热腾腾的饭开始。</p>
            <button class="text-button" @click="app.navigate('menu')">
              去发现美味 <UiIcon name="arrow" :size="18" />
            </button>
          </div>
          <FoodArt name="红烧肉饭" :variant="0" />
          <div class="date-card">
            <strong>{{ dateLabel }}</strong
            ><UiIcon name="sun" :size="32" /><span>无论晴雨<br />都要好好吃饭</span>
          </div>
        </section>
        <div class="order-status-grid">
          <button v-for="item in statuses" :key="item.code" @click="filterOrders(item.code)">
            <span class="stat-icon" :class="item.color"
              ><UiIcon :name="item.icon" :size="26" /></span
            ><span
              ><strong>{{ item.label }}</strong
              ><small
                >{{ app.orders.filter((o) => o.status === item.code).length }} 个订单</small
              ></span
            ><UiIcon name="chevron" :size="15" />
          </button>
        </div>
        <div class="personal-panels">
          <section class="panel recent-orders">
            <div class="panel-heading">
              <h2>最近订单</h2>
              <button class="text-button muted" @click="app.navigate('orders')">
                查看全部 <UiIcon name="arrow" :size="16" />
              </button>
            </div>
            <OrderList compact />
          </section>
          <div class="personal-sidecards">
            <button class="side-promo rose" @click="app.navigate('favorites')">
              <UiIcon name="heart" :size="39" />
              <div>
                <span>我的心动美食</span
                ><strong>{{ app.favorites.length }} <small>道收藏</small></strong>
                <p>把喜欢的味道悄悄收藏</p>
              </div></button
            ><button class="side-promo sky" @click="app.helpOpen = true">
              <UiIcon name="chat" :size="38" />
              <div>
                <strong>用餐小帮手</strong>
                <p>点餐、取餐，有问题看这里 <UiIcon name="arrow" :size="15" /></p>
              </div>
            </button>
          </div>
        </div>
      </template>
      <template v-else-if="app.page === 'orders'"
        ><div class="page-heading">
          <div>
            <p class="eyebrow">MY ORDERS</p>
            <h1>我的订单</h1>
            <p>每一份热爱，都在认真准备。</p>
          </div>
          <button class="soft-button" @click="app.refresh">
            <UiIcon name="refresh" :size="17" />刷新订单
          </button>
        </div>
        <OrderList ref="orderList"
      /></template>
      <template v-else-if="app.page === 'favorites'"
        ><div class="page-heading">
          <div>
            <p class="eyebrow">MY FAVORITES</p>
            <h1>我的心动美食</h1>
            <p>喜欢的味道，下次还想再来一份。收藏保存在当前浏览器中。</p>
          </div>
        </div>
        <div v-if="favorites.length" class="dish-grid favorites-grid">
          <DishCard v-for="dish in favorites" :key="dish.id" :dish="dish" />
        </div>
        <div v-else class="empty-state panel">
          <UiIcon name="heart" :size="50" />
          <h3>把喜欢的美味收藏起来</h3>
          <p>点击菜品卡片上的小爱心，就能在这里找到它。</p>
          <button class="primary-button small-button" @click="app.navigate('menu')">
            去发现美味
          </button>
        </div></template
      >
      <template v-else-if="app.page === 'addresses'"
        ><div class="page-heading">
          <div>
            <p class="eyebrow">MY ADDRESSES</p>
            <h1>我的收餐地址</h1>
            <p>让美味找到你。</p>
          </div>
          <button class="primary-button small-button" @click="app.openAddress()">
            <UiIcon name="plus" :size="18" />新增地址
          </button>
        </div>
        <div v-if="app.addresses.length" class="address-grid">
          <article v-for="address in app.addresses" :key="address.id" class="address-card panel">
            <span class="stat-icon sky"><UiIcon name="pin" :size="25" /></span>
            <h3>
              {{ address.contactName
              }}<span v-if="address.isDefault" class="status-pill COMPLETED">默认地址</span>
            </h3>
            <p>{{ address.phone }}</p>
            <strong>{{ address.detail }}</strong>
            <div>
              <button class="text-button" @click="app.openAddress(address)">
                <UiIcon name="edit" :size="16" />编辑</button
              ><button class="text-button muted" @click="deleteAddress(address)">
                <UiIcon name="trash" :size="16" />删除
              </button>
            </div>
          </article>
        </div>
        <div v-else class="empty-state panel">
          <UiIcon name="pin" :size="48" />
          <h3>还没有收餐地址</h3>
          <p>新增地址后，就可以选择送餐服务啦。</p>
        </div></template
      >
      <template v-else
        ><div class="page-heading">
          <div>
            <p class="eyebrow">MY PROFILE</p>
            <h1>个人设置</h1>
            <p>认真照顾每一个小细节。</p>
          </div>
        </div>
        <div class="settings-grid">
          <form class="panel settings-form" @submit.prevent="app.saveProfile(profile)">
            <h2>基本资料</h2>
            <label>账号<input :value="app.user.username" disabled /></label
            ><label
              >姓名<input
                v-model="profile.realName"
                required
                maxlength="50"
                autocomplete="name" /></label
            ><label
              >手机号码<input
                v-model="profile.phone"
                required
                pattern="1[0-9]{10}"
                maxlength="11"
                autocomplete="tel" /></label
            ><button class="primary-button small-button" :disabled="app.busy">保存资料</button>
          </form>
          <form class="panel settings-form" @submit.prevent="changePassword">
            <h2>修改密码</h2>
            <label
              >原密码<input
                v-model="password.oldPassword"
                required
                type="password"
                autocomplete="current-password" /></label
            ><label
              >新密码<input
                v-model="password.newPassword"
                required
                type="password"
                minlength="8"
                maxlength="72"
                autocomplete="new-password"
                placeholder="至少 8 位" /></label
            ><label
              >确认新密码<input
                v-model="password.confirmPassword"
                required
                type="password"
                minlength="8"
                maxlength="72"
                autocomplete="new-password"
            /></label>
            <p v-if="passwordError" class="form-error">{{ passwordError }}</p>
            <button class="soft-button" :disabled="app.busy">更新密码</button>
          </form>
        </div></template
      >
    </div>
  </div>
</template>
