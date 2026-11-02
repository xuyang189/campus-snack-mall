import http from '@/utils/http'

export function fetchAddressListApi() {
  return http.get('/user/addresses')
}

export function createAddressApi(data) {
  return http.post('/user/addresses', data)
}

export function updateAddressApi(id, data) {
  return http.put(`/user/addresses/${id}`, data)
}

export function deleteAddressApi(id) {
  return http.delete(`/user/addresses/${id}`)
}

export function setDefaultAddressApi(id) {
  return http.put(`/user/addresses/${id}/default`)
}
