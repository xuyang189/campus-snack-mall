<template>
  <div class="catalog-page">
    <form class="catalog-filterbar" @submit.prevent="fetchList">
      <select v-model="query.categoryId">
        <option value="">全部分类</option>
        <option v-for="item in categories" :key="item.id" :value="item.id">
          {{ item.name }}
        </option>
      </select>
      <input v-model.trim="query.keyword" placeholder="搜索商品" />
      <input v-model="query.minPrice" type="number" min="0" step="0.01" placeholder="最低价" />
      <input v-model="query.maxPrice" type="number" min="0" step="0.01" placeholder="最高价" />
      <button class="primary-btn" type="submit">筛选</button>
      <button class="ghost-btn" type="button" @click="resetFilters">重置</button>
    </form>

    <div class="catalog-meta">
      <span class="catalog-count">共 {{ pagination.total }} 件商品</span>
      <div class="toolbar">
        <button class="ghost-btn" type="button" :disabled="query.current <= 1" @click="changePage(-1)">上一页</button>
        <span class="user-chip">第 {{ pagination.current }} / {{ totalPages }} 页</span>
        <button class="ghost-btn" type="button" :disabled="pagination.current >= totalPages" @click="changePage(1)">下一页</button>
      </div>
    </div>

    <p v-if="loading" class="loading-text">商品列表加载中...</p>
    <div v-else-if="pagination.records.length" class="product-grid">
      <RouterLink
        v-for="item in pagination.records"
        :key="item.id"
        :to="`/products/${item.id}`"
        class="product-card product-card--catalog"
      >
        <img v-if="item.coverImage" :src="getImageUrl(item.coverImage)" :alt="item.name" class="thumb" />
        <strong>{{ item.name }}</strong>
        <small>{{ item.categoryName }}</small>
        <span>{{ formatPrice(item.price) }}</span>
        <small>库存 {{ item.stock }} / 销量 {{ item.sales }}</small>
      </RouterLink>
    </div>
    <AppEmpty v-else description="当前筛选条件下暂无商品" />
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { RouterLink } from 'vue-router'
import AppEmpty from '@/components/common/AppEmpty.vue'
import { fetchCategoryListApi } from '@/api/category'
import { fetchProductListApi } from '@/api/product'
import { DEFAULT_PAGINATION } from '@/constants/app'
import { normalizeListData, normalizePageData } from '@/utils/api'
import { formatPrice } from '@/utils/format'
import { getImageUrl } from '@/utils/image'

const categories = ref([])
const pagination = ref({ ...DEFAULT_PAGINATION })
const loading = ref(false)
const query = reactive({
  current: 1,
  size: 10,
  keyword: '',
  categoryId: '',
  minPrice: '',
  maxPrice: '',
})

const totalPages = computed(() => Math.max(1, Math.ceil((pagination.value.total || 0) / query.size)))

async function fetchList() {
  if (query.minPrice && query.maxPrice && Number(query.minPrice) > Number(query.maxPrice)) {
    return
  }

  loading.value = true
  const [categoryRes, productRes] = await Promise.allSettled([
    fetchCategoryListApi(),
    fetchProductListApi(query),
  ])

  if (categoryRes.status === 'fulfilled') {
    categories.value = normalizeListData(categoryRes.value.data)
  }

  if (productRes.status === 'fulfilled') {
    pagination.value = normalizePageData(productRes.value.data)
  }

  loading.value = false
}

function changePage(step) {
  query.current += step
  fetchList()
}

function resetFilters() {
  query.current = 1
  query.keyword = ''
  query.categoryId = ''
  query.minPrice = ''
  query.maxPrice = ''
  fetchList()
}

onMounted(fetchList)
</script>
