<template>
  <RouterView />
  <RouterLink v-if="route.path !== '/'" class="quick-home-btn" to="/">
    返回主页
  </RouterLink>
</template>

<script setup>
import { RouterLink, RouterView, useRoute } from 'vue-router'

const route = useRoute()
</script>
