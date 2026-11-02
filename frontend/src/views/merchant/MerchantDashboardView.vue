<template>
  <div class="stack">
    <BasePanel title="经营概览" subtitle="">
      <div class="hero-grid">
        <div v-for="card in cards" :key="card.label" class="hero-card">
          <p>{{ card.label }}</p>
          <h3>{{ card.value }}</h3>
        </div>
      </div>
    </BasePanel>
    <BasePanel title="热销商品排行" subtitle="接口：GET /api/merchant/statistics/hot-products">
      <div v-if="hotProducts.length" class="table-like">
        <article v-for="item in hotProducts" :key="item.productId || item.id" class="table-row">
          <strong>{{ item.productName || item.name }}</strong>
          <span>{{ item.sales ?? item.saleCount ?? '--' }}</span>
        </article>
      </div>
    </BasePanel>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import BasePanel from '@/components/common/BasePanel.vue'
import { merchantStatisticsApi } from '@/api/admin'

const cards = ref([
  { label: '商品总数', value: '--' },
  { label: '订单总数', value: '--' },
  { label: '销售额', value: '--' },
  { label: '今日订单', value: '--' },
])
const hotProducts = ref([])

onMounted(async () => {
  try {
    const [{ data }, hotRes] = await Promise.all([
      merchantStatisticsApi.overview(),
      merchantStatisticsApi.hotProducts({ limit: 10 }),
    ])
    cards.value = [
      { label: '商品总数', value: data.productCount ?? '--' },
      { label: '订单总数', value: data.orderCount ?? '--' },
      { label: '销售额', value: data.salesAmount ?? '--' },
      { label: '今日订单', value: data.todayOrderCount ?? '--' },
      { label: '今日销售额', value: data.todaySalesAmount ?? '--' },
    ]
    hotProducts.value = hotRes.data || []
  } catch (error) {
    // Keep fallback cards.
  }
})
</script>
