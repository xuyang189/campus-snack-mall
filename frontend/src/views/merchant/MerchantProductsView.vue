<template>
  <BasePanel title="商品管理" subtitle="提交商品后立即上架，可随时上下架；管理员可下架违规商品">
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
      <button class="ghost-btn" type="button" @click="resetForm">新建商品</button>
    </form>

    <form class="form-grid" @submit.prevent="submitForm">
      <input v-model.trim="form.name" placeholder="商品名称" />
      <select v-model.number="form.categoryId">
        <option value="">选择分类</option>
        <option v-for="item in categories" :key="item.id" :value="item.id">{{ item.name }}</option>
      </select>
      <input v-model.number="form.price" type="number" min="0" step="0.01" placeholder="价格" />
      <input v-model.number="form.stock" type="number" min="0" placeholder="库存" />
      <input type="file" accept=".jpg,.jpeg,.png,.gif,.webp" @change="uploadFile" />
      <input v-model.trim="form.coverImage" placeholder="封面图相对路径" />
      <img
        v-if="form.coverImage"
        :src="form.coverImage.startsWith('/upload') ? getImageUrl(form.coverImage) : form.coverImage"
        alt="商品预览"
        class="image-preview"
      />
      <textarea v-model.trim="form.description" rows="3" placeholder="商品描述" />
      <button class="primary-btn" type="submit" :disabled="submitting">
        {{ submitting ? '提交中...' : (form.id ? '保存修改' : '提交并上架') }}
      </button>
    </form>

    <p v-if="loading" class="loading-text">商品列表加载中...</p>
    <div v-else class="table-like">
      <article v-for="item in records" :key="item.id" class="table-row">
        <div>
          <strong>{{ item.name }}</strong>
          <p>{{ item.categoryName }} · {{ formatPrice(item.price) }} · 库存 {{ item.stock }}</p>
          <p>
            审核 {{ getProductAuditText(item.auditStatus) }} ·
            状态 {{ getProductStatusText(item.status) }}
          </p>
        </div>
        <div class="toolbar">
          <button class="ghost-btn" type="button" @click="editItem(item)">编辑</button>
          <button
            class="ghost-btn"
            type="button"
            @click="toggleStatus(item)"
          >
            {{ item.status === 1 ? '下架' : '上架' }}
          </button>
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
import { merchantProductApi } from '@/api/admin'
import { fetchCategoryListApi } from '@/api/category'
import { uploadImageApi } from '@/api/upload'
import { DEFAULT_PAGINATION } from '@/constants/app'
import { getUploadUrl, normalizeListData, normalizePageData } from '@/utils/api'
import { formatPrice, getProductAuditText, getProductStatusText } from '@/utils/format'
import { getImageUrl } from '@/utils/image'

const message = ref('')
const messageType = ref('info')
const categories = ref([])
const records = ref([])
const loading = ref(false)
const submitting = ref(false)
const pagination = ref({ ...DEFAULT_PAGINATION })
const query = reactive({
  current: 1,
  size: 10,
  keyword: '',
  categoryId: '',
  auditStatus: '',
  status: '',
})
const form = reactive({
  id: null,
  categoryId: '',
  name: '',
  coverImage: '',
  price: '',
  stock: '',
  description: '',
})
const totalPages = computed(() => Math.max(1, Math.ceil((pagination.value.total || 0) / query.size)))

async function fetchList() {
  loading.value = true
  try {
    const [listRes, categoryRes] = await Promise.all([
      merchantProductApi.list(query),
      fetchCategoryListApi(),
    ])
    pagination.value = normalizePageData(listRes.data)
    records.value = pagination.value.records
    categories.value = normalizeListData(categoryRes.data)
  } finally {
    loading.value = false
  }
}

function editItem(item) {
  Object.assign(form, {
    id: item.id,
    categoryId: item.categoryId,
    name: item.name,
    coverImage: item.coverImage,
    price: item.price,
    stock: item.stock,
    description: item.description,
  })
}

function resetForm() {
  Object.assign(form, {
    id: null,
    categoryId: '',
    name: '',
    coverImage: '',
    price: '',
    stock: '',
    description: '',
  })
}

async function submitForm() {
  if (!form.name.trim()) {
    message.value = '请输入商品名称'
    messageType.value = 'error'
    return
  }
  if (!form.categoryId) {
    message.value = '请选择商品分类'
    messageType.value = 'error'
    return
  }
  if (Number(form.price) < 0 || Number.isNaN(Number(form.price))) {
    message.value = '商品价格必须大于等于 0'
    messageType.value = 'error'
    return
  }
  if (Number(form.stock) < 0 || Number.isNaN(Number(form.stock))) {
    message.value = '商品库存必须大于等于 0'
    messageType.value = 'error'
    return
  }

  try {
    submitting.value = true
    const payload = {
      categoryId: Number(form.categoryId),
      name: form.name,
      coverImage: form.coverImage,
      price: Number(form.price),
      stock: Number(form.stock),
      description: form.description,
    }

    if (form.id) {
      await merchantProductApi.update(form.id, payload)
      message.value = '商品已保存'
    } else {
      await merchantProductApi.create(payload)
      message.value = '商品已提交并上架'
    }
    messageType.value = 'success'
    resetForm()
    await fetchList()
  } catch (error) {
    message.value = error.message || '商品提交失败'
    messageType.value = 'error'
  } finally {
    submitting.value = false
  }
}

async function toggleStatus(item) {
  const nextStatus = item.status === 1 ? 0 : 1
  if (!window.confirm(`确认${nextStatus === 1 ? '上架' : '下架'}商品“${item.name}”吗？`)) {
    return
  }
  try {
    await merchantProductApi.updateStatus(item.id, { status: nextStatus })
    message.value = nextStatus === 1 ? '商品已上架，前台即可看到' : '商品已下架'
    messageType.value = 'success'
    await fetchList()
  } catch (error) {
    message.value = error.message || '操作失败'
    messageType.value = 'error'
  }
}

async function removeItem(id) {
  if (!window.confirm('确认删除该商品吗？')) {
    return
  }
  try {
    await merchantProductApi.remove(id)
    message.value = '商品已删除'
    messageType.value = 'success'
    await fetchList()
  } catch (error) {
    message.value = error.message || '删除失败'
    messageType.value = 'error'
  }
}

async function uploadFile(event) {
  const file = event.target.files?.[0]
  if (!file) {
    return
  }
  const { data } = await uploadImageApi(file)
  form.coverImage = getUploadUrl(data)
  message.value = '图片上传成功'
  messageType.value = 'success'
}

function changePage(step) {
  query.current += step
  fetchList()
}

onMounted(fetchList)
</script>
