import http from '@/utils/http'

export function uploadImageApi(file) {
  const formData = new FormData()
  formData.append('file', file)

  return http.post('/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
