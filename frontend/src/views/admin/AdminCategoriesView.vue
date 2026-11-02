<template>
  <BasePanel title="分类管理" subtitle="">
    <form class="toolbar" @submit.prevent="fetchList">
      <input v-model.trim="query.keyword" placeholder="分类关键词" />
      <button class="ghost-btn" type="button" @click="resetForm">新建分类</button>
      <button class="primary-btn" type="submit">查询</button>
    </form>
    <form class="form-grid" @submit.prevent="submitForm">
      <input v-model.trim="form.name" placeholder="分类名称" />
      <input v-model.number="form.sort" type="number" min="0" placeholder="排序值" />
      <select v-model.number="form.status">
        <option :value="1">启用</option>
        <option :value="0">禁用</option>
      </select>
      <button class="primary-btn" type="submit" :disabled="submitting">
        {{ submitting ? '提交中...' : (form.id ? '保存修改' : '新增分类') }}
      </button>
    </form>
    <p v-if="loading" class="loading-text">分类列表加载中...</p>
    <div v-else class="table-like">
      <article v-for="item in records" :key="item.id" class="table-row">
        <div>
          <strong>{{ item.name }}</strong>
          <p>排序 {{ item.sort }} · 状态 {{ item.status === 1 ? '启用' : '禁用' }}</p>
        </div>
        <div class="toolbar">
          <button class="ghost-btn" type="button" @click="editItem(item)">编辑</button>
          <button class="ghost-btn" type="button" @click="removeItem(item.id)">删除</button>
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
import { adminCategoryApi } from '@/api/admin'
import { DEFAULT_PAGINATION } from '@/constants/app'
import { normalizePageData } from '@/utils/api'

const message = ref('')
const messageType = ref('info')
const records = ref([])
const loading = ref(false)
const submitting = ref(false)
const pagination = ref({ ...DEFAULT_PAGINATION })
const query = reactive({
  current: 1,
  size: 10,
  keyword: '',
})
const form = reactive({
  id: null,
  name: '',
  sort: 1,
  status: 1,
})
const totalPages = computed(() => Math.max(1, Math.ceil((pagination.value.total || 0) / query.size)))

async function fetchList() {
  loading.value = true
  try {
    const { data } = await adminCategoryApi.list(query)
    pagination.value = normalizePageData(data)
    records.value = pagination.value.records
  } finally {
    loading.value = false
  }
}

function editItem(item) {
  form.id = item.id
  form.name = item.name
  form.sort = item.sort
  form.status = item.status
}

function resetForm() {
  form.id = null
  form.name = ''
  form.sort = 1
  form.status = 1
}

async function submitForm() {
  if (!form.name.trim()) {
    message.value = '请输入分类名称'
    messageType.value = 'error'
    return
  }
  try {
    submitting.value = true
    const payload = {
      name: form.name,
      sort: form.sort,
      status: form.status,
    }
    if (form.id) {
      await adminCategoryApi.update(form.id, payload)
      message.value = '分类已更新'
    } else {
      await adminCategoryApi.create(payload)
      message.value = '分类已新增'
    }
    messageType.value = 'success'
    resetForm()
    await fetchList()
  } catch (error) {
    message.value = error.message || '分类提交失败'
    messageType.value = 'error'
  } finally {
    submitting.value = false
  }
}

async function removeItem(id) {
  if (!window.confirm('确认删除该分类吗？')) {
    return
  }
  await adminCategoryApi.remove(id)
  message.value = '分类已删除'
  messageType.value = 'success'
  await fetchList()
}

function changePage(step) {
  query.current += step
  fetchList()
}

onMounted(fetchList)
</script>
