<script setup>
import { computed, inject, reactive, ref, watch } from 'vue'
import { ElMessageBox } from 'element-plus'
import UiIcon from '../components/UiIcon.vue'
import FoodArt from '../components/FoodArt.vue'
import OrderList from '../components/OrderList.vue'
import { money, dateTime } from '../services/format'
const app = inject('campus')
const nav = [
  { key: 'overview', label: '商家首页', icon: 'home' },
  { key: 'dishes', label: '菜品管理', icon: 'orders' },
  { key: 'orders', label: '订单管理', icon: 'bag' },
  { key: 'statistics', label: '数据统计', icon: 'chart' },
  { key: 'settings', label: '店铺设置', icon: 'settings' },
  { key: 'reviews', label: '顾客评价', icon: 'chat' }
]
const keyword = ref('')
const dishFilter = ref('all')
const form = reactive({ name: '', businessHours: '' })
watch(
  () => app.stall,
  (stall) => {
    if (stall) Object.assign(form, { name: stall.name, businessHours: stall.businessHours })
  },
  { immediate: true }
)
const dishes = computed(() =>
  app.dishes.filter(
    (d) =>
      d.name.includes(keyword.value) &&
      (dishFilter.value === 'all' || d.status === Number(dishFilter.value))
  )
)
const maxSales = computed(() => Math.max(1, ...app.sales.map((item) => item.salesQuantity)))
const totalSales = computed(() =>
  app.sales.reduce((sum, item) => sum + Number(item.salesAmount || 0), 0)
)
const date = new Intl.DateTimeFormat('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long'
}).format(new Date())
function newDish() {
  app.editingDish = null
  app.dishModal = true
}
async function newCategory() {
  try {
    const { value } = await ElMessageBox.prompt('例如：盖饭、面食、饮品', '添加菜品分类', {
      confirmButtonText: '添加分类',
      cancelButtonText: '取消',
      inputValidator: (value) => Boolean(value?.trim()) || '请输入分类名称'
    })
    await app.addCategory(value)
  } catch {
    /* Cancelled by user. */
  }
}
</script>

<template>
  <div class="dashboard-layout merchant-layout">
    <aside class="side-panel merchant-sidebar">
      <nav aria-label="商家导航">
        <button
          v-for="item in nav"
          :key="item.key"
          :class="{ active: app.page === item.key }"
          @click="app.navigate(item.key)"
        >
          <UiIcon :name="item.icon" :size="20" />{{ item.label
          }}<i v-if="item.key === 'orders' && app.summary.pendingOrderCount" class="side-count">{{
            app.summary.pendingOrderCount
          }}</i>
        </button>
      </nav>
      <div class="sidebar-bottom">
        <UiIcon name="leaf" :size="30" />
        <p>用心做饭，<br />温暖校园的每一天。</p>
      </div>
    </aside>
    <div class="dashboard-content">
      <template v-if="app.page === 'overview'"
        ><div class="page-heading merchant-greeting">
          <div>
            <p class="eyebrow">A GOOD DAY STARTS HERE</p>
            <h1>你好，{{ app.stall?.name || app.user.realName || '食堂伙伴' }}！</h1>
            <p>用美味连接校园，让每一餐都更温暖。</p>
          </div>
          <span class="date-label">{{ date }}</span>
        </div>
        <div class="merchant-stats">
          <article class="panel">
            <span class="stat-icon sky"><UiIcon name="orders" :size="32" /></span>
            <div>
              <span>今日订单</span
              ><strong>{{ app.summary.todayOrderCount ?? '—' }}<small>单</small></strong>
            </div>
          </article>
          <article class="panel">
            <span class="stat-icon rose"><UiIcon name="wallet" :size="32" /></span>
            <div>
              <span>今日营业额</span><strong>¥{{ money(app.summary.todaySales) }}</strong>
            </div>
          </article>
          <article class="panel">
            <span class="stat-icon peach"><UiIcon name="bag" :size="32" /></span>
            <div>
              <span>待处理订单</span
              ><strong>{{ app.summary.pendingOrderCount ?? '—' }}<small>单</small></strong>
            </div>
          </article>
        </div>
        <div class="quick-actions">
          <button class="mint" :disabled="!app.stall" @click="newDish">
            <UiIcon name="plus" :size="21" />发布新菜品</button
          ><button class="sky" @click="app.navigate('dishes')">
            <UiIcon name="orders" :size="21" />管理菜品</button
          ><button class="sky" @click="app.navigate('orders')">
            <UiIcon name="bag" :size="21" />查看订单</button
          ><button class="peach" @click="app.navigate('statistics')">
            <UiIcon name="chart" :size="21" />数据统计
          </button>
        </div>
        <div class="merchant-panels">
          <section class="panel">
            <div class="panel-heading">
              <h2>最新订单</h2>
              <button class="text-button muted" @click="app.navigate('orders')">
                查看全部 <UiIcon name="arrow" :size="16" />
              </button>
            </div>
            <OrderList compact merchant />
          </section>
          <section class="panel business-panel">
            <div class="panel-heading">
              <h2><UiIcon name="store" :size="20" />营业提醒</h2>
            </div>
            <div class="business-note mint">
              <UiIcon name="leaf" :size="36" />
              <h3>
                {{ app.stall?.status === 1 ? '今天也要元气满满' : '休息一下，准备下一份美味' }}
              </h3>
              <p>
                在售菜品 {{ app.dishes.filter((d) => d.status === 1).length }} 道 ·
                认真对待每一份期待
              </p>
              <p>营业时间：{{ app.stall?.businessHours || '待设置' }}</p>
              <span class="open-indicator" :class="{ closed: app.stall?.status !== 1 }">{{
                app.stall?.status === 1 ? '正在营业' : '休息中'
              }}</span
              ><button class="white-button" @click="app.navigate('settings')">
                管理营业信息 <UiIcon name="arrow" :size="15" />
              </button>
            </div>
            <p class="merchant-note">新订单来了，记得及时接单哦。</p>
          </section>
        </div>
      </template>
      <template v-else-if="app.page === 'dishes'"
        ><div class="page-heading">
          <div>
            <p class="eyebrow">YOUR DELICIOUS CREATIONS</p>
            <h1>菜品管理</h1>
            <p>把你的拿手好菜，分享给校园里的每个人。</p>
          </div>
          <div class="row-actions">
            <button class="soft-button" :disabled="!app.stall" @click="newCategory">添加分类</button
            ><button class="primary-button small-button" :disabled="!app.stall" @click="newDish">
              <UiIcon name="plus" :size="18" />发布菜品
            </button>
          </div>
        </div>
        <div class="table-toolbar">
          <label class="inline-search"
            ><UiIcon name="search" :size="19" /><input
              v-model="keyword"
              placeholder="搜索菜品名称"
              aria-label="搜索商家菜品" /></label
          ><select v-model="dishFilter" aria-label="上架状态筛选">
            <option value="all">全部状态</option>
            <option value="1">已上架</option>
            <option value="0">已下架</option></select
          ><span>{{ dishes.length }} 道菜品</span>
        </div>
        <div class="panel table-scroll">
          <table class="data-table">
            <thead>
              <tr>
                <th>菜品</th>
                <th>分类</th>
                <th>单价</th>
                <th>库存</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="dish in dishes" :key="dish.id">
                <td>
                  <div class="table-dish">
                    <FoodArt :name="dish.name" :image="dish.imageUrl" :variant="dish.id" />
                    <div>
                      <strong>{{ dish.name }}</strong
                      ><small>{{ dish.description || '新鲜制作的校园好味道' }}</small>
                    </div>
                  </div>
                </td>
                <td>{{ app.categoryName(dish) }}</td>
                <td>
                  <strong>¥{{ money(dish.price) }}</strong>
                </td>
                <td>{{ dish.stock }}</td>
                <td>
                  <span
                    class="status-pill"
                    :class="dish.status === 1 ? 'COMPLETED' : 'CANCELLED'"
                    >{{ dish.status === 1 ? '已上架' : '已下架' }}</span
                  >
                </td>
                <td>
                  <div class="row-actions">
                    <button class="text-button" @click="app.openDish(dish)">编辑</button
                    ><button
                      class="text-button"
                      :class="{ muted: dish.status === 1 }"
                      :disabled="app.busy"
                      @click="app.toggleDish(dish)"
                    >
                      {{ dish.status === 1 ? '下架' : '上架' }}
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="!dishes.length" class="empty-state">
            <UiIcon name="bowl" :size="42" />
            <h3>还没有菜品</h3>
            <p>先添加分类，再发布你的第一道拿手菜。</p>
          </div>
        </div></template
      >
      <template v-else-if="app.page === 'orders'"
        ><div class="page-heading">
          <div>
            <p class="eyebrow">EVERY ORDER MATTERS</p>
            <h1>订单管理</h1>
            <p>接单、制作、出餐，每一步都有条不紊。</p>
          </div>
          <button class="soft-button" @click="app.refresh">
            <UiIcon name="refresh" :size="17" />刷新订单
          </button>
        </div>
        <OrderList merchant
      /></template>
      <template v-else-if="app.page === 'statistics'"
        ><div class="page-heading">
          <div>
            <p class="eyebrow">SMALL STEPS, GOOD GROWTH</p>
            <h1>经营数据</h1>
            <p>从每一道好菜，看见用心的回报。</p>
          </div>
          <button class="soft-button" @click="app.refresh">
            <UiIcon name="refresh" :size="17" />刷新数据
          </button>
        </div>
        <div class="merchant-stats">
          <article class="panel">
            <span class="stat-icon mint"><UiIcon name="wallet" :size="29" /></span>
            <div>
              <span>菜品累计销售额</span><strong>¥{{ money(totalSales) }}</strong>
            </div>
          </article>
          <article class="panel">
            <span class="stat-icon sky"><UiIcon name="bag" :size="29" /></span>
            <div>
              <span>菜品累计销量</span
              ><strong
                >{{ app.sales.reduce((s, i) => s + i.salesQuantity, 0) }}<small>份</small></strong
              >
            </div>
          </article>
          <article class="panel">
            <span class="stat-icon peach"><UiIcon name="bowl" :size="29" /></span>
            <div>
              <span>在售菜品</span
              ><strong
                >{{ app.dishes.filter((d) => d.status === 1).length }}<small>道</small></strong
              >
            </div>
          </article>
        </div>
        <section class="panel sales-panel">
          <div class="panel-heading">
            <h2>菜品销量排行</h2>
            <span>已支付订单统计</span>
          </div>
          <div v-if="app.sales.length" class="sales-chart">
            <div
              v-for="(item, index) in [...app.sales].sort(
                (a, b) => b.salesQuantity - a.salesQuantity
              )"
              :key="item.dishId"
              class="sales-row"
            >
              <span class="sales-rank">{{ String(index + 1).padStart(2, '0') }}</span
              ><strong>{{ item.dishName }}</strong>
              <div class="bar-track">
                <span :style="{ width: `${(item.salesQuantity / maxSales) * 100}%` }"></span>
              </div>
              <span>{{ item.salesQuantity }} 份</span
              ><strong>¥{{ money(item.salesAmount) }}</strong>
            </div>
          </div>
          <div v-else class="empty-state">
            <UiIcon name="chart" :size="42" />
            <h3>美味的成绩，正在积累</h3>
            <p>有已支付订单后，这里会显示菜品销售统计。</p>
          </div>
        </section></template
      >
      <template v-else-if="app.page === 'reviews'"
        ><div class="page-heading">
          <div>
            <p class="eyebrow">WORDS FROM YOUR GUESTS</p>
            <h1>顾客评价</h1>
            <p>认真倾听，让下一餐更好。</p>
          </div>
        </div>
        <div v-if="app.reviews.length" class="review-grid">
          <article v-for="review in app.reviews" :key="review.id" class="panel review-card">
            <div>
              <span class="review-stars"
                >{{ '★'.repeat(review.rating) }}{{ '☆'.repeat(5 - review.rating) }}</span
              ><small>{{ dateTime(review.createdAt) }}</small>
            </div>
            <p>{{ review.content }}</p>
            <small>来自订单 #{{ review.orderId }}</small>
          </article>
        </div>
        <div v-else class="empty-state panel">
          <UiIcon name="chat" :size="44" />
          <h3>期待第一份好评</h3>
          <p>同学们的用餐感受会显示在这里。</p>
        </div></template
      >
      <template v-else
        ><div class="page-heading">
          <div>
            <p class="eyebrow">YOUR LITTLE KITCHEN</p>
            <h1>店铺设置</h1>
            <p>为你的校园小店，添一点温暖。</p>
          </div>
        </div>
        <div class="settings-grid">
          <form class="panel settings-form" @submit.prevent="app.saveStall(form)">
            <h2>营业信息</h2>
            <label
              >店铺名称<input
                v-model="form.name"
                required
                maxlength="60"
                placeholder="例如：第一食堂 · 暖心小厨" /></label
            ><label
              >营业时间<input
                v-model="form.businessHours"
                required
                maxlength="60"
                placeholder="例如：07:00 - 21:00" /></label
            ><button class="primary-button small-button" :disabled="app.busy || !app.stall">
              保存店铺信息
            </button>
          </form>
          <section class="panel settings-form">
            <h2>营业状态</h2>
            <p>休息时，店铺菜品将不向学生展示。</p>
            <div class="store-state">
              <span class="stat-icon mint"><UiIcon name="store" :size="29" /></span
              ><strong>{{ app.stall?.status === 1 ? '正在营业' : '休息中' }}</strong>
            </div>
            <button class="soft-button" :disabled="app.busy || !app.stall" @click="app.toggleStall">
              {{ app.stall?.status === 1 ? '设为休息' : '恢复营业' }}
            </button>
            <p class="form-note">已接到的订单仍需及时处理。</p>
          </section>
        </div></template
      >
    </div>
  </div>
</template>
