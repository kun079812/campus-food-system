<script setup>
import { inject, reactive, ref, watch } from 'vue'
import UiIcon from '../components/UiIcon.vue'
const app = inject('campus')
const role = ref('STUDENT')
const registering = ref(false)
const visiblePassword = ref(false)
const remembered = localStorage.getItem('canteen-remembered-account') || ''
const remember = ref(Boolean(remembered))
const form = reactive({ username: remembered, password: '', realName: '', phone: '' })
watch([role, registering], () => {
  app.authError = ''
})
async function submit() {
  if (registering.value) {
    if (await app.register(form, role.value)) {
      registering.value = false
      form.password = ''
    }
  } else await app.login(form, role.value, remember.value)
}
</script>

<template>
  <main class="auth-scene">
    <div class="auth-landscape" aria-hidden="true"></div>
    <div class="auth-welcome">
      <span class="handwritten-kicker">一日三餐 · 四季有味</span>
      <h1>校园里的美味，<br />刚好为你而来<span>。</span></h1>
      <p>少一点等待，多一点热爱。<br />让热腾腾的饭菜，陪你度过每一个校园日常。</p>
      <div class="welcome-tag"><UiIcon name="leaf" :size="18" /> 新鲜每一天，好好吃饭呀</div>
    </div>
    <section class="login-panel" :class="{ registering }" aria-label="登录或注册">
      <div class="login-intro">
        <span class="tiny-flower">✳</span>
        <p>{{ registering ? 'HELLO, NEW FRIEND' : 'WELCOME BACK' }}</p>
        <h2>{{ registering ? '很高兴认识你' : '今天，也要好好吃饭' }}</h2>
        <span>{{
          registering ? '注册食堂小站，开启你的美味日常' : '一份热饭，一点温暖，欢迎回到食堂小站'
        }}</span>
      </div>
      <div class="auth-tabs" role="tablist" aria-label="选择登录身份">
        <button
          role="tab"
          :aria-selected="role === 'STUDENT'"
          :class="{ active: role === 'STUDENT' }"
          @click="role = 'STUDENT'"
        >
          学生{{ registering ? '注册' : '登录' }}</button
        ><button
          role="tab"
          :aria-selected="role === 'MERCHANT'"
          :class="{ active: role === 'MERCHANT' }"
          @click="role = 'MERCHANT'"
        >
          商家{{ registering ? '注册' : '登录' }}
        </button>
      </div>
      <form class="auth-form" @submit.prevent="submit">
        <label class="input-with-icon"
          ><UiIcon name="user" :size="21" /><input
            v-model="form.username"
            required
            maxlength="50"
            autocomplete="username"
            :aria-label="role === 'STUDENT' ? '学号或用户名' : '商家账号'"
            :placeholder="role === 'STUDENT' ? '请输入学号 / 用户名' : '请输入商家账号'"
        /></label>
        <label v-if="registering" class="input-with-icon"
          ><UiIcon name="edit" :size="20" /><input
            v-model="form.realName"
            required
            maxlength="50"
            autocomplete="name"
            aria-label="姓名"
            placeholder="请输入姓名"
        /></label>
        <label v-if="registering" class="input-with-icon"
          ><UiIcon name="chat" :size="20" /><input
            v-model="form.phone"
            required
            pattern="1[0-9]{10}"
            maxlength="11"
            autocomplete="tel"
            aria-label="手机号"
            placeholder="请输入 11 位手机号"
        /></label>
        <label class="input-with-icon"
          ><UiIcon name="lock" :size="21" /><input
            v-model="form.password"
            :type="visiblePassword ? 'text' : 'password'"
            required
            :minlength="registering ? 8 : 1"
            maxlength="72"
            :autocomplete="registering ? 'new-password' : 'current-password'"
            aria-label="密码"
            :placeholder="registering ? '设置密码（至少 8 位）' : '请输入密码'" /><button
            type="button"
            class="icon-button"
            :aria-label="visiblePassword ? '隐藏密码' : '显示密码'"
            @click="visiblePassword = !visiblePassword"
          >
            <UiIcon name="eye" :size="18" /></button
        ></label>
        <div v-if="!registering" class="login-options">
          <label><input v-model="remember" type="checkbox" /> 记住账号</label
          ><button type="button" class="text-button" @click="app.helpOpen = true">
            忘记密码？
          </button>
        </div>
        <p v-if="registering && role === 'MERCHANT'" class="form-note">
          商家注册后需由平台关联食堂档口，关联完成即可发布菜品。
        </p>
        <p v-if="app.authError" class="form-error" role="alert">{{ app.authError }}</p>
        <button class="primary-button auth-submit" :disabled="app.authBusy" type="submit">
          {{ app.authBusy ? '正在处理…' : registering ? '创建账号' : '登录'
          }}<UiIcon name="arrow" :size="20" />
        </button>
      </form>
      <p class="register-line">
        {{ registering ? '已经有账号？' : '还没有账号？'
        }}<button class="text-button" @click="registering = !registering">
          {{ registering ? '立即登录' : '立即注册' }}
        </button>
      </p>
      <div v-if="!registering" class="preview-entry">
        <span>先逛一逛</span>
        <div>
          <button @click="app.enterPreview('STUDENT')">
            体验学生端 <UiIcon name="arrow" :size="14" /></button
          ><button @click="app.enterPreview('MERCHANT')">
            体验商家端 <UiIcon name="arrow" :size="14" />
          </button>
        </div>
      </div>
    </section>
    <div class="auth-benefits">
      <div>
        <span class="benefit-icon rose"><UiIcon name="bowl" :size="29" /></span>
        <p><strong>校园美食</strong><small>多样选择 · 满足每一种口味</small></p>
      </div>
      <div>
        <span class="benefit-icon mint"><UiIcon name="shield" :size="29" /></span>
        <p><strong>安心好食材</strong><small>新鲜制作 · 用心做好每一餐</small></p>
      </div>
      <div>
        <span class="benefit-icon sky"><UiIcon name="bag" :size="29" /></span>
        <p><strong>轻松在线点餐</strong><small>告别排队 · 美味不用等</small></p>
      </div>
      <div>
        <span class="benefit-icon peach"><UiIcon name="store" :size="29" /></span>
        <p><strong>到店自取 / 配送</strong><small>自在选择 · 让生活更方便</small></p>
      </div>
    </div>
  </main>
</template>
