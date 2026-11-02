import { DEFAULT_PAGINATION } from '@/constants/app'

export function normalizePageData(data) {
  return {
    ...DEFAULT_PAGINATION,
    ...(data || {}),
    records: Array.isArray(data?.records) ? data.records : [],
  }
}

export function normalizeListData(data, key = null) {
  if (Array.isArray(data)) {
    return data
  }

  if (key && Array.isArray(data?.[key])) {
    return data[key]
  }

  if (Array.isArray(data?.items)) {
    return data.items
  }

  if (Array.isArray(data?.records)) {
    return data.records
  }

  return []
}

export function getUploadUrl(data) {
  return data?.url || data?.path || ''
}
