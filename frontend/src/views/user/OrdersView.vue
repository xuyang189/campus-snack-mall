<template>
  <BasePanel title="我的订单" subtitle="">
    <template #extra>
      <div class="toolbar">
        <select v-model="query.status" @change="fetchOrders">
          <option value="">全部状态</option>
          <option value="1">待发货</option>
          <option value="2">配送中</option>
          <option value="3">已完成</option>
          <option value="4">已取消</option>
        </select>
        <button class="ghost-btn" type="button" @click="fetchOrders">刷新</button>
      </div>
    </template>
    <p v-if="loading" class="loading-text">订单列表加载中...</p>
    <div v-else-if="records.length" class="table-like">
      <article v-for="item in records" :key="item.id" class="table-row">
        <div>
          <strong>{{ item.orderNo }}</strong>
          <p>{{ getOrderStatusText(item.status) }} · {{ formatDateTime(item.createTime) }}</p>
        </div>
        <div class="toolbar">
          <span>{{ formatPrice(item.totalAmount) }}</span>
          <button class="ghost-btn" type="button" @click="viewDetail(item.id)">详情</button>
          <button
            v-if="item.status === 1"
            class="ghost-btn"
            type="button"
            @click="cancel(item.id)"
          >
            取消订单
          </button>
          <button
            v-if="item.status === 2"
            class="primary-btn"
            type="button"
            @click="confirm(item.id)"
          >
            确认收货
          </button>
        </div>
      </article>
    </div>
    <div v-if="records.length" class="toolbar">
      <button class="ghost-btn" type="button" :disabled="query.current <= 1" @click="changePage(-1)">上一页</button>
      <span class="user-chip">第 {{ pagination.current }} / {{ totalPages }} 页</span>
      <button class="ghost-btn" type="button" :disabled="pagination.current >= totalPages" @click="changePage(1)">下一页</button>
    </div>
    <AppEmpty v-if="!loading && !records.length" description="订单列表页支持按状态筛选和详情跳转。" />
    <AppFeedback :message="message" :type="messageType" />
  </BasePanel>

  <BasePanel v-if="detail" title="订单详情" :subtitle="detail.orderNo">
    <div class="stack">
      <div class="info-list">
        <span>状态：{{ getOrderStatusText(detail.status) }}</span>
        <span>金额：{{ formatPrice(detail.totalAmount) }}</span>
        <span>收货人：{{ detail.receiverName }} {{ detail.receiverPhone }}</span>
        <span>地址：{{ detail.receiverAddress }}</span>
        <span>下单时间：{{ formatDateTime(detail.createTime) }}</span>
      </div>
      <div class="table-like">
        <article v-for="item in detail.items || []" :key="item.productId" class="table-row">
          <strong>{{ item.productName }}</strong>
          <span>{{ formatPrice(item.price) }} × {{ item.quantity }}</span>
        </article>
      </div>
    </div>
  </BasePanel>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import AppFeedback from '@/components/common/AppFeedback.vue'
import AppEmpty from '@/components/common/AppEmpty.vue'
import BasePanel from '@/components/common/BasePanel.vue'
import { cancelOrderApi, confirmOrderApi, fetchOrderDetailApi, fetchOrderListApi } from '@/api/order'
import { DEFAULT_PAGINATION } from '@/constants/app'
import { normalizePageData } from '@/utils/api'
import { formatDateTime, formatPrice, getOrderStatusText } from '@/utils/format'
import { confirmAction } from '@/utils/ui'

const records = ref([])
const detail = ref(null)
const message = ref('')
const messageType = ref('info')
const loading = ref(false)
const pagination = ref({ ...DEFAULT_PAGINATION })
const query = reactive({
  current: 1,
  size: 10,
  status: '',
})
const totalPages = computed(() => Math.max(1, Math.ceil((pagination.value.total || 0) / query.size)))

async function fetchOrders() {
  loading.value = true
  try {
    const { data } = await fetchOrderListApi(query)
    pagination.value = normalizePageData(data)
    records.value = pagination.value.records
  } catch (error) {
    records.value = []
  } finally {
    loading.value = false
  }
}

async function viewDetail(id) {
  const { data } = await fetchOrderDetailApi(id)
  detail.value = data
}

async function cancel(id) {
  if (!confirmAction('确认取消该订单吗？')) {
    return
  }
  await cancelOrderApi(id)
  message.value = '订单已取消'
  messageType.value = 'success'
  await fetchOrders()
  if (detail.value?.id === id) {
    await viewDetail(id)
  }
}

async function confirm(id) {
  if (!confirmAction('确认已经收到该订单吗？')) {
    return
  }
  await confirmOrderApi(id)
  message.value = '已确认收货'
  messageType.value = 'success'
  await fetchOrders()
  if (detail.value?.id === id) {
    await viewDetail(id)
  }
}

function changePage(step) {
  query.current += step
  fetchOrders()
}

onMounted(fetchOrders)
</script>
