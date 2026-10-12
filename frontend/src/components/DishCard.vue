<script setup>
import { inject } from 'vue'
import UiIcon from './UiIcon.vue'
import FoodArt from './FoodArt.vue'
import { money } from '../services/format'
defineProps({ dish: { type: Object, required: true } })
const app = inject('campus')
</script>
<template>
  <article class="dish-card">
    <div class="dish-image">
      <FoodArt
        :name="dish.name"
        :category="app.categoryName(dish)"
        :image="dish.imageUrl"
        :variant="dish.id"
      /><span
        v-if="dish.stock <= 0"
        class="sold-out"
        >今日售罄</span
      ><button
        class="favorite-button"
        :class="{ selected: app.favorites.includes(dish.id) }"
        :aria-label="`${app.favorites.includes(dish.id) ? '取消收藏' : '收藏'}${dish.name}`"
        :aria-pressed="app.favorites.includes(dish.id)"
        @click="app.favorite(dish.id)"
      >
        <UiIcon name="heart" :size="17" />
      </button>
    </div>
    <div class="dish-copy">
      <span class="dish-category"
        >{{ app.categoryName(dish) }}<i>·</i>{{ app.stallName(dish.stallId) }}</span
      >
      <h3>{{ dish.name }}</h3>
      <p>{{ dish.description || '食堂现做的好滋味，给今天添一点满足。' }}</p>
      <div class="dish-bottom">
        <strong><small>¥</small>{{ money(dish.price) }}</strong
        ><button
          class="add-dish"
          :disabled="dish.stock <= 0 || app.busy"
          :aria-label="`加入购物车：${dish.name}`"
          @click="app.addCart(dish)"
        >
          <UiIcon name="plus" :size="20" />
        </button>
      </div>
    </div>
  </article>
</template>
