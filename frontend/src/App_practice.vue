<script setup>
import { vi } from 'element-plus/es/locale/index.mjs'
import { computed,ref } from 'vue'

const cartItems=ref([])
// 创建一个可以变化的购物车数量
const cartCount =computed(()=>{
    return cartItems.value.reduce((total,item)=>{
        return total+item.quantity
    },0)
})

const cartTotal=computed(()=>{
    return cartItems.value.reduce((total,item)=>{
        return total+item.price*item.quantity
    },0)
})

// 点击加入购物车时执行这个函数
function addToCart(name,price) {
    cartItems.value.push({
        name:name,
        price:price
    })
}
</script>





<template>
  <!-- 整个网页的最外层容器 -->
  <div class="app-shell">

    <!-- 左侧导航区域 -->
    <aside class="sidebar">
      <!-- 网站名称 -->
      <h1>校园食堂</h1>

      <!-- 消费者端导航菜单 -->
      <nav>
        <button>发现菜品</button>
        <button>购物车</button>
        <button>我的订单</button>
      </nav>
    </aside>

    <!-- 右侧主要内容区域 -->
    <main class="main-content">

      <!-- 顶部欢迎区域 -->
      <header class="top-header">
        <p>校园食堂外卖</p>
        <button>退出登录</button>
      </header>

      <!-- 页面欢迎横幅 -->
      <section class="hero">
        <p>校园点餐服务</p>
        <h2>今天想吃点什么？</h2>
        <p>选择喜欢的菜品，轻松完成点餐。</p>
      </section>

      <!-- 页面主体区域 -->
      <section class="content-area">

        <!-- 菜品列表区域 -->
        <section class="dish-section">
          <h2>今日菜品</h2>

          <!-- 第一份菜品 -->
          <article class="dish-card">
            <div class="dish-image">🍗</div>

            <div class="dish-info">
              <p>快餐档口</p>
              <h3>香辣鸡腿饭</h3>
              <p>外酥里嫩的鸡腿，搭配时蔬和米饭。</p>
              <div class="dish-bottom">
                <strong>¥15.90</strong>
                <button @click="addToCart('香辣鸡腿饭',15.9)">
                    加入购物车
                </button>
              </div>
                </div>
          </article>

          <!-- 第二份菜品 -->
          <article class="dish-card">
            <div class="dish-image">🍜</div>

            <div class="dish-info">
              <p>面食档口</p>
              <h3>番茄牛肉面</h3>
              <p>番茄汤底，搭配牛肉和手擀面。</p>

              <div class="dish-bottom">
                <strong>¥18.00</strong>
                <button @click="addToCart('番茄牛肉面',18)">
                    加入购物车
                </button>
              </div>
            </div>
          </article>

          <!-- 第三份菜品 -->
          <article class="dish-card">
            <div class="dish-image">🥟</div>

            <div class="dish-info">
              <p>早餐档口</p>
              <h3>鲜肉小笼包</h3>
              <p>一笼八只，现点现蒸。</p>
              <div class="dish-bottom">
                <strong>¥8.00</strong>
                <button @click="addToCart('鲜肉小笼包',8)">
                    加入购物车
                </button>
              </div>
            </div>
            </article>
        </section>

        <!-- 购物车区域 -->
        <aside class="cart-section">
          <h2>购物车({{cartCount}})</h2>
          <p v-if="cartItems.value.length===0">
            购物车还是空的
          </p>
          <ul v-else>
            <li v-for="(item,index) in cartItems"
                :key="index"
                >
                <span>{{ item.name }}</span>
                <strong>￥{{ item.price }}</strong>
            </li>
          </ul>
        </aside>

      </section>
    </main>
  </div>
</template>



/* 去掉购物车列表默认的项目符号 */
.cart-section ul {
  padding: 0;
  margin: 0;
  list-style: none;
}

/* 每一个购物车商品 */
.cart-section li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 13px 0;
  border-bottom: 1px solid #e5eee9;
  color: #435650;
  font-size: 14px;
}

/* 购物车中的价格 */
.cart-section li strong {
  color: #dc7048;
}
