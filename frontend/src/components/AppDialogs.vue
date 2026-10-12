<script setup>
import { inject, reactive, watch } from 'vue'
import UiIcon from './UiIcon.vue'
import FoodArt from './FoodArt.vue'
import { money, dateTime, statusName } from '../services/format'
const app = inject('campus')
const checkoutForm = reactive({ pickupType: 'PICKUP', addressId: '', remark: '' })
const dishForm = reactive({})
const addressForm = reactive({})
const reviewForm = reactive({ rating: 5, content: '' })
watch(
  () => app.addresses,
  (addresses) => {
    if (app.checkoutOpen && !addresses.some((a) => a.id === Number(checkoutForm.addressId))) {
      checkoutForm.addressId = addresses.find((a) => a.isDefault)?.id || addresses[0]?.id || ''
    }
  }
)
watch(
  () => app.checkoutOpen,
  (open) => {
    if (open) {
      checkoutForm.addressId =
        app.addresses.find((a) => a.isDefault)?.id || app.addresses[0]?.id || ''
      checkoutForm.remark = ''
    }
  }
)
watch(
  () => app.dishModal,
  (open) => {
    if (open) {
      for (const key of Object.keys(dishForm)) delete dishForm[key]
      Object.assign(
        dishForm,
        app.editingDish
          ? { ...app.editingDish }
          : {
              name: '',
              price: '',
              stock: 20,
              categoryId: app.categories[0]?.id || '',
              description: '',
              imageUrl: '',
              status: 1
            }
      )
    }
  }
)
watch(
  () => app.addressModal,
  (open) => {
    if (open) {
      for (const key of Object.keys(addressForm)) delete addressForm[key]
      Object.assign(
        addressForm,
        app.editingAddress
          ? { ...app.editingAddress, isDefault: Boolean(app.editingAddress.isDefault) }
          : {
              contactName: app.user?.realName || '',
              phone: app.user?.phone || '',
              detail: '',
              isDefault: app.addresses.length === 0
            }
      )
    }
  }
)
watch(
  () => app.reviewOrder,
  () => {
    reviewForm.rating = 5
    reviewForm.content = ''
  }
)
</script>

<template>
  <el-drawer
    v-model="app.cartOpen"
    title="我的美味购物车"
    size="450px"
    class="cart-drawer"
    :close-on-click-modal="!app.busy"
  >
    <div v-if="!app.cart.length" class="empty-state">
      <UiIcon name="bag" :size="62" />
      <h3>这里还空着呢</h3>
      <p>去选一份美味，犒劳认真生活的自己。</p>
      <button class="primary-button small-button" @click="app.browseMenu()">去逛食堂</button>
    </div>
    <template v-else
      ><p class="cart-note">
        <UiIcon name="info" :size="17" /> 一次结算同一档口的菜品，新鲜一起到。
      </p>
      <div class="cart-items">
        <article v-for="item in app.cart" :key="item.id" class="cart-item">
          <FoodArt :name="item.dishName" :image="item.dishImageUrl" :variant="item.dishId" />
          <div class="cart-item-info">
            <h3>{{ item.dishName }}</h3>
            <span v-if="item.dishStatus !== 1 || item.dishStock < item.quantity" class="form-error"
              >库存或上架状态已变化，请移除</span
            ><strong>¥{{ money(item.dishPrice) }}</strong>
            <div class="quantity-control">
              <button
                :disabled="app.busy"
                :aria-label="`减少${item.dishName}数量`"
                @click="app.changeQuantity(item, -1)"
              >
                <UiIcon name="minus" :size="15" /></button
              ><span>{{ item.quantity }}</span
              ><button
                :disabled="app.busy || item.quantity >= item.dishStock || item.dishStatus !== 1"
                :aria-label="`增加${item.dishName}数量`"
                @click="app.changeQuantity(item, 1)"
              >
                <UiIcon name="plus" :size="15" />
              </button>
            </div>
          </div>
          <button
            class="icon-button muted"
            :disabled="app.busy"
            :aria-label="`移除${item.dishName}`"
            @click="app.changeQuantity(item, -item.quantity)"
          >
            <UiIcon name="trash" :size="18" />
          </button>
        </article></div
    ></template>
    <template #footer
      ><div v-if="app.cart.length" class="cart-checkout">
        <div>
          <span>共 {{ app.cartCount }} 件，合计</span><strong>¥{{ money(app.cartTotal) }}</strong>
        </div>
        <button class="primary-button" :disabled="app.busy" @click="app.checkoutOpen = true">
          去结算 <UiIcon name="arrow" :size="18" />
        </button>
        <p>下一步选择取餐方式并创建订单</p>
      </div></template
    >
  </el-drawer>

  <el-dialog
    v-model="app.checkoutOpen"
    title="离美味，只差一步"
    width="530px"
    align-center
    :close-on-click-modal="false"
    class="campus-dialog"
  >
    <form class="dialog-form" @submit.prevent="app.checkout(checkoutForm)">
      <div class="checkout-items">
        <div v-for="item in app.cart" :key="item.id">
          <span
            >{{ item.dishName }} <small>× {{ item.quantity }}</small></span
          ><strong>¥{{ money(item.dishPrice * item.quantity) }}</strong>
        </div>
      </div>
      <label>取餐方式</label>
      <div class="delivery-options">
        <label :class="{ selected: checkoutForm.pickupType === 'PICKUP' }"
          ><input v-model="checkoutForm.pickupType" type="radio" value="PICKUP" /><UiIcon
            name="store"
            :size="24"
          /><span>食堂自取<small>出餐后到档口领取</small></span></label
        ><label :class="{ selected: checkoutForm.pickupType === 'DELIVERY' }"
          ><input v-model="checkoutForm.pickupType" type="radio" value="DELIVERY" /><UiIcon
            name="truck"
            :size="24"
          /><span>送餐到家<small>送至你的指定地址</small></span></label
        >
      </div>
      <label v-if="checkoutForm.pickupType === 'DELIVERY'"
        >收餐地址<select v-model="checkoutForm.addressId" aria-label="收餐地址" required>
          <option value="" disabled>请选择收餐地址</option>
          <option v-for="address in app.addresses" :key="address.id" :value="address.id">
            {{ address.contactName }} · {{ address.detail }}
          </option>
        </select></label
      ><button
        v-if="checkoutForm.pickupType === 'DELIVERY'"
        type="button"
        class="text-button"
        @click="app.openAddress()"
      >
        <UiIcon name="plus" :size="16" />添加收餐地址</button
      ><label
        >给食堂留句话 <small>选填</small
        ><textarea
          v-model="checkoutForm.remark"
          maxlength="255"
          rows="2"
          placeholder="例如：少辣，不要香菜"
        />
      </label>
      <div class="checkout-total">
        <span>应付金额</span><strong>¥{{ money(app.cartTotal) }}</strong>
      </div>
      <p class="form-note">本系统提供模拟支付，不会进行真实扣款。创建订单后可在“我的订单”付款。</p>
      <button class="primary-button full-width" :disabled="app.busy || !app.cart.length">
        {{ app.busy ? '正在创建…' : '确认创建订单' }} <UiIcon name="arrow" :size="18" />
      </button>
    </form>
  </el-dialog>

  <el-dialog
    v-model="app.detailOpen"
    title="这份美味的旅程"
    width="600px"
    align-center
    class="campus-dialog"
    ><div v-if="!app.detail" class="empty-state compact">正在读取订单…</div>
    <template v-else
      ><div class="detail-heading">
        <span class="stat-icon sky"><UiIcon name="bag" :size="27" /></span>
        <div>
          <strong>{{ app.detail.order.orderNo || `订单 #${app.detail.order.id}` }}</strong>
          <p>{{ dateTime(app.detail.order.createdAt) }}</p>
        </div>
        <span class="status-pill" :class="app.detail.order.status">{{
          statusName(app.detail.order.status)
        }}</span>
      </div>
      <div class="checkout-items">
        <div v-for="item in app.detail.orderItems" :key="item.id || item.dishId">
          <span
            >{{ item.dishName || `菜品 #${item.dishId}` }}
            <small>× {{ item.quantity }}</small></span
          ><strong>¥{{ money(item.price * item.quantity) }}</strong>
        </div>
      </div>
      <div class="detail-meta">
        <p>
          取餐方式：{{ app.detail.order.deliveryType === 'DELIVERY' ? '送餐配送' : '食堂自取' }}
        </p>
        <p v-if="app.detail.address">
          {{ app.detail.address.contactName }} · {{ app.detail.address.phone }}<br />{{
            app.detail.address.detail
          }}
        </p>
        <p v-if="app.detail.order.remark">备注：{{ app.detail.order.remark }}</p>
      </div>
      <div class="checkout-total">
        <span>订单金额</span><strong>¥{{ money(app.detail.order.totalAmount) }}</strong>
      </div>
      <ol class="order-timeline">
        <li v-for="(log, index) in app.detail.statusLogs" :key="log.id || index">
          <span>{{ statusName(log.newStatus) }}</span
          ><small>{{ dateTime(log.createdAt) }}</small>
        </li>
      </ol></template
    ></el-dialog
  >

  <el-dialog
    v-model="app.dishModal"
    :title="app.editingDish ? '让这道美味更完美' : '发布一道拿手好菜'"
    width="590px"
    align-center
    :close-on-click-modal="false"
    class="campus-dialog"
    ><form class="dialog-form" @submit.prevent="app.saveDish(dishForm)">
      <label
        >菜品名称<input
          v-model="dishForm.name"
          required
          maxlength="60"
          placeholder="例如：番茄鸡蛋盖饭"
      /></label>
      <div class="form-columns">
        <label
          >价格（元）<input
            v-model="dishForm.price"
            required
            type="number"
            min="0.01"
            max="99999"
            step="0.01"
            placeholder="12.80" /></label
        ><label
          >库存（份）<input
            v-model="dishForm.stock"
            required
            type="number"
            min="0"
            max="999999"
            step="1"
        /></label>
      </div>
      <div class="form-columns">
        <label
          >菜品分类<select v-model="dishForm.categoryId" required>
            <option value="" disabled>请选择分类</option>
            <option v-for="category in app.categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select></label
        >
        <p class="form-note">新菜品发布后立即上架。需要暂停售卖时，可在菜品列表点击“下架”。</p>
      </div>
      <p v-if="!app.categories.length" class="form-error">
        请先关闭窗口，在菜品管理中“添加分类”，再发布菜品。
      </p>
      <label
        >菜品介绍 <small>选填</small
        ><textarea
          v-model="dishForm.description"
          rows="2"
          maxlength="255"
          placeholder="介绍一下食材、口味或你的独家用心…"
        /></label
      ><label
        >图片链接 <small>选填；留空使用食物插画</small
        ><input
          v-model="dishForm.imageUrl"
          type="url"
          maxlength="500"
          placeholder="https://…" /></label
      ><button class="primary-button full-width" :disabled="app.busy || !app.categories.length">
        {{ app.busy ? '保存中…' : '保存菜品' }}<UiIcon name="check" :size="18" />
      </button></form
  ></el-dialog>

  <el-dialog
    v-model="app.addressModal"
    :title="app.editingAddress ? '编辑收餐地址' : '让美味找到你'"
    width="500px"
    align-center
    :close-on-click-modal="false"
    class="campus-dialog"
    ><form class="dialog-form" @submit.prevent="app.saveAddress(addressForm)">
      <label
        >收餐人<input
          v-model="addressForm.contactName"
          required
          maxlength="50"
          autocomplete="name"
          placeholder="怎么称呼你" /></label
      ><label
        >联系电话<input
          v-model="addressForm.phone"
          required
          pattern="1[0-9]{10}"
          maxlength="11"
          autocomplete="tel"
          placeholder="11 位手机号码" /></label
      ><label
        >详细地址<textarea
          v-model="addressForm.detail"
          required
          maxlength="200"
          rows="3"
          placeholder="例如：东区 3 号楼一层大厅"
        /></label
      ><label class="checkbox-line"
        ><input v-model="addressForm.isDefault" type="checkbox" /> 设为默认收餐地址</label
      ><button class="primary-button full-width" :disabled="app.busy">保存地址</button>
    </form></el-dialog
  >

  <el-dialog
    :model-value="Boolean(app.reviewOrder)"
    title="这餐，合你心意吗？"
    width="480px"
    align-center
    class="campus-dialog"
    @update:model-value="
      (value) => {
        if (!value) app.reviewOrder = null
      }
    "
    ><form class="dialog-form" @submit.prevent="app.saveReview(reviewForm)">
      <label
        >用餐评分<el-rate
          v-model="reviewForm.rating"
          :max="5"
          size="large"
          show-text
          :texts="['有待改进', '不太满意', '还不错', '很满意', '超喜欢']" /></label
      ><label
        >分享你的感受<textarea
          v-model="reviewForm.content"
          required
          maxlength="500"
          rows="4"
          placeholder="味道、分量、服务…说说你的真实感受吧"
        /></label
      ><button class="primary-button full-width" :disabled="app.busy || !reviewForm.rating">
        提交评价 <UiIcon name="heart" :size="18" />
      </button></form
  ></el-dialog>

  <el-dialog
    v-model="app.helpOpen"
    title="食堂小站 · 用餐小帮手"
    width="560px"
    align-center
    class="campus-dialog"
    ><div class="help-intro">
      <UiIcon name="chef" :size="47" />
      <h3>把每一餐，过成小确幸。</h3>
      <p>连接校园食堂与每一个认真生活的你。</p>
    </div>
    <el-collapse accordion
      ><el-collapse-item title="怎么点餐和付款？" name="1"
        ><p>
          选择学生登录，找到喜欢的菜品，点击“＋”加入购物车。结算时选择食堂自取或配送，创建订单后点击“模拟支付”。当前支付用于课程演示，不会扣除真实资金。
        </p></el-collapse-item
      ><el-collapse-item title="在哪里查看取餐进度？" name="2"
        ><p>
          在“我的订单”中查看状态。商家完成制作后，自取订单会显示“待取餐”；拿到餐后点击“确认取餐”。配送订单在送达后可确认收餐。
        </p></el-collapse-item
      ><el-collapse-item title="忘记密码怎么办？" name="3"
        ><p>
          请联系学校食堂平台管理人员协助恢复账号。已经登录的用户可以在“个人设置”修改密码。记住账号不会保存你的密码。
        </p></el-collapse-item
      ><el-collapse-item title="商家注册后如何上架菜品？" name="4"
        ><p>
          商家账号需要先关联食堂档口。请联系平台管理人员完成关联，随后进入“菜品管理”，添加分类并发布菜品，填写名称、价格和库存即可。
        </p></el-collapse-item
      ><el-collapse-item title="体验模式和真实登录有什么区别？" name="5"
        ><p>
          体验模式使用这台电脑浏览器里的示例数据，可以体验学生和商家两方的操作。真实登录会使用服务器中的账号、菜品和订单。两种模式的数据相互独立。
        </p></el-collapse-item
      ></el-collapse
    ></el-dialog
  >
</template>
