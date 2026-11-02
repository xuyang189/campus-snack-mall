export const USER_ROLE = {
  USER: 'USER',
  MERCHANT: 'MERCHANT',
  ADMIN: 'ADMIN',
}

export const ORDER_STATUS = {
  CREATED: 1,
  DELIVERING: 2,
  FINISHED: 3,
  CANCELLED: 4,
}

export const ORDER_STATUS_TEXT = {
  [ORDER_STATUS.CREATED]: '待发货',
  [ORDER_STATUS.DELIVERING]: '配送中',
  [ORDER_STATUS.FINISHED]: '已完成',
  [ORDER_STATUS.CANCELLED]: '已取消',
}

export const PRODUCT_STATUS_TEXT = {
  0: '已下架',
  1: '上架中',
}

export const PRODUCT_AUDIT_STATUS_TEXT = {
  0: '待审核',
  1: '已通过',
  2: '已驳回',
}

export const DEFAULT_PAGINATION = {
  current: 1,
  size: 10,
  total: 0,
  records: [],
}
