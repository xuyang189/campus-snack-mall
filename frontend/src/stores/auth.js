import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { loginApi, logoutApi, adminLoginApi, merchantLoginApi, fetchCurrentUserApi } from '@/api/auth'
import { USER_ROLE } from '@/constants/app'
import { clearAuthSession, loadAuthSession, saveAuthSession } from '@/utils/storage'

export const useAuthStore = defineStore('auth', () => {
  const token = ref('')
  const userInfo = ref(null)

  function hydrate() {
    if (token.value) {
      return
    }

    const session = loadAuthSession()
    token.value = session.token
    userInfo.value = session.userInfo
  }

  function setAuth(payload) {
    token.value = payload.token
    userInfo.value = payload.userInfo
    saveAuthSession(payload.token, payload.userInfo)
  }

  function clearAuth() {
    token.value = ''
    userInfo.value = null
    clearAuthSession()
  }

  async function login(form) {
    const { data } = await loginApi(form)
    setAuth(data)
    return data
  }

  async function adminLogin(form) {
    const { data } = await adminLoginApi(form)
    setAuth(data)
    return data
  }

  async function merchantLogin(form) {
    const { data } = await merchantLoginApi(form)
    setAuth(data)
    return data
  }

  async function refreshProfile() {
    if (!token.value) {
      return null
    }

    const { data } = await fetchCurrentUserApi()
    userInfo.value = data
    saveAuthSession(token.value, data)
    return data
  }

  async function logout() {
    try {
      if (token.value) {
        await logoutApi()
      }
    } finally {
      clearAuth()
    }
  }

  const isLoggedIn = computed(() => Boolean(token.value))
  const isAdmin = computed(() => userInfo.value?.role === USER_ROLE.ADMIN)
  const isMerchant = computed(() => userInfo.value?.role === USER_ROLE.MERCHANT)

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    isMerchant,
    hydrate,
    setAuth,
    clearAuth,
    login,
    merchantLogin,
    adminLogin,
    refreshProfile,
    logout,
  }
})
