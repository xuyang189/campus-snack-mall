<template>
  <div class="site-shell">
    <header class="topbar">
      <RouterLink to="/" class="brand">Campus Snack Mall</RouterLink>
      <nav class="nav">
        <RouterLink to="/products">商品</RouterLink>
        <RouterLink to="/cart">购物车</RouterLink>
        <RouterLink to="/orders">我的订单</RouterLink>
        <RouterLink to="/profile">个人中心</RouterLink>
        <RouterLink to="/addresses">地址管理</RouterLink>
        <RouterLink v-if="authStore.isMerchant" to="/merchant">商家后台</RouterLink>
        <RouterLink v-if="authStore.isAdmin" to="/admin">管理端</RouterLink>
      </nav>
      <div class="topbar__actions">
        <span class="badge">购物车 {{ cartStore.count }}</span>
        <template v-if="authStore.isLoggedIn">
          <span class="user-chip">{{ authStore.userInfo?.nickname || authStore.userInfo?.username }}</span>
          <button class="ghost-btn" @click="handleLogout">退出</button>
        </template>
        <RouterLink v-else class="primary-btn" to="/login">登录</RouterLink>
      </div>
    </header>
    <main class="page-shell">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { RouterLink, RouterView } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'

const authStore = useAuthStore()
const cartStore = useCartStore()

onMounted(() => {
  authStore.hydrate()
  if (authStore.isLoggedIn) {
    cartStore.fetchCart().catch(() => {})
  }
})

async function handleLogout() {
  await authStore.logout()
  cartStore.clearCart()
}
</script>
