<template>
  <div class="stack">
    <section class="auth-block auth-block--primary">
      <div class="auth-block__header">
        <p class="eyebrow">Create Account</p>
        <h2>{{ isMerchant ? '注册入驻用户账号' : '注册普通用户账号' }}</h2>
        <span>管理员账号不开放前台注册；这里只处理普通用户和入驻用户。</span>
      </div>

      <div class="auth-role-switch">
        <button
          class="ghost-btn"
          :class="{ 'auth-role-switch__item--active': form.role === 'USER' }"
          type="button"
          :disabled="submitting"
          @click="switchRole('USER')"
        >
          普通用户
        </button>
        <button
          class="ghost-btn"
          :class="{ 'auth-role-switch__item--active': form.role === 'MERCHANT' }"
          type="button"
          :disabled="submitting"
          @click="switchRole('MERCHANT')"
        >
          入驻用户
        </button>
      </div>

      <form class="form-grid" @submit.prevent="submitRegister">
        <div>
          <input
            v-model.trim="form.username"
            placeholder="用户名（4-20位字母、数字或下划线）"
            :disabled="submitting"
            @blur="touchField('username')"
          />
          <p v-if="fieldErrors.username && touched.username" class="feedback feedback--error">
            {{ fieldErrors.username }}
          </p>
        </div>

        <div>
          <input
            v-model="form.password"
            type="password"
            placeholder="密码（6-20位，至少包含字母和数字）"
            :disabled="submitting"
            @blur="touchField('password')"
          />
          <p v-if="fieldErrors.password && touched.password" class="feedback feedback--error">
            {{ fieldErrors.password }}
          </p>
        </div>

        <div>
          <input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
            :disabled="submitting"
            @blur="touchField('confirmPassword')"
          />
          <p v-if="fieldErrors.confirmPassword && touched.confirmPassword" class="feedback feedback--error">
            {{ fieldErrors.confirmPassword }}
          </p>
        </div>

        <div>
          <input
            v-model.trim="form.nickname"
            placeholder="昵称（2-20位）"
            :disabled="submitting"
            @blur="touchField('nickname')"
          />
          <p v-if="fieldErrors.nickname && touched.nickname" class="feedback feedback--error">
            {{ fieldErrors.nickname }}
          </p>
        </div>

        <div>
          <input
            v-model.trim="form.phone"
            placeholder="手机号"
            :disabled="submitting"
            @blur="touchField('phone')"
          />
          <p v-if="fieldErrors.phone && touched.phone" class="feedback feedback--error">
            {{ fieldErrors.phone }}
          </p>
        </div>

        <button class="primary-btn" type="submit" :disabled="submitting || !canSubmit">
          {{ submitting ? '注册中...' : (isMerchant ? '完成入驻注册' : '完成注册') }}
        </button>
      </form>
    </section>

    <div class="auth-footer">
      <RouterLink class="text-link" to="/login">已有账号，返回登录</RouterLink>
    </div>

    <AppFeedback :message="message" :type="messageType" />
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import AppFeedback from '@/components/common/AppFeedback.vue'
import { registerApi, registerMerchantApi } from '@/api/auth'

const router = useRouter()
const message = ref('')
const messageType = ref('info')
const submitting = ref(false)
const form = reactive({
  role: 'USER',
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: '',
})

const touched = reactive({
  username: false,
  password: false,
  confirmPassword: false,
  nickname: false,
  phone: false,
})

const isMerchant = computed(() => form.role === 'MERCHANT')

const fieldErrors = computed(() => ({
  username: validateUsername(form.username),
  password: validatePassword(form.password),
  confirmPassword: validateConfirmPassword(form.password, form.confirmPassword),
  nickname: validateNickname(form.nickname),
  phone: validatePhone(form.phone),
}))

const canSubmit = computed(() => Object.values(fieldErrors.value).every((value) => !value))

watch(
  () => form.password,
  () => {
    if (touched.confirmPassword) {
      touched.confirmPassword = true
    }
  },
)

function validateUsername(value) {
  if (!value) {
    return '用户名不能为空'
  }
  if (!/^[A-Za-z0-9_]{4,20}$/.test(value)) {
    return '用户名需为 4-20 位字母、数字或下划线'
  }
  return ''
}

function validatePassword(value) {
  if (!value) {
    return '密码不能为空'
  }
  if (value.length < 6 || value.length > 20) {
    return '密码需为 6-20 位'
  }
  if (!/[A-Za-z]/.test(value) || !/\d/.test(value)) {
    return '密码至少包含 1 个字母和 1 个数字'
  }
  return ''
}

function validateConfirmPassword(password, confirmPassword) {
  if (!confirmPassword) {
    return '确认密码不能为空'
  }
  if (password !== confirmPassword) {
    return '两次输入的密码不一致'
  }
  return ''
}

function validateNickname(value) {
  if (!value) {
    return '昵称不能为空'
  }
  if (value.length < 2 || value.length > 20) {
    return '昵称长度需为 2-20 位'
  }
  return ''
}

function validatePhone(value) {
  if (!value) {
    return '手机号不能为空'
  }
  if (!/^1\d{10}$/.test(value)) {
    return '手机号格式不正确'
  }
  return ''
}

function touchField(field) {
  touched[field] = true
}

function touchAllFields() {
  Object.keys(touched).forEach((field) => {
    touched[field] = true
  })
}

function resetFeedback() {
  message.value = ''
  messageType.value = 'info'
}

function resetTouched() {
  Object.keys(touched).forEach((field) => {
    touched[field] = false
  })
}

function resetForm() {
  form.username = ''
  form.password = ''
  form.confirmPassword = ''
  form.nickname = ''
  form.phone = ''
  resetTouched()
}

function switchRole(role) {
  if (form.role === role) {
    return
  }
  form.role = role
  resetFeedback()
  resetForm()
}

function getRegisterErrorMessage(error) {
  const fallback = '注册失败，请稍后重试'
  const rawMessage = String(error?.message || error?.msg || '').trim()
  const normalized = rawMessage.toLowerCase()

  if (!rawMessage) {
    return fallback
  }
  if (normalized.includes('username') && (normalized.includes('exist') || rawMessage.includes('已存在'))) {
    return '用户名已存在，请更换后重试'
  }
  if ((normalized.includes('phone') || rawMessage.includes('手机号')) && (normalized.includes('exist') || rawMessage.includes('已存在'))) {
    return '手机号已被占用，请更换后重试'
  }
  if (normalized.includes('nickname') && (normalized.includes('blank') || rawMessage.includes('不能为空'))) {
    return '昵称不能为空'
  }
  return rawMessage
}

async function submitRegister() {
  resetFeedback()
  touchAllFields()

  if (!canSubmit.value) {
    message.value = '请先完整填写并修正表单信息'
    messageType.value = 'error'
    return
  }

  const payload = {
    username: form.username,
    password: form.password,
    nickname: form.nickname,
    phone: form.phone,
  }

  try {
    submitting.value = true
    if (isMerchant.value) {
      await registerMerchantApi(payload)
      message.value = '入驻用户注册成功，正在返回登录页'
    } else {
      await registerApi(payload)
      message.value = '注册成功，正在返回登录页'
    }
    messageType.value = 'success'
    resetForm()
    window.setTimeout(() => {
      router.replace({
        path: '/login',
        query: {
          role: form.role,
        },
      })
    }, 800)
  } catch (error) {
    message.value = getRegisterErrorMessage(error)
    messageType.value = 'error'
  } finally {
    submitting.value = false
  }
}
</script>
