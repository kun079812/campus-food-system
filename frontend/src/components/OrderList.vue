<script setup>
import { computed, inject, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import UiIcon from './UiIcon.vue'
import { money, dateTime, statusName, nextOrderStatus } from '../services/format'
const props = defineProps({ compact: Boolean, merchant: Boolean })
const app = inject('campus')
const filter = ref('ALL')
const tabs = computed(() =>
  props.merchant
    ? [
        { code: 'ALL', name: '全部订单' },
        { code: 'WAIT_ACCEPT', name: '待接单' },
        { code: 'MAKING', name: '制作中' },
        { code: 'READY_PICKUP', name: '待取餐' },
        { code: 'DELIVERING', name: '配送中' },
        { code: 'COMPLETED', name: '已完成' }
      ]
    : [
        { code: 'ALL', name: '全部订单' },
        { code: 'WAIT_PAY', name: '待支付' },
        { code: 'WAIT_ACCEPT', name: '待接单' },
        { code: 'MAKING', name: '制作中' },
        { code: 'READY_PICKUP', name: '待取餐' },
        { code: 'DELIVERING', name: '配送中' },
        { code: 'DELIVERED', name: '已送达' },
        { code: 'COMPLETED', name: '已完成' },
        { code: 'CANCELLED', name: '已取消' }
      ]
)
const visible = computed(() =>
  props.compact
    ? app.orders.slice(0, 4)
    : app.orders.filter((o) => filter.value === 'ALL' || o.status === filter.value)
)
async function cancel(order) {
  try {
    await ElMessageBox.confirm('确定取消这笔尚未支付的订单吗？', '取消订单', {
      confirmButtonText: '确认取消',
      cancelButtonText: '再想想',
      type: 'warning'
    })
    await app.orderAction(order, 'cancel')
  } catch {
    /* The user kept the order. */
  }
}
defineExpose({
  setFilter: (value) => {
    filter.value = value
  }
})
</script>

<template>
  <div class="orders-section">
    <div v-if="!compact" class="order-filter" aria-label="订单状态筛选">
      <button
        v-for="tab in tabs"
        :key="tab.code"
        :class="{ active: filter === tab.code }"
        @click="filter = tab.code"
      >
        {{ tab.name
        }}<span v-if="tab.code !== 'ALL' && app.orders.some((o) => o.status === tab.code)">{{
          app.orders.filter((o) => o.status === tab.code).length
        }}</span>
      </button>
    </div>
    <div v-if="!visible.length" class="empty-state" :class="{ compact }">
      <UiIcon name="orders" :size="42" />
      <h3>{{ compact ? '还没有订单' : '这里暂时没有订单' }}</h3>
      <p>{{ merchant ? '新订单到来后，会显示在这里。' : '去挑一份喜欢的美味吧。' }}</p>
      <button v-if="!merchant" class="text-button" @click="app.navigate('menu')">
        去逛逛 <UiIcon name="arrow" :size="16" />
      </button>
    </div>
    <div v-else :class="compact ? 'mini-order-list' : 'full-order-list'">
      <article v-for="order in visible" :key="order.id" class="order-entry" :class="{ compact }">
        <div class="order-main">
          <span v-if="!compact" class="order-icon"
            ><UiIcon :name="order.deliveryType === 'DELIVERY' ? 'truck' : 'bag'" :size="25"
          /></span>
          <div>
            <strong>{{
              compact
                ? order.orderItems?.map((i) => i.dishName).join('、') || `食堂订单 #${order.id}`
                : app.stallName(order.stallId)
            }}</strong
            ><small>{{
              compact
                ? dateTime(order.createdAt)
                : `${order.orderNo || '#' + order.id} · ${dateTime(order.createdAt)}`
            }}</small>
          </div>
          <span class="status-pill" :class="order.status">{{ statusName(order.status) }}</span>
        </div>
        <div class="order-meta">
          <span v-if="!compact"
            >{{ order.deliveryType === 'DELIVERY' ? '送餐到指定地址' : '食堂自取'
            }}{{ order.remark ? ' · ' + order.remark : '' }}</span
          ><strong>¥{{ money(order.totalAmount) }}</strong
          ><button
            v-if="compact"
            class="text-button"
            :aria-label="`查看订单 ${order.id}`"
            @click="app.showDetail(order)"
          >
            <UiIcon name="chevron" :size="17" />
          </button>
        </div>
        <div v-if="!compact" class="order-actions">
          <button class="soft-button" @click="app.showDetail(order)">订单详情</button
          ><template v-if="!merchant"
            ><button
              v-if="order.status === 'WAIT_PAY'"
              class="text-button muted"
              :disabled="app.busy"
              @click="cancel(order)"
            >
              取消订单</button
            ><button
              v-if="order.status === 'WAIT_PAY'"
              class="primary-button small-button"
              :disabled="app.busy"
              @click="app.orderAction(order, 'pay')"
            >
              模拟支付 ¥{{ money(order.totalAmount) }}</button
            ><button
              v-if="['READY_PICKUP', 'DELIVERED'].includes(order.status)"
              class="primary-button small-button"
              :disabled="app.busy"
              @click="app.orderAction(order, 'confirm')"
            >
              {{ order.status === 'READY_PICKUP' ? '确认取餐' : '确认收餐' }}</button
            ><button
              v-if="
                order.status === 'COMPLETED' && !app.reviews.some((r) => r.orderId === order.id)
              "
              class="soft-button"
              @click="app.reviewOrder = order"
            >
              评价这餐
            </button></template
          ><button
            v-else-if="nextOrderStatus(order)"
            class="primary-button small-button"
            :disabled="app.busy"
            @click="app.orderAction(order, 'status', nextOrderStatus(order).code)"
          >
            {{ nextOrderStatus(order).label }}
          </button>
        </div>
      </article>
    </div>
  </div>
</template>
