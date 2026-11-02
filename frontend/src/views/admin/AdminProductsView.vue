<template>
  <BasePanel title="商品审核" subtitle="管理员只负责审核与上下架">
    <form class="toolbar" @submit.prevent="fetchList">
      <input v-model.trim="query.keyword" placeholder="商品关键词" />
      <select v-model="query.categoryId">
        <option value="">全部分类</option>
        <option v-for="item in categories" :key="item.id" :value="item.id">{{ item.name }}</option>
      </select>
      <select v-model="query.auditStatus">
        <option value="">全部审核状态</option>
        <option value="0">待审核</option>
        <option value="1">已通过</option>
        <option value="2">已驳回</option>
      </select>
      <select v-model="query.status">
        <option value="">全部上架状态</option>
        <option value="1">上架中</option>
        <option value="0">已下架</option>
      </select>
      <button class="primary-btn" type="submit">查询</button>
    </form>

    <p v-if="loading" class="loading-text">商品列表加载中...</p>
    <div v-else class="table-like">
      <article v-for="item in records" :key="item.id" class="table-row">
        <div>
          <strong>{{ item.name }}</strong>
          <p>{{ item.categoryName }} · 商家 {{ item.merchantName || item.merchantId || '--' }}</p>
          <p>
            审核 {{ getProductAuditText(item.auditStatus) }} ·
            状态 {{ getProductStatusText(item.status) }} ·
            {{ formatPrice(item.price) }} · 库存 {{ item.stock }}
          </p>
        </div>
        <div class="toolbar">
          <button
            v-if="item.auditStatus === 0"
            class="primary-btn"
            type="button"
            @click="auditItem(item.id, 1)"
          >
            通过
          </button>
          <button
            v-if="item.auditStatus === 0"
            class="ghost-btn"
            type="button"
            @click="auditItem(item.id, 2)"
          >
            驳回
          </button>
          <button
            v-if="item.auditStatus === 1"
            class="ghost-btn"
            type="button"
            @click="toggleStatus(item)"
          >
            {{ item.status === 1 ? '下架' : '上架' }}
          </button>
        </div>
      </article>
    </div>

    <div class="toolbar">
      <button class="ghost-btn" type="button" :disabled="query.current <= 1" @click="changePage(-1)">上一页</button>
      <span class="user-chip">第 {{ pagination.current }} / {{ totalPages }} 页</span>
      <button class="ghost-btn" type="button" :disabled="pagination.current >= totalPages" @click="changePage(1)">下一页</button>
    </div>

    <AppFeedback :message="message" :type="messageType" />
  </BasePanel>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import AppFeedback from '@/components/common/AppFeedback.vue'
import BasePanel from '@/components/common/BasePanel.vue'
import { adminProductApi } from '@/api/admin'
import { fetchCategoryListApi } from '@/api/category'
import { DEFAULT_PAGINATION } from '@/constants/app'
import { normalizeListData, normalizePageData } from '@/utils/api'
import { formatPrice, getProductAuditText, getProductStatusText } from '@/utils/format'

const message = ref('')
const messageType = ref('info')
const categories = ref([])
const records = ref([])
const loading = ref(false)
const pagination = ref({ ...DEFAULT_PAGINATION })
const query = reactive({
  current: 1,
  size: 10,
  keyword: '',
  categoryId: '',
  auditStatus: '',
  status: '',
})
const totalPages = computed(() => Math.max(1, Math.ceil((pagination.value.total || 0) / query.size)))

async function fetchList() {
  loading.value = true
  try {
    const [listRes, categoryRes] = await Promise.all([
      adminProductApi.list(query),
      fetchCategoryListApi(),
    ])
    pagination.value = normalizePageData(listRes.data)
    records.value = pagination.value.records
    categories.value = normalizeListData(categoryRes.data)
  } finally {
    loading.value = false
  }
}

async function auditItem(id, auditStatus) {
  const actionText = auditStatus === 1 ? '通过' : '驳回'
  if (!window.confirm(`确认${actionText}该商品吗？`)) {
    return
  }
  await adminProductApi.audit(id, { auditStatus })
  message.value = `商品已${actionText}`
  messageType.value = 'success'
  await fetchList()
}

async function toggleStatus(item) {
  const nextStatus = item.status === 1 ? 0 : 1
  if (!window.confirm(`确认${nextStatus === 1 ? '上架' : '下架'}商品“${item.name}”吗？`)) {
    return
  }
  await adminProductApi.updateStatus(item.id, { status: nextStatus })
  message.value = '商品状态已更新'
  messageType.value = 'success'
  await fetchList()
}

function changePage(step) {
  query.current += step
  fetchList()
}

onMounted(fetchList)
</script>
