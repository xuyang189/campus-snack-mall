import http from '@/utils/http'

export const adminCategoryApi = {
  list: (params) => http.get('/admin/categories', { params }),
  create: (data) => http.post('/admin/categories', data),
  update: (id, data) => http.put(`/admin/categories/${id}`, data),
  remove: (id) => http.delete(`/admin/categories/${id}`),
}

export const adminProductApi = {
  list: (params) => http.get('/admin/products', { params }),
  updateStatus: (id, data) => http.put(`/admin/products/${id}/status`, data),
  audit: (id, data) => http.put(`/admin/products/${id}/audit`, data),
}

export const adminOrderApi = {
  list: (params) => http.get('/admin/orders', { params }),
  detail: (id) => http.get(`/admin/orders/${id}`),
  updateStatus: (id, data) => http.put(`/admin/orders/${id}/status`, data),
}

export const adminUserApi = {
  list: (params) => http.get('/admin/users', { params }),
  updateStatus: (id, data) => http.put(`/admin/users/${id}/status`, data),
}

export const adminNoticeApi = {
  list: (params) => http.get('/admin/notices', { params }),
  create: (data) => http.post('/admin/notices', data),
  update: (id, data) => http.put(`/admin/notices/${id}`, data),
  remove: (id) => http.delete(`/admin/notices/${id}`),
}

export const adminStatisticsApi = {
  overview: () => http.get('/admin/statistics/overview'),
  hotProducts: (params) => http.get('/admin/statistics/hot-products', { params }),
  orderTrend: () => http.get('/admin/statistics/order-trend'),
}

export const merchantProductApi = {
  list: (params) => http.get('/merchant/products', { params }),
  create: (data) => http.post('/merchant/products', data),
  update: (id, data) => http.put(`/merchant/products/${id}`, data),
  remove: (id) => http.delete(`/merchant/products/${id}`),
  updateStatus: (id, data) => http.put(`/merchant/products/${id}/status`, data),
}

export const merchantOrderApi = {
  list: (params) => http.get('/merchant/orders', { params }),
  detail: (id) => http.get(`/merchant/orders/${id}`),
  updateStatus: (id, data) => http.put(`/merchant/orders/${id}/status`, data),
}

export const merchantStatisticsApi = {
  overview: () => http.get('/merchant/statistics/overview'),
  hotProducts: (params) => http.get('/merchant/statistics/hot-products', { params }),
  orderTrend: () => http.get('/merchant/statistics/order-trend'),
}
