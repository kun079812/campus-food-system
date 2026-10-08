<script setup>
defineProps({
  name: { type: String, default: 'bowl' },
  size: { type: [Number, String], default: 22 }
})
const paths = {
  chef: [
    'M7 15v6h10v-6',
    'M7 15H5a4 4 0 0 1-1-7.87 5 5 0 0 1 9.2-3.8A4 4 0 0 1 20 8a3.5 3.5 0 0 1-1 7h-2',
    'M10 11v3m4-3v3M7 18h10'
  ],
  home: ['m3 10 9-7 9 7', 'M5 9v12h5v-7h4v7h5V9'],
  search: ['M21 21l-5-5', 'M18 10a8 8 0 1 1-16 0 8 8 0 0 1 16 0'],
  user: ['M16 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0', 'M4 21v-2a8 8 0 0 1 16 0v2Z'],
  lock: ['M6 10h12v11H6Z', 'M8 10V6a4 4 0 0 1 8 0v4', 'M12 14v3'],
  eye: ['M2 12s3-7 10-7 10 7 10 7-3 7-10 7S2 12 2 12', 'M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0'],
  cart: ['M2 3h3l3 13h11l3-10H6', 'M10 21h.01M18 21h.01'],
  arrow: ['M4 12h16m-6-6 6 6-6 6'],
  chevron: ['m9 5 7 7-7 7'],
  down: ['m6 9 6 6 6-6'],
  plus: ['M12 5v14M5 12h14'],
  minus: ['M5 12h14'],
  close: ['m6 6 12 12M6 18 18 6'],
  heart: [
    'M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8L12 21l8.8-8.6a5.5 5.5 0 0 0 0-7.8Z'
  ],
  star: ['m12 2 3.1 6.3 6.9 1-5 4.9 1.2 6.8-6.2-3.2L5.8 21 7 14.2 2 9.3l6.9-1Z'],
  bag: ['M5 7h14l2 14H3Z', 'M8 8V6a4 4 0 0 1 8 0v2'],
  shield: ['m12 2 9 4v6c0 6-9 10-9 10S3 18 3 12V6Z', 'm8 12 3 3 5-6'],
  bowl: [
    'M3 11h18c0 7-4 10-9 10S3 18 3 11Z',
    'M7 3c-3 3 3 3 0 6m5-7c-3 3 3 3 0 6m5-5c-3 3 3 3 0 6'
  ],
  noodle: ['M3 12h18c0 6-4 9-9 9s-9-3-9-9Z', 'm9 3 13 2M9 6l13 2M7 10c0-3 9-3 9 0m-6 2V8m3 4V8'],
  drink: ['M5 7h14l-2 14H7Z', 'M4 7h16M12 7l2-5h5', 'M8 12c3 2 5-2 8 0'],
  sun: [
    'M16 12a4 4 0 1 1-8 0 4 4 0 0 1 8 0',
    'M12 2v2m0 16v2M2 12h2m16 0h2M5 5l1 1m12 12 1 1M5 19l1-1M18 6l1-1'
  ],
  meal: ['M3 18h18M5 15a7 7 0 0 1 14 0ZM12 5V3m-1 0h2'],
  egg: ['M19 14c0 5-3 7-7 7s-7-2-7-7S9 3 12 3s7 6 7 11Z', 'M15 14a3 3 0 1 1-6 0 3 3 0 0 1 6 0'],
  snack: ['m4 21 16-18M5 13l5 5M8 6l9 9M14 3l7 7', 'm3 11 5-5 9 9-5 5Z'],
  orders: ['M8 4H5v18h14V4h-3M9 2h6v5H9ZM8 11h8m-8 4h8m-8 4h5'],
  pin: ['M20 10c0 6-8 12-8 12S4 16 4 10a8 8 0 1 1 16 0Z', 'M15 10a3 3 0 1 1-6 0 3 3 0 0 1 6 0'],
  ticket: ['M3 5h18v5a2 2 0 0 0 0 4v5H3v-5a2 2 0 0 0 0-4Zm12 0v3m0 3v2m0 3v3'],
  settings: [
    'M12 8a4 4 0 1 1 0 8 4 4 0 0 1 0-8',
    'm10 2-1 3-3 1-3 1v4l2 1v3l-1 2 3 3 3-1 2 2 3-1 1-3 3-1 2-2-1-3 1-3-3-2-1-3-4-1Z'
  ],
  chart: ['M4 21V10m5 11V3m6 18V8m5 13V5'],
  store: [
    'M3 10h18l-2-7H5ZM5 11v10h14V11M9 21v-7h6v7',
    'M3 10c0 4 5 4 5 0 0 4 8 4 8 0 0 4 5 4 5 0'
  ],
  bell: ['M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9', 'M10 21h4'],
  logout: ['M9 3H3v18h6m5-14 5 5-5 5m-7-5h15'],
  truck: [
    'M1 4h13v13H1Zm13 5h5l4 5v3h-9',
    'M7 18a2 2 0 1 1-4 0 2 2 0 0 1 4 0m14 0a2 2 0 1 1-4 0 2 2 0 0 1 4 0'
  ],
  wallet: ['M3 5h16v16H3ZM3 5V3h14v2', 'M14 11h7v5h-7Zm3 2.5h.01'],
  check: ['m5 12 4 4L19 6'],
  clock: ['M22 12a10 10 0 1 1-20 0 10 10 0 0 1 20 0', 'M12 6v6l4 2'],
  edit: ['m14 4 6 6M3 21l5-1L22 6l-5-5L3 15Z'],
  trash: ['M3 6h18M9 6V3h6v3M5 6l1 15h12l1-15M10 10v7m4-7v7'],
  refresh: ['M21 11a9 9 0 0 0-15-7L3 7m0-5v5h5M3 13a9 9 0 0 0 15 7l3-3m0 5v-5h-5'],
  chat: ['M21 11a9 9 0 0 1-9 9H4l-3 2 1-7a9 9 0 1 1 19-4Z', 'M7 11h.01M12 11h.01M17 11h.01'],
  leaf: ['M3 21C23 20 21 3 21 3S3 1 3 21Zm0 0L16 8'],
  info: ['M22 12a10 10 0 1 1-20 0 10 10 0 0 1 20 0', 'M12 11v6m0-10v.01']
}
</script>

<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.65"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
  >
    <path v-for="(d, index) in paths[name] || paths.bowl" :key="index" :d="d" />
  </svg>
</template>
