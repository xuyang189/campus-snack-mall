import { ORDER_STATUS_TEXT, PRODUCT_AUDIT_STATUS_TEXT, PRODUCT_STATUS_TEXT } from '@/constants/app'

export function formatPrice(value) {
  const amount = Number(value || 0)
  return `￥${amount.toFixed(2)}`
}

export function formatDateTime(value) {
  if (!value) {
    return '--'
  }

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }

  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(
    date.getDate(),
  ).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(
    date.getMinutes(),
  ).padStart(2, '0')}`
}

export function getOrderStatusText(status) {
  return ORDER_STATUS_TEXT[status] || '未知状态'
}

export function getProductStatusText(status) {
  return PRODUCT_STATUS_TEXT[status] || '未知状态'
}

export function getProductAuditText(status) {
  return PRODUCT_AUDIT_STATUS_TEXT[status] || '未知状态'
}
