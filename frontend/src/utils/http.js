import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
})

http.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  authStore.hydrate()

  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }

  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data

    if (payload?.code !== undefined && payload.code !== 200) {
      return Promise.reject({
        ...payload,
        message: payload.message || '请求失败',
      })
    }

    return payload
  },
  async (error) => {
    const authStore = useAuthStore()
    const status = error.response?.status
    const payload = error.response?.data

    if (status === 401 || payload?.code === 401) {
      authStore.clearAuth()
      const redirect = `${window.location.pathname}${window.location.search}`
      window.location.replace(`/login?redirect=${encodeURIComponent(redirect)}`)
    }

    return Promise.reject(payload || error)
  },
)

export default http
