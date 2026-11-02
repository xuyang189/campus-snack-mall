<template>
  <BasePanel title="统计分析" subtitle="">
    <form class="toolbar" @submit.prevent="fetchData">
      <input v-model.number="limit" type="number" min="1" max="20" placeholder="热销数量" />
      <button class="primary-btn" type="submit">刷新统计</button>
    </form>
    <p v-if="loading" class="loading-text">统计数据加载中...</p>
    <div class="split-grid">
      <div class="panel">
        <h3>热销商品</h3>
        <div class="table-like">
          <article v-for="item in hotProducts" :key="item.productId || item.id" class="table-row">
            <strong>{{ item.productName || item.name }}</strong>
            <span>{{ item.sales ?? item.saleCount ?? '--' }}</span>
          </article>
        </div>
      </div>
      <div class="panel">
        <h3>近 7 日订单趋势</h3>
        <div class="table-like">
          <article v-for="item in trends" :key="item.date" class="table-row">
            <strong>{{ item.date }}</strong>
            <span>{{ item.orderCount ?? '--' }} 单 / {{ item.salesAmount ?? '--' }}</span>
          </article>
        </div>
      </div>
    </div>
  </BasePanel>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import BasePanel from '@/components/common/BasePanel.vue'
import { adminStatisticsApi } from '@/api/admin'
import { normalizeListData } from '@/utils/api'

const limit = ref(10)
const hotProducts = ref([])
const trends = ref([])
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const [hotRes, trendRes] = await Promise.all([
      adminStatisticsApi.hotProducts({ limit: limit.value }),
      adminStatisticsApi.orderTrend(),
    ])
    hotProducts.value = normalizeListData(hotRes.data)
    trends.value = normalizeListData(trendRes.data)
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>
