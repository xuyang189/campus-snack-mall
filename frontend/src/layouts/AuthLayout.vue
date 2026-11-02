<template>
  <div class="auth-shell auth-shell--centered">
    <section class="auth-stage">
      <div class="auth-stage__topbar">
        <RouterLink class="ghost-btn" to="/">返回主页</RouterLink>
      </div>
      <div class="auth-stage__intro">
        <span class="section-kicker">Campus snack mall</span>
        <h1>轻一点登录，快一点下单。</h1>
        <p>
          认证页改成中心式布局，不再左右切屏。保留用户端和管理端双入口，但整体更收敛、更像正式产品登录页。
        </p>
      </div>
      <section class="auth-card auth-card--center">
        <RouterView />
      </section>
    </section>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>
