<script setup>
import { onMounted, provide } from 'vue'
import { useCampus } from './services/useCampus'
import UiIcon from './components/UiIcon.vue'
import AuthView from './views/AuthView.vue'
import StudentHome from './views/StudentHome.vue'
import UserCenter from './views/UserCenter.vue'
import MerchantCenter from './views/MerchantCenter.vue'
import AppDialogs from './components/AppDialogs.vue'
import { money } from './services/format'

const app = useCampus()
provide('campus', app)
onMounted(app.init)
const studentNav = [
  { key: 'home', label: '首页' },
  { key: 'menu', label: '食堂菜单' },
  { key: 'orders', label: '我的订单' },
  { key: 'profile', label: '个人中心' }
]
const merchantNav = [
  { key: 'overview', label: '首页' },
  { key: 'dishes', label: '菜品管理' },
  { key: 'orders', label: '订单管理' },
  { key: 'statistics', label: '数据统计' },
  { key: 'settings', label: '店铺设置' }
]
</script>

<template>
  <div class="site-wrap" :class="{ 'is-auth': !app.user, 'is-merchant': app.merchant }">
    <header class="site-header">
      <button
        class="brand"
        aria-label="食堂小站首页"
        @click="app.user && app.navigate(app.merchant ? 'overview' : 'home')"
      >
        <span class="brand-symbol"><UiIcon name="chef" :size="39" /></span>
        <span>食堂小站<small>CAMPUS KITCHEN</small></span>
      </button>
      <span v-if="!app.user" class="brand-tagline">校园美食，触手可及</span>
      <span v-else-if="app.merchant" class="header-divider">商家中心</span>

      <nav v-if="app.user" class="top-nav" aria-label="主导航">
        <button
          v-for="item in app.merchant ? merchantNav : studentNav"
          :key="item.key"
          :class="{ active: app.page === item.key }"
          @click="app.navigate(item.key)"
        >
          {{ item.label }}
        </button>
      </nav>
      <nav v-else class="top-nav auth-nav" aria-label="网站导航">
        <span class="nav-current">首页</span><button @click="app.helpOpen = true">关于我们</button
        ><button @click="app.helpOpen = true">帮助中心</button>
      </nav>

      <div v-if="app.user" class="header-actions">
        <form
          v-if="!app.merchant"
          class="header-search"
          role="search"
          @submit.prevent="app.navigate('menu')"
        >
          <UiIcon name="search" :size="17" /><input
            v-model="app.keyword"
            aria-label="搜索菜品"
            placeholder="搜索菜品、食堂…"
          />
        </form>
        <button
          class="icon-button header-cart"
          :aria-label="app.merchant ? '查看待处理订单' : '打开购物车'"
          @click="app.merchant ? app.navigate('orders') : (app.cartOpen = true)"
        >
          <UiIcon :name="app.merchant ? 'bell' : 'cart'" :size="24" /><span
            v-if="app.merchant ? app.summary.pendingOrderCount : app.cartCount"
            class="count-dot"
            >{{ app.merchant ? app.summary.pendingOrderCount : app.cartCount }}</span
          >
        </button>
        <el-dropdown trigger="click">
          <button class="account-button" aria-label="账号菜单">
            <span class="avatar small" :class="{ peach: app.merchant }"
              ><UiIcon :name="app.merchant ? 'store' : 'user'" :size="21" /></span
            ><span class="account-name"
              >{{ app.user.realName || app.user.username
              }}<small>{{ app.merchant ? '商家账号' : '今天也要好好吃饭' }}</small></span
            ><UiIcon name="down" :size="14" />
          </button>
          <template #dropdown
            ><el-dropdown-menu
              ><el-dropdown-item @click="app.navigate(app.merchant ? 'settings' : 'profile')">{{
                app.merchant ? '店铺设置' : '个人中心'
              }}</el-dropdown-item
              ><el-dropdown-item divided @click="app.logout"
                >退出登录</el-dropdown-item
              ></el-dropdown-menu
            ></template
          >
        </el-dropdown>
      </div>
    </header>

    <div v-if="app.booting" class="boot-screen">
      <UiIcon name="chef" :size="54" />
      <p>正在为你准备校园美味…</p>
    </div>
    <template v-else>
      <AuthView v-if="!app.user" />
      <template v-else>
        <div v-if="app.preview" class="preview-notice">
          <UiIcon name="info" :size="16" /><span
            >页面体验 · 当前为本机示例数据，模拟付款不会扣款</span
          ><button @click="app.logout">登录真实账号 <UiIcon name="arrow" :size="15" /></button>
        </div>
        <div v-if="app.error" class="error-notice" role="alert">
          <span>{{ app.error }}</span
          ><button @click="app.refresh">重新加载</button>
        </div>
        <main v-loading="app.loading" class="main-content" :aria-busy="app.loading">
          <MerchantCenter v-if="app.merchant" />
          <StudentHome v-else-if="['home', 'menu'].includes(app.page)" />
          <UserCenter v-else />
        </main>
        <button
          v-if="!app.merchant && app.cartCount && ['home', 'menu', 'favorites'].includes(app.page)"
          class="floating-cart"
          @click="app.cartOpen = true"
        >
          <span class="floating-cart-icon"
            ><UiIcon name="bag" :size="24" /><i>{{ app.cartCount }}</i></span
          ><span
            >美味已装好 <strong>¥{{ money(app.cartTotal) }}</strong></span
          ><span class="floating-cart-cta">去结算 <UiIcon name="arrow" :size="19" /></span>
        </button>
      </template>
    </template>
    <footer class="site-footer">
      <span>食堂小站 · 把每一餐，过成小确幸</span><span>校园专属 <i>·</i> 用心做好每一餐</span>
    </footer>
    <AppDialogs />
  </div>
</template>
