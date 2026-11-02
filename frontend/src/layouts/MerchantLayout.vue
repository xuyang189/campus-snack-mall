<template>
  <div class="admin-shell admin-shell--merchant">
    <aside class="admin-sidebar admin-sidebar--merchant">
      <RouterLink to="/merchant" class="brand brand--admin">Merchant Studio</RouterLink>
      <RouterLink v-for="item in menus" :key="item.to" :to="item.to">{{ item.label }}</RouterLink>
    </aside>
    <section class="admin-main">
      <header class="admin-header">
        <div>
          <p class="eyebrow">Merchant workspace</p>
          <h1>{{ route.meta.title }}</h1>
        </div>
        <RouterLink class="ghost-btn" to="/">返回商城</RouterLink>
      </header>
      <RouterView />
    </section>
  </div>
</template>

<script setup>
import { RouterLink, RouterView, useRoute } from 'vue-router'

const route = useRoute()

const menus = [
  { label: '商家总览', to: '/merchant' },
  { label: '商品管理', to: '/merchant/products' },
  { label: '订单管理', to: '/merchant/orders' },
  { label: '经营分析', to: '/merchant/statistics' },
]
</script>
