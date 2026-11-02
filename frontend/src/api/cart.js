import http from '@/utils/http'

export function fetchCartListApi() {
  return http.get('/cart')
}

export function addCartItemApi(data) {
  return http.post('/cart', data)
}

export function updateCartItemApi(id, data) {
  return http.put(`/cart/${id}`, data)
}

export function updateCartItemCheckedApi(id, data) {
  return http.put(`/cart/${id}/checked`, data)
}

export function deleteCartItemApi(id) {
  return http.delete(`/cart/${id}`)
}

export function batchDeleteCartItemsApi(ids) {
  return http.post('/cart/batch-delete', { ids })
}

export function updateCartCheckedAllApi(data) {
  return http.put('/cart/checked-all', data)
}
