import http from '@/utils/http'

export function fetchProductListApi(params) {
  return http.get('/products', { params })
}

export function fetchProductDetailApi(id) {
  return http.get(`/products/${id}`)
}

export function fetchHotProductsApi() {
  return http.get('/products/recommend/hot')
}

export function fetchLatestProductsApi() {
  return http.get('/products/recommend/latest')
}
