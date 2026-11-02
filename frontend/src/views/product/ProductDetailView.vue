<template>
  <BasePanel title="商品详情" subtitle="">
    <p v-if="loading" class="loading-text">商品详情加载中...</p>
    <div v-else-if="product" class="detail-grid">
      <div>
        <img v-if="product.coverImage" :src="getImageUrl(product.coverImage)" :alt="product.name" class="cover" />
        <p class="eyebrow">商品信息</p>
        <h2>{{ product.name }}</h2>
        <p>{{ product.description || '暂无商品描述' }}</p>
      </div>
      <div class="info-list">
        <span>价格：{{ formatPrice(product.price) }}</span>
        <span>库存：{{ product.stock }}</span>
        <span>销量：{{ product.sales }}</span>
        <span>状态：{{ product.status === 1 ? '上架中' : '已下架' }}</span>
        <input v-model.number="quantity" type="number" min="1" placeholder="购买数量" />
        <button class="primary-btn" :disabled="product.status !== 1" @click="addToCart">
          {{ product.status === 1 ? '加入购物车' : '商品已下架' }}
        </button>
        <p v-if="message" class="feedback">{{ message }}</p>
      </div>
    </div>
    <AppEmpty v-else description="商品详情页已接入文档要求的上下架状态判断。" />
  </BasePanel>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import AppEmpty from '@/components/common/AppEmpty.vue'
import BasePanel from '@/components/common/BasePanel.vue'
import { addCartItemApi } from '@/api/cart'
import { fetchProductDetailApi } from '@/api/product'
import { formatPrice } from '@/utils/format'
import { getImageUrl } from '@/utils/image'

const route = useRoute()
const product = ref(null)
const quantity = ref(1)
const message = ref('')
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const { data } = await fetchProductDetailApi(route.params.id)
    product.value = data
  } catch (error) {
    product.value = null
    message.value = error.message || '商品详情加载失败'
  } finally {
    loading.value = false
  }
})

async function addToCart() {
  if (!product.value || product.value.status !== 1) {
    return
  }

  try {
    await addCartItemApi({
      productId: product.value.id,
      quantity: Math.max(1, Number(quantity.value || 1)),
    })
    message.value = '已加入购物车'
  } catch (error) {
    message.value = error.message || '加入购物车失败'
  }
}
</script>
