<template>
  <div class="stack">
    <BasePanel title="个人资料" subtitle="">
      <p v-if="loading" class="loading-text">个人资料加载中...</p>
      <form v-else-if="profile" class="form-grid" @submit.prevent="submitProfile">
        <input :value="profile.username" disabled placeholder="用户名" />
        <input v-model.trim="profileForm.nickname" placeholder="昵称" />
        <input v-model.trim="profileForm.phone" placeholder="手机号" />
        <input v-model.trim="profileForm.avatar" placeholder="头像相对路径" />
        <button class="primary-btn" type="submit" :disabled="submittingProfile">
          {{ submittingProfile ? '保存中...' : '保存资料' }}
        </button>
      </form>
      <AppEmpty v-else description="个人资料页已按接口文档准备完成。" />
      <AppFeedback :message="message" :type="messageType" />
    </BasePanel>
    <BasePanel title="修改密码" subtitle="">
      <form class="form-grid" @submit.prevent="submitPassword">
        <input v-model="passwordForm.oldPassword" type="password" placeholder="旧密码" />
        <input v-model="passwordForm.newPassword" type="password" placeholder="新密码（6-20位）" />
        <input v-model="passwordForm.confirmPassword" type="password" placeholder="确认新密码" />
        <button class="primary-btn" type="submit" :disabled="submittingPassword">
          {{ submittingPassword ? '提交中...' : '修改密码' }}
        </button>
      </form>
    </BasePanel>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import AppFeedback from '@/components/common/AppFeedback.vue'
import AppEmpty from '@/components/common/AppEmpty.vue'
import BasePanel from '@/components/common/BasePanel.vue'
import { fetchUserProfileApi, updatePasswordApi, updateUserProfileApi } from '@/api/user'

const profile = ref(null)
const message = ref('')
const messageType = ref('info')
const loading = ref(false)
const submittingProfile = ref(false)
const submittingPassword = ref(false)
const profileForm = reactive({
  nickname: '',
  phone: '',
  avatar: '',
})
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

onMounted(async () => {
  loading.value = true
  try {
    const { data } = await fetchUserProfileApi()
    profile.value = data
    profileForm.nickname = data.nickname || ''
    profileForm.phone = data.phone || ''
    profileForm.avatar = data.avatar || ''
  } catch (error) {
    profile.value = null
  } finally {
    loading.value = false
  }
})

async function submitProfile() {
  try {
    submittingProfile.value = true
    await updateUserProfileApi({
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      avatar: profileForm.avatar,
    })
    message.value = '个人资料已更新'
    messageType.value = 'success'
    const { data } = await fetchUserProfileApi()
    profile.value = data
  } catch (error) {
    message.value = error.message || '更新失败'
    messageType.value = 'error'
  } finally {
    submittingProfile.value = false
  }
}

async function submitPassword() {
  if (passwordForm.newPassword.length < 6 || passwordForm.newPassword.length > 20) {
    message.value = '新密码长度需为 6-20 位'
    messageType.value = 'error'
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    message.value = '两次输入的新密码不一致'
    messageType.value = 'error'
    return
  }

  try {
    submittingPassword.value = true
    await updatePasswordApi({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
    })
    message.value = '密码修改成功'
    messageType.value = 'success'
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    message.value = error.message || '密码修改失败'
    messageType.value = 'error'
  } finally {
    submittingPassword.value = false
  }
}
</script>
