import http from '@/utils/http'

export function fetchNoticeListApi() {
  return http.get('/notices')
}
