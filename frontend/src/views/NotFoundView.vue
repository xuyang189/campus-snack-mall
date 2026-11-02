<template>
  <BasePanel title="页面不存在" subtitle="当前路由未命中。">
    <RouterLink class="primary-btn" to="/">返回首页</RouterLink>
  </BasePanel>
</template>

<script setup>
import { RouterLink } from 'vue-router'
import BasePanel from '@/components/common/BasePanel.vue'
</script>
