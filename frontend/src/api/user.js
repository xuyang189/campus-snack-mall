import http from '@/utils/http'

export function fetchUserProfileApi() {
  return http.get('/user/profile')
}

export function updateUserProfileApi(data) {
  return http.put('/user/profile', data)
}

export function updatePasswordApi(data) {
  return http.put('/user/password', data)
}
