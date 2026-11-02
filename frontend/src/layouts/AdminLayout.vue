<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <RouterLink to="/admin" class="brand brand--admin">Admin Console</RouterLink>
      <RouterLink v-for="item in menus" :key="item.to" :to="item.to">{{ item.label }}</RouterLink>
    </aside>
    <section class="admin-main">
      <header class="admin-header">
        <div>
          <p class="eyebrow">Admin workspace</p>
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
  { label: '控制台', to: '/admin' },
  { label: '分类管理', to: '/admin/categories' },
  { label: '商品管理', to: '/admin/products' },
  { label: '订单管理', to: '/admin/orders' },
  { label: '用户管理', to: '/admin/users' },
  { label: '公告管理', to: '/admin/notices' },
  { label: '统计分析', to: '/admin/statistics' },
]
</script>
