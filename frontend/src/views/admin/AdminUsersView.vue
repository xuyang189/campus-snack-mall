<template>
  <BasePanel title="用户管理" subtitle="">
    <form class="toolbar" @submit.prevent="fetchList">
      <input v-model.trim="query.keyword" placeholder="用户名/昵称/手机号" />
      <select v-model="query.status">
        <option value="">全部状态</option>
        <option value="1">启用</option>
        <option value="0">禁用</option>
      </select>
      <button class="primary-btn" type="submit">查询</button>
    </form>
    <p v-if="loading" class="loading-text">用户列表加载中...</p>
    <div v-else class="table-like">
      <article v-for="item in records" :key="item.id" class="table-row">
        <div>
          <strong>{{ item.username }}</strong>
          <p>{{ item.nickname || '--' }} · {{ item.phone || '--' }}</p>
        </div>
        <div class="toolbar">
          <span class="user-chip">{{ item.status === 1 ? '启用' : '禁用' }}</span>
          <button class="ghost-btn" type="button" @click="toggleStatus(item)">
            {{ item.status === 1 ? '禁用' : '启用' }}
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
import { adminUserApi } from '@/api/admin'
import { DEFAULT_PAGINATION } from '@/constants/app'
import { normalizePageData } from '@/utils/api'
import { confirmAction } from '@/utils/ui'

const records = ref([])
const loading = ref(false)
const message = ref('')
const messageType = ref('info')
const pagination = ref({ ...DEFAULT_PAGINATION })
const query = reactive({
  current: 1,
  size: 10,
  keyword: '',
  status: '',
})
const totalPages = computed(() => Math.max(1, Math.ceil((pagination.value.total || 0) / query.size)))

async function fetchList() {
  loading.value = true
  try {
    const { data } = await adminUserApi.list(query)
    pagination.value = normalizePageData(data)
    records.value = pagination.value.records
  } finally {
    loading.value = false
  }
}

async function toggleStatus(item) {
  if (!confirmAction(`确认${item.status === 1 ? '禁用' : '启用'}用户 ${item.username} 吗？`)) {
    return
  }
  await adminUserApi.updateStatus(item.id, {
    status: item.status === 1 ? 0 : 1,
  })
  message.value = '用户状态已更新'
  messageType.value = 'success'
  await fetchList()
}

function changePage(step) {
  query.current += step
  fetchList()
}

onMounted(fetchList)
</script>
