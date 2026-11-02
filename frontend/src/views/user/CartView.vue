<template>
  <BasePanel title="购物车" subtitle="">
    <template #extra>
      <div class="toolbar">
        <button class="ghost-btn" type="button" @click="toggleAll(true)">全选</button>
        <button class="ghost-btn" type="button" @click="toggleAll(false)">取消全选</button>
        <button class="ghost-btn" type="button" @click="removeChecked">删除已选</button>
        <RouterLink class="primary-btn" to="/checkout">去结算</RouterLink>
      </div>
    </template>
    <p v-if="loading" class="loading-text">购物车加载中...</p>
    <div v-else-if="items.length" class="table-like">
      <article
        v-for="item in items"
        :key="item.id"
        class="table-row"
        :class="{ 'is-muted': item.productStatus !== 1 }"
      >
        <div>
          <label class="toolbar">
            <input
              :checked="item.checked"
              type="checkbox"
              :disabled="item.productStatus !== 1"
              @change="toggleItem(item, $event.target.checked)"
            />
            <strong>{{ item.productName }}</strong>
          </label>
          <p>{{ item.productStatus === 1 ? '可结算' : '已下架，结算时跳过' }}</p>
        </div>
        <div class="toolbar">
          <span>{{ formatPrice(item.price) }}</span>
          <input :value="item.quantity" type="number" min="1" @change="changeQuantity(item, $event.target.value)" />
          <button class="ghost-btn" type="button" @click="removeOne(item.id)">删除</button>
        </div>
      </article>
    </div>
    <div v-if="items.length" class="toolbar">
      <span class="user-chip">已选 {{ cartStore.checkedCount }} 件</span>
      <span class="user-chip">预计金额 {{ formatPrice(cartStore.totalAmount) }}</span>
    </div>
    <AppEmpty v-else description="" />
  </BasePanel>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import AppEmpty from '@/components/common/AppEmpty.vue'
import BasePanel from '@/components/common/BasePanel.vue'
import {
  batchDeleteCartItemsApi,
  deleteCartItemApi,
  updateCartCheckedAllApi,
  updateCartItemApi,
  updateCartItemCheckedApi,
} from '@/api/cart'
import { useCartStore } from '@/stores/cart'
import { formatPrice } from '@/utils/format'

const cartStore = useCartStore()
const items = computed(() => cartStore.items)
const loading = ref(false)

onMounted(() => {
  refresh().catch(() => {})
})

async function refresh() {
  loading.value = true
  await cartStore.fetchCart()
  loading.value = false
}

async function changeQuantity(item, value) {
  await updateCartItemApi(item.id, {
    quantity: Math.max(1, Number(value || 1)),
  })
  await refresh()
}

async function toggleItem(item, checked) {
  await updateCartItemCheckedApi(item.id, { checked })
  await refresh()
}

async function removeOne(id) {
  if (!window.confirm('确认删除这条购物车商品吗？')) {
    return
  }
  await deleteCartItemApi(id)
  await refresh()
}

async function removeChecked() {
  const ids = items.value.filter((item) => item.checked).map((item) => item.id)
  if (!ids.length) {
    return
  }
  if (!window.confirm('确认删除已勾选的购物车商品吗？')) {
    return
  }

  await batchDeleteCartItemsApi(ids)
  await refresh()
}

async function toggleAll(checked) {
  await updateCartCheckedAllApi({ checked })
  await refresh()
}
</script>
