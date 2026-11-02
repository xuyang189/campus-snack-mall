<template>
  <div class="stack">
    <BasePanel title="提交订单" subtitle="">
      <form class="form-grid" @submit.prevent="submitOrder">
        <label class="toolbar">
          <span>收货人</span>
          <input v-model.trim="form.receiverName" placeholder="收货人姓名" />
        </label>
        <label class="toolbar">
          <span>手机号</span>
          <input v-model.trim="form.receiverPhone" placeholder="联系电话" />
        </label>
        <label class="toolbar">
          <span>地址</span>
          <textarea v-model.trim="form.receiverAddress" rows="3" placeholder="详细地址" />
        </label>
        <label class="toolbar">
          <span>备注</span>
          <input v-model.trim="form.remark" placeholder="可选，配送备注" />
        </label>
        <div class="toolbar">
          <span class="user-chip">已选 {{ checkedItems.length }} 件商品</span>
          <span class="user-chip">预计金额 {{ formatPrice(cartStore.totalAmount) }}</span>
        </div>
        <button class="primary-btn" type="submit" :disabled="!checkedItems.length || submitting">
          {{ submitting ? '提交中...' : '提交订单' }}
        </button>
      </form>
    </BasePanel>
    <BasePanel title="地址选择" subtitle="">
      <p v-if="loading" class="loading-text">结算信息加载中...</p>
      <div v-else-if="addresses.length" class="list-grid">
        <article
          v-for="item in addresses"
          :key="item.id"
          class="notice-item"
          @click="applyAddress(item)"
        >
          <h3>{{ item.receiverName }} {{ item.receiverPhone }}</h3>
          <p>{{ item.receiverAddress }}</p>
          <small v-if="item.isDefault">默认地址</small>
        </article>
      </div>
      <AppEmpty v-else description="地址列表会在下单前提供快速填充能力。" />
    </BasePanel>
    <BasePanel title="已勾选商品" subtitle="">
      <div v-if="checkedItems.length" class="table-like">
        <article v-for="item in checkedItems" :key="item.id" class="table-row">
          <strong>{{ item.productName }}</strong>
          <span>{{ formatPrice(item.price) }} × {{ item.quantity }}</span>
        </article>
      </div>
      <AppEmpty v-else description="请先去购物车勾选商品再结算。" />
      <AppFeedback :message="message" :type="messageType" />
    </BasePanel>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppEmpty from '@/components/common/AppEmpty.vue'
import AppFeedback from '@/components/common/AppFeedback.vue'
import BasePanel from '@/components/common/BasePanel.vue'
import { fetchAddressListApi } from '@/api/address'
import { createOrderApi } from '@/api/order'
import { useCartStore } from '@/stores/cart'
import { normalizeListData } from '@/utils/api'
import { formatPrice } from '@/utils/format'

const router = useRouter()
const cartStore = useCartStore()
const addresses = ref([])
const message = ref('')
const messageType = ref('info')
const loading = ref(false)
const submitting = ref(false)
const form = reactive({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  remark: '',
})

const checkedItems = computed(() =>
  cartStore.items.filter((item) => item.checked && item.productStatus === 1),
)

onMounted(async () => {
  loading.value = true
  try {
    await cartStore.fetchCart()
    const { data } = await fetchAddressListApi()
    addresses.value = normalizeListData(data)
    const defaultAddress = addresses.value.find((item) => item.isDefault)
    if (defaultAddress) {
      applyAddress(defaultAddress)
    }
  } catch (error) {
    addresses.value = []
  } finally {
    loading.value = false
  }
})

function applyAddress(item) {
  form.receiverName = item.receiverName || ''
  form.receiverPhone = item.receiverPhone || ''
  form.receiverAddress = item.receiverAddress || ''
}

async function submitOrder() {
  if (!form.receiverName || !form.receiverPhone || !form.receiverAddress) {
    message.value = '请完整填写收货信息'
    messageType.value = 'error'
    return
  }
  try {
    submitting.value = true
    const { data } = await createOrderApi({
      cartIds: checkedItems.value.map((item) => item.id),
      receiverName: form.receiverName,
      receiverPhone: form.receiverPhone,
      receiverAddress: form.receiverAddress,
      remark: form.remark,
    })
    message.value = `下单成功，订单号 ${data.orderNo}`
    messageType.value = 'success'
    await cartStore.fetchCart()
    router.push('/orders')
  } catch (error) {
    message.value = error.message || '下单失败'
    messageType.value = 'error'
  } finally {
    submitting.value = false
  }
}
</script>
