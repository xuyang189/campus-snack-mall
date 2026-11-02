<template>
  <BasePanel title="公告管理" subtitle="">
    <form class="toolbar" @submit.prevent="fetchList">
      <button class="ghost-btn" type="button" @click="resetForm">新建公告</button>
      <button class="primary-btn" type="submit">刷新列表</button>
    </form>
    <form class="form-grid" @submit.prevent="submitForm">
      <input v-model.trim="form.title" placeholder="公告标题" />
      <textarea v-model.trim="form.content" rows="4" placeholder="公告内容" />
      <select v-model.number="form.status">
        <option :value="1">发布</option>
        <option :value="0">下线</option>
      </select>
      <button class="primary-btn" type="submit" :disabled="submitting">
        {{ submitting ? '提交中...' : (form.id ? '保存公告' : '新增公告') }}
      </button>
    </form>
    <p v-if="loading" class="loading-text">公告列表加载中...</p>
    <div v-else class="table-like">
      <article v-for="item in records" :key="item.id" class="table-row">
        <div>
          <strong>{{ item.title }}</strong>
          <p>{{ item.content }}</p>
        </div>
        <div class="toolbar">
          <span class="user-chip">{{ item.status === 1 ? '发布中' : '已下线' }}</span>
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
import { adminNoticeApi } from '@/api/admin'
import { DEFAULT_PAGINATION } from '@/constants/app'
import { normalizePageData } from '@/utils/api'
import { confirmAction } from '@/utils/ui'

const message = ref('')
const messageType = ref('info')
const records = ref([])
const loading = ref(false)
const submitting = ref(false)
const pagination = ref({ ...DEFAULT_PAGINATION })
const query = reactive({
  current: 1,
  size: 10,
})
const form = reactive({
  id: null,
  title: '',
  content: '',
  status: 1,
})
const totalPages = computed(() => Math.max(1, Math.ceil((pagination.value.total || 0) / query.size)))

async function fetchList() {
  loading.value = true
  try {
    const { data } = await adminNoticeApi.list(query)
    pagination.value = normalizePageData(data)
    records.value = pagination.value.records
  } finally {
    loading.value = false
  }
}

function editItem(item) {
  form.id = item.id
  form.title = item.title
  form.content = item.content
  form.status = item.status
}

function resetForm() {
  form.id = null
  form.title = ''
  form.content = ''
  form.status = 1
}

async function submitForm() {
  if (!form.title.trim()) {
    message.value = '请输入公告标题'
    messageType.value = 'error'
    return
  }
  if (!form.content.trim()) {
    message.value = '请输入公告内容'
    messageType.value = 'error'
    return
  }
  const payload = {
    title: form.title,
    content: form.content,
    status: form.status,
  }
  try {
    submitting.value = true
    if (form.id) {
      await adminNoticeApi.update(form.id, payload)
      message.value = '公告已更新'
    } else {
      await adminNoticeApi.create(payload)
      message.value = '公告已新增'
    }
    messageType.value = 'success'
    resetForm()
    await fetchList()
  } catch (error) {
    message.value = error.message || '公告提交失败'
    messageType.value = 'error'
  } finally {
    submitting.value = false
  }
}

async function removeItem(id) {
  if (!confirmAction('确认删除该公告吗？')) {
    return
  }
  await adminNoticeApi.remove(id)
  message.value = '公告已删除'
  messageType.value = 'success'
  await fetchList()
}

function changePage(step) {
  query.current += step
  fetchList()
}

onMounted(fetchList)
</script>
