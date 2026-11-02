import http from '@/utils/http'

export function fetchCategoryListApi() {
  return http.get('/categories')
}
