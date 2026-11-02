import http from '@/utils/http'

export function createOrderApi(data) {
  return http.post('/orders', data)
}

export function fetchOrderListApi(params) {
  return http.get('/orders', { params })
}

export function fetchOrderDetailApi(id) {
  return http.get(`/orders/${id}`)
}

export function cancelOrderApi(id) {
  return http.put(`/orders/${id}/cancel`)
}

export function confirmOrderApi(id) {
  return http.put(`/orders/${id}/confirm`)
}
