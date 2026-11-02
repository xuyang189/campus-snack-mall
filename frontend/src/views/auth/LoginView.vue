<template>
  <div class="stack">
    <div class="auth-stack">
      <section class="auth-block auth-block--primary">
        <div class="auth-block__header">
          <p class="eyebrow">User Login</p>
          <h2>普通用户登录</h2>
          <span></span>
        </div>
        <form class="form-grid" @submit.prevent="submitLogin">
          <input v-model.trim="form.username" placeholder="用户名" />
          <input v-model="form.password" type="password" placeholder="密码" />
          <button class="primary-btn" type="submit" :disabled="submitting">
            {{ submitting ? '登录中...' : '登录用户端' }}
          </button>
        </form>
      </section>

      <section class="auth-block auth-block--merchant">
        <div class="auth-block__header">
          <p class="eyebrow">Merchant Login</p>
          <h2>入驻用户登录</h2>
          <span></span>
        </div>
        <form class="form-grid" @submit.prevent="submitMerchantLogin">
          <input v-model.trim="merchantForm.username" placeholder="入驻账号" />
          <input v-model="merchantForm.password" type="password" placeholder="入驻密码" />
          <button class="ghost-btn auth-block__submit" type="submit" :disabled="submittingMerchant">
            {{ submittingMerchant ? '登录中...' : '登录入驻端' }}
          </button>
        </form>
      </section>

      <section class="auth-block">
        <div class="auth-block__header">
          <p class="eyebrow">Admin Login</p>
          <h2>管理员登录</h2>
          <span></span>
        </div>
        <form class="form-grid" @submit.prevent="submitAdminLogin">
          <input v-model.trim="adminForm.username" placeholder="管理员账号" />
          <input v-model="adminForm.password" type="password" placeholder="管理员密码" />
          <button class="ghost-btn auth-block__submit" type="submit" :disabled="submittingAdmin">
            {{ submittingAdmin ? '登录中...' : '登录管理端' }}
          </button>
        </form>
      </section>
    </div>

    <div class="auth-footer">
      <RouterLink class="text-link" to="/register">没有账号？去注册普通用户或入驻用户</RouterLink>
    </div>

    <AppFeedback :message="message" :type="messageType" />
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import AppFeedback from '@/components/common/AppFeedback.vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const message = ref('')
const messageType = ref('info')
const submitting = ref(false)
const submittingMerchant = ref(false)
const submittingAdmin = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const merchantForm = reactive({
  username: '',
  password: '',
})

const adminForm = reactive({
  username: '',
  password: '',
})

async function submitLogin() {
  if (!form.username || !form.password) {
    message.value = '请输入用户名和密码'
    messageType.value = 'error'
    return
  }

  try {
    submitting.value = true
    await authStore.login(form)
    message.value = '登录成功'
    messageType.value = 'success'
    router.replace(String(route.query.redirect || '/'))
  } catch (error) {
    message.value = error.message || '登录失败'
    messageType.value = 'error'
  } finally {
    submitting.value = false
  }
}

async function submitMerchantLogin() {
  if (!merchantForm.username || !merchantForm.password) {
    message.value = '请输入入驻账号和密码'
    messageType.value = 'error'
    return
  }

  try {
    submittingMerchant.value = true
    await authStore.merchantLogin(merchantForm)
    message.value = '入驻用户登录成功'
    messageType.value = 'success'
    router.replace(String(route.query.redirect || '/'))
  } catch (error) {
    message.value = error.message || '登录失败'
    messageType.value = 'error'
  } finally {
    submittingMerchant.value = false
  }
}

async function submitAdminLogin() {
  if (!adminForm.username || !adminForm.password) {
    message.value = '请输入管理员账号和密码'
    messageType.value = 'error'
    return
  }

  try {
    submittingAdmin.value = true
    await authStore.adminLogin(adminForm)
    message.value = '管理员登录成功'
    messageType.value = 'success'
    router.replace('/admin')
  } catch (error) {
    message.value = error.message || '登录失败'
    messageType.value = 'error'
  } finally {
    submittingAdmin.value = false
  }
}
</script>
