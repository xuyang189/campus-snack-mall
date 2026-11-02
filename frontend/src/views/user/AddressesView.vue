<template>
  <BasePanel title="收货地址管理" subtitle="">
    <template #extra>
      <button class="ghost-btn" type="button" @click="resetForm">新增地址</button>
    </template>
    <form class="form-grid" @submit.prevent="submitForm">
      <input v-model.trim="form.receiverName" placeholder="收货人姓名" />
      <input v-model.trim="form.receiverPhone" placeholder="联系电话" />
      <textarea v-model.trim="form.receiverAddress" rows="3" placeholder="详细地址" />
      <label class="toolbar">
        <input v-model="form.isDefault" type="checkbox" />
        <span>设为默认地址</span>
      </label>
      <button class="primary-btn" type="submit" :disabled="submitting">
        {{ submitting ? '提交中...' : (form.id ? '保存地址' : '新增地址') }}
      </button>
    </form>
    <p v-if="loading" class="loading-text">地址列表加载中...</p>
    <div v-else-if="addresses.length" class="list-grid">
      <article v-for="item in addresses" :key="item.id" class="notice-item">
        <h3>
          {{ item.receiverName }}
          <small v-if="item.isDefault">默认地址</small>
        </h3>
        <p>{{ item.receiverPhone }}</p>
        <p>{{ item.receiverAddress }}</p>
        <div class="toolbar">
          <button class="ghost-btn" type="button" @click="editItem(item)">编辑</button>
          <button class="ghost-btn" type="button" @click="setDefault(item.id)">设默认</button>
          <button class="ghost-btn" type="button" @click="removeItem(item.id)">删除</button>
        </div>
      </article>
    </div>
    <AppEmpty v-else description="地址页面已具备独立模块和路由，不与个人资料混在一起。" />
    <AppFeedback :message="message" :type="messageType" />
  </BasePanel>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import AppFeedback from '@/components/common/AppFeedback.vue'
import AppEmpty from '@/components/common/AppEmpty.vue'
import BasePanel from '@/components/common/BasePanel.vue'
import {
  createAddressApi,
  deleteAddressApi,
  fetchAddressListApi,
  setDefaultAddressApi,
  updateAddressApi,
} from '@/api/address'
import { normalizeListData } from '@/utils/api'
import { confirmAction } from '@/utils/ui'

const addresses = ref([])
const message = ref('')
const messageType = ref('info')
const loading = ref(false)
const submitting = ref(false)
const form = reactive({
  id: null,
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  isDefault: false,
})

async function fetchAddresses() {
  loading.value = true
  try {
    const { data } = await fetchAddressListApi()
    addresses.value = normalizeListData(data)
  } catch (error) {
    addresses.value = []
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.id = null
  form.receiverName = ''
  form.receiverPhone = ''
  form.receiverAddress = ''
  form.isDefault = false
}

function editItem(item) {
  form.id = item.id
  form.receiverName = item.receiverName
  form.receiverPhone = item.receiverPhone
  form.receiverAddress = item.receiverAddress
  form.isDefault = Boolean(item.isDefault)
}

async function submitForm() {
  if (!form.receiverName || !form.receiverPhone || !form.receiverAddress) {
    message.value = '请完整填写地址信息'
    messageType.value = 'error'
    return
  }
  const payload = {
    receiverName: form.receiverName,
    receiverPhone: form.receiverPhone,
    receiverAddress: form.receiverAddress,
    isDefault: form.isDefault,
  }

  try {
    submitting.value = true
    if (form.id) {
      await updateAddressApi(form.id, payload)
      message.value = '地址已更新'
    } else {
      await createAddressApi(payload)
      message.value = '地址已新增'
    }
    if (form.isDefault && form.id) {
      await setDefaultAddressApi(form.id)
    }
    messageType.value = 'success'
    resetForm()
    await fetchAddresses()
  } catch (error) {
    message.value = error.message || '地址操作失败'
    messageType.value = 'error'
  } finally {
    submitting.value = false
  }
}

async function setDefault(id) {
  try {
    await setDefaultAddressApi(id)
    message.value = '已设为默认地址'
    messageType.value = 'success'
    await fetchAddresses()
  } catch (error) {
    message.value = error.message || '设置默认地址失败'
    messageType.value = 'error'
  }
}

async function removeItem(id) {
  if (!confirmAction('确认删除该地址吗？')) {
    return
  }
  try {
    await deleteAddressApi(id)
    message.value = '地址已删除'
    messageType.value = 'success'
    await fetchAddresses()
  } catch (error) {
    message.value = error.message || '删除地址失败'
    messageType.value = 'error'
  }
}

onMounted(fetchAddresses)
</script>
