import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { fetchCartListApi } from '@/api/cart'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  const checkedCount = ref(0)
  const totalAmount = ref(0)

  const count = computed(() =>
    items.value.reduce((total, item) => total + Number(item.quantity || 0), 0),
  )

  async function fetchCart() {
    const { data } = await fetchCartListApi()
    items.value = Array.isArray(data?.items) ? data.items : []
    checkedCount.value = Number(data?.checkedCount || 0)
    totalAmount.value = Number(data?.totalAmount || 0)
    return items.value
  }

  function clearCart() {
    items.value = []
    checkedCount.value = 0
    totalAmount.value = 0
  }

  return {
    items,
    count,
    checkedCount,
    totalAmount,
    fetchCart,
    clearCart,
  }
})
