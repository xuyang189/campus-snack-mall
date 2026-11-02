<template>
  <BasePanel title="订单管理" subtitle="">
    <form class="toolbar" @submit.prevent="fetchList">
      <input v-model.trim="query.orderNo" placeholder="订单号" />
      <select v-model="query.status">
        <option value="">全部状态</option>
        <option value="1">待发货</option>
        <option value="2">配送中</option>
        <option value="3">已完成</option>
        <option value="4">已取消</option>
      </select>
      <button class="primary-btn" type="submit">查询</button>
    </form>
    <p v-if="loading" class="loading-text">订单列表加载中...</p>
    <div v-else class="table-like">
      <article v-for="item in records" :key="item.id" class="table-row">
        <div>
          <strong>{{ item.orderNo }}</strong>
          <p>{{ item.username }} · {{ getOrderStatusText(item.status) }}</p>
        </div>
        <div class="toolbar">
          <span>{{ formatPrice(item.totalAmount) }}</span>
          <button class="ghost-btn" type="button" @click="viewDetail(item.id)">详情</button>
          <select :value="item.status" @change="changeStatus(item.id, $event.target.value)">
            <option value="1">待发货</option>
            <option value="2">配送中</option>
            <option value="3">已完成</option>
            <option value="4">已取消</option>
          </select>
        </div>
      </article>
    </div>
    <div class="toolbar">
      <button class="ghost-btn" type="button" :disabled="query.current <= 1" @click="changePage(-1)">上一页</button>
      <span class="user-chip">第 {{ pagination.current }} / {{ totalPages }} 页</span>
      <button class="ghost-btn" type="button" :disabled="pagination.current >= totalPages" @click="changePage(1)">下一页</button>
    </div>
    <p v-if="message" class="feedback">{{ message }}</p>
  </BasePanel>
  <BasePanel v-if="detail" title="订单详情" :subtitle="detail.orderNo">
    <div class="stack">
      <div class="info-list">
        <span>用户：{{ detail.username }}</span>
        <span>状态：{{ getOrderStatusText(detail.status) }}</span>
        <span>金额：{{ formatPrice(detail.totalAmount) }}</span>
        <span>收货人：{{ detail.receiverName }} {{ detail.receiverPhone }}</span>
        <span>收货地址：{{ detail.receiverAddress }}</span>
        <span>备注：{{ detail.remark || '--' }}</span>
      </div>
      <div class="table-like">
        <article v-for="item in detail.items || []" :key="`${detail.id}-${item.productId}`" class="table-row">
          <strong>{{ item.productName }}</strong>
          <span>{{ formatPrice(item.price) }} x {{ item.quantity }}</span>
        </article>
      </div>
    </div>
  </BasePanel>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import BasePanel from '@/components/common/BasePanel.vue'
import { merchantOrderApi } from '@/api/admin'
import { DEFAULT_PAGINATION } from '@/constants/app'
import { normalizePageData } from '@/utils/api'
import { formatPrice, getOrderStatusText } from '@/utils/format'
import { confirmAction } from '@/utils/ui'

const message = ref('')
const records = ref([])
const detail = ref(null)
const loading = ref(false)
const pagination = ref({ ...DEFAULT_PAGINATION })
const query = reactive({
  current: 1,
  size: 10,
  orderNo: '',
  status: '',
})
const totalPages = computed(() => Math.max(1, Math.ceil((pagination.value.total || 0) / query.size)))

async function fetchList() {
  loading.value = true
  try {
    const { data } = await merchantOrderApi.list(query)
    pagination.value = normalizePageData(data)
    records.value = pagination.value.records
  } finally {
    loading.value = false
  }
}

async function changeStatus(id, status) {
  if (!confirmAction('确认更新该订单状态吗？')) {
    return
  }
  await merchantOrderApi.updateStatus(id, { status: Number(status) })
  message.value = '订单状态已更新'
  await fetchList()
  if (detail.value?.id === id) {
    await viewDetail(id)
  }
}

async function viewDetail(id) {
  const { data } = await merchantOrderApi.detail(id)
  detail.value = data
}

function changePage(step) {
  query.current += step
  fetchList()
}

onMounted(fetchList)
</script>
