import http from '@/utils/http'

export function registerApi(data) {
  return http.post('/auth/register', data)
}

export function registerMerchantApi(data) {
  return http.post('/auth/merchant/register', data)
}

export function loginApi(data) {
  return http.post('/auth/login', data)
}

export function merchantLoginApi(data) {
  return http.post('/auth/merchant/login', data)
}

export function adminLoginApi(data) {
  return http.post('/auth/admin/login', data)
}

export function fetchCurrentUserApi() {
  return http.get('/auth/me')
}

export function logoutApi() {
  return http.post('/auth/logout')
}
