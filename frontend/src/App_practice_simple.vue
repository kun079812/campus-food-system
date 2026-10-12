<script setup>
import { ref } from 'vue'

// 保存当前选择的身份
// STUDENT 表示消费者，MERCHANT 表示商家
const selectedRole = ref('STUDENT')

// 点击按钮时切换身份
function chooseRole(role) {
  selectedRole.value = role
}
</script>


<template>
  <!-- 登录页面最外层 -->
  <div class="login-page">

    <!-- 登录卡片 -->
    <main class="login-card">

      <!-- 系统标题 -->
      <div class="login-header">
        <div class="logo">食</div>
        <p class="small-title">CAMPUS FOOD ORDER</p>
        <h1>
          {{selectedRole==='STUDENT'?'消费者登陆':'商家登陆'}}
          </h1>
        <p>
          {{selectedRole==='STUDENT'?'登录后开始点餐':'登录管理商品'}}
        </p>
      </div>

      <!-- 身份选择区域 -->
      <div class="role-tabs">
        <button
        :class="{active:selectedRole==='STUDENT'}"
        @click="chooseRole('STUDENT')">
          消费者登录
        </button>

        <button
        :class="{active:selectedRole==='MERCHANT'}"
        @click="chooseRole('MERCHANT')">
          商家登录
        </button>
      </div>

      <!-- 登录表单 -->
      <form class="login-form">

        <!-- 用户名输入框 -->
        <label>
          用户名
          <input
            type="text"
            placeholder="请输入用户名"
          />
        </label>

        <!-- 密码输入框 -->
        <label>
          密码
          <input
            type="password"
            placeholder="请输入密码"
          />
        </label>

        <!-- 登录按钮 -->
        <button class="login-button">
          登录
        </button>
      </form>

      <!-- 注册提示 -->
      <p class="login-tip">
        还没有账号？请联系管理员创建账号
      </p>
    </main>
  </div>
</template>
