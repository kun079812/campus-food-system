<script setup>
import { computed, ref, watch } from 'vue'
const props = defineProps({
  name: String,
  category: String,
  image: String,
  variant: { type: Number, default: 0 }
})
const failed = ref(false)
watch(
  () => props.image,
  () => {
    failed.value = false
  }
)
const kind = computed(() =>
  /饮品|饮料/.test(props.category || '') || /茶|汁|饮|奶|咖啡/.test(props.name || '')
    ? 'drink'
    : /面|粉/.test(props.name || '')
      ? 'noodle'
      : 'rice'
)
const colors = ['#ff956c', '#f4b85a', '#ff898d', '#80bc93', '#d899c7', '#85b6e5']
const accent = computed(() => colors[Math.abs(props.variant) % colors.length])
</script>

<template>
  <div class="food-art" :style="{ '--food-accent': accent }">
    <img v-if="image && !failed" :src="image" :alt="name" loading="lazy" @error="failed = true" />
    <svg v-else viewBox="0 0 200 140" fill="none" aria-hidden="true">
      <ellipse cx="100" cy="119" rx="62" ry="8" fill="#d8eaff" opacity=".55" />
      <g v-if="kind === 'drink'" stroke="#2495f3" stroke-width="2.5" stroke-linejoin="round">
        <path d="M70 37h62l-8 82H80Z" fill="#fff" />
        <path d="M75 63h53l-6 52H82Z" fill="#c8efca" stroke="none" />
        <path d="M67 37h68l-4-10H72Z" fill="#e8f5ff" />
        <path d="m98 70 14-54h22" />
        <circle cx="101" cy="82" r="13" fill="#fff9d3" stroke="#e6bd61" />
        <path d="m102 70-2 23m-10-12 22 3" stroke="#e6bd61" />
      </g>
      <g v-else stroke="#2495f3" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
        <ellipse cx="100" cy="79" rx="59" ry="19" fill="#edf8ff" />
        <path d="M42 80c4 30 26 45 58 45s54-15 58-45c-27 22-89 23-116 0Z" fill="#fff" />
        <path d="M59 104c18 14 58 17 82-1" stroke="#b7e1ff" stroke-width="4" />
        <g v-if="kind === 'noodle'" :stroke="accent" stroke-width="3">
          <ellipse cx="100" cy="73" rx="46" ry="14" fill="#fff4dd" />
          <path d="M65 73c0-16 67-16 67 0S76 90 76 75s47-14 47-2-35 15-35 3 26-10 26-2-15 9-15 1" />
          <path d="m113 56 48-23m-48 31 50-22" stroke="#e0a87e" />
        </g>
        <g v-else>
          <path
            d="M56 77c-5-10 5-21 14-20-1-13 14-20 23-12 8-12 24-7 26 3 12-4 23 7 22 18 12 1 16 12 7 18-26 12-62 12-92-7Z"
            fill="#fffdf3"
            stroke="#f4c38b"
          />
          <path
            d="M62 69c-9-9 3-23 13-16-2-13 15-17 22-8 13-7 22 6 16 16 0 10-24 19-30 10-9 6-14 4-21-2Z"
            :fill="accent"
            :stroke="accent"
            opacity=".88"
          />
          <path d="m69 58 6 4m8-12 5 5m-5 9 5 3m8-12 5 2" stroke="#fff8ee" stroke-width="3" />
          <path
            d="M121 69c-8-6 0-21 7-13 7-10 18 4 11 9 11 5 4 16-5 11-7 9-20 2-13-7Z"
            fill="#b8d895"
            stroke="#8cb76e"
          />
          <ellipse cx="108" cy="78" rx="15" ry="10" fill="#fff" stroke="#f4d89a" />
          <ellipse cx="109" cy="78" rx="7" ry="5" fill="#ffcf63" stroke="none" />
        </g>
        <path d="M87 24c-8 8 5 12-1 19m17-26c-8 8 5 12-1 19" stroke="#b8dfff" />
      </g>
      <path d="m33 42 3 6 7 1-5 5 1 7-6-3-6 3 1-7-5-5 7-1Z" fill="#ffe3a1" />
      <path d="M163 71c-6-8-14 2-1 10 14-9 4-17 1-10Z" fill="#ffc0ce" />
    </svg>
  </div>
</template>
