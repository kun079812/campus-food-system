<script setup>
import { computed, inject } from 'vue'
import UiIcon from '../components/UiIcon.vue'
import FoodArt from '../components/FoodArt.vue'
import DishCard from '../components/DishCard.vue'
const app = inject('campus')
const categoryItems = computed(() => ['全部', ...new Set(app.categories.map((c) => c.name))])
const filtered = computed(() =>
  app.dishes.filter(
    (dish) =>
      (!app.keyword.trim() ||
        `${dish.name} ${dish.description || ''} ${app.stallName(dish.stallId)}`
          .toLowerCase()
          .includes(app.keyword.trim().toLowerCase())) &&
      (app.selectedCategory === '全部' || app.categoryName(dish) === app.selectedCategory) &&
      (!app.selectedStall || dish.stallId === Number(app.selectedStall))
  )
)
const showingRecommendations = computed(
  () => app.page === 'home' && !app.keyword && app.selectedCategory === '全部' && !app.selectedStall
)
const visibleDishes = computed(() =>
  showingRecommendations.value ? filtered.value.slice(0, 5) : filtered.value
)
function icon(label) {
  return /面|粉/.test(label)
    ? 'noodle'
    : /饮|茶/.test(label)
      ? 'drink'
      : /早/.test(label)
        ? 'sun'
        : /小吃/.test(label)
          ? 'snack'
          : /特色/.test(label)
            ? 'chef'
            : /午/.test(label)
              ? 'egg'
              : /米|饭|晚/.test(label)
                ? 'bowl'
                : 'meal'
}
function choose(label) {
  app.selectedCategory = label
  document.getElementById('today-menu')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}
function resetFilters() {
  app.keyword = ''
  app.selectedCategory = '全部'
  app.selectedStall = ''
}
</script>

<template>
  <div class="student-home">
    <section v-if="app.page === 'home'" class="campus-hero">
      <div class="hero-landscape" aria-hidden="true"></div>
      <div class="hero-content">
        <div class="hero-eyebrow"><span></span> 校园限定的幸福滋味</div>
        <h1>
          美味食堂<span>陪你度过每一个<br class="mobile-break" />校园日常</span>
        </h1>
        <p>新鲜现做 · 营养搭配 · 便捷点餐</p>
        <button class="coral-button" @click="choose('全部')">
          今天吃点好的 <UiIcon name="arrow" :size="20" />
        </button>
        <div class="hero-little-note">
          <UiIcon name="heart" :size="14" /> 一份美味，让平凡的一天闪闪发光
        </div>
      </div>
      <div class="hero-sticker">
        <UiIcon name="leaf" :size="19" /><span>GOOD FOOD<br /><strong>GOOD MOOD</strong></span>
      </div>
    </section>
    <section v-else class="menu-heading">
      <p class="eyebrow">FIND YOUR FAVORITE</p>
      <h1>今天，想吃点什么？</h1>
      <p>发现食堂里的好滋味，每一餐都有新期待。</p>
    </section>

    <div class="category-strip" aria-label="菜品分类">
      <button
        v-for="(label, index) in categoryItems"
        :key="label"
        :class="{ active: app.selectedCategory === label }"
        :aria-pressed="app.selectedCategory === label"
        @click="choose(label)"
      >
        <span
          class="category-icon"
          :class="['rose', 'peach', 'sky', 'mint', 'butter', 'lavender'][index % 6]"
          ><UiIcon :name="icon(label)" :size="31" /></span
        ><span>{{ label }}</span>
      </button>
    </div>

    <section id="today-menu" class="menu-section">
      <div class="section-heading">
        <div>
          <h2>
            <span class="heading-petal">✦</span
            >{{ app.page === 'home' && !app.keyword ? '今日推荐' : '食堂菜单'
            }}<small>营养美味 · 每天不重样</small>
          </h2>
          <p v-if="app.keyword" class="search-result">
            “{{ app.keyword }}” 的搜索结果 · {{ filtered.length }} 道菜品
          </p>
        </div>
        <div class="menu-tools">
          <select v-model="app.selectedStall" aria-label="选择食堂档口">
            <option value="">全部档口</option>
            <option v-for="stall in app.stalls" :key="stall.id" :value="stall.id">
              {{ stall.name }}
            </option></select
          ><button class="text-button muted" aria-label="刷新菜单" @click="app.refresh">
            <UiIcon name="refresh" :size="17" />
          </button>
        </div>
      </div>
      <div v-if="filtered.length" class="dish-grid">
        <DishCard v-for="dish in visibleDishes" :key="dish.id" :dish="dish" />
      </div>
      <div v-if="showingRecommendations && filtered.length > 5" class="more-dishes">
        <button class="text-button muted" @click="app.navigate('menu')">
          看看全部 {{ filtered.length }} 道美味 <UiIcon name="arrow" :size="16" />
        </button>
      </div>
      <div v-else-if="!app.loading" class="empty-state">
        <UiIcon name="bowl" :size="50" />
        <h3>{{ app.dishes.length ? '还没有找到这道美味' : '食堂正在准备美味' }}</h3>
        <p>{{ app.dishes.length ? '换个关键词或分类，再看看吧。' : '菜品上架后会出现在这里。' }}</p>
        <button v-if="app.dishes.length" class="soft-button" @click="resetFilters">
          查看全部菜品
        </button>
      </div>
    </section>

    <section v-if="app.page === 'home'" class="promo-grid" aria-label="美食发现">
      <button
        class="promo-card peach"
        @click="choose(app.categories.find((c) => /饭/.test(c.name))?.name || '全部')"
      >
        <div>
          <span>一餐好好吃</span>
          <h3>暖心好饭</h3>
          <p>烟火气里，藏着小确幸 <UiIcon name="arrow" :size="16" /></p>
        </div>
        <FoodArt name="米饭" :variant="1" />
      </button>
      <button
        class="promo-card lavender"
        @click="choose(app.categories.find((c) => /面/.test(c.name))?.name || '全部')"
      >
        <div>
          <span>每一口都满足</span>
          <h3>热气腾腾</h3>
          <p>面面俱到的好滋味 <UiIcon name="arrow" :size="16" /></p>
        </div>
        <FoodArt name="牛肉面" :variant="2" />
      </button>
      <button
        class="promo-card sky"
        @click="choose(app.categories.find((c) => /饮|茶/.test(c.name))?.name || '全部')"
      >
        <div>
          <span>给生活加点甜</span>
          <h3>清爽饮品</h3>
          <p>一杯好心情，快乐一整天 <UiIcon name="arrow" :size="16" /></p>
        </div>
        <FoodArt name="柠檬茶" />
      </button>
    </section>
    <div class="home-assurance">
      <span><UiIcon name="shield" :size="17" /> 食堂直供，安心好食材</span
      ><span><UiIcon name="clock" :size="17" /> 在线预订，少等一会儿</span
      ><span><UiIcon name="heart" :size="17" /> 认真对待你的每一餐</span>
    </div>
  </div>
</template>
