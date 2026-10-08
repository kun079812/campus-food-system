export const money = (value) => Number(value || 0).toFixed(2)
export const dateTime = (value) =>
  value
    ? new Date(value).toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      })
    : '—'
export const statusNames = {
  WAIT_PAY: '待支付',
  WAIT_ACCEPT: '待接单',
  MAKING: '制作中',
  READY_PICKUP: '待取餐',
  DELIVERING: '配送中',
  DELIVERED: '已送达',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  ACCEPTED: '已接单'
}
export const statusName = (status) => statusNames[status] || status
export function nextOrderStatus(order) {
  if (order.status === 'WAIT_ACCEPT') return { code: 'MAKING', label: '接单并制作' }
  if (order.status === 'MAKING')
    return order.deliveryType === 'DELIVERY'
      ? { code: 'DELIVERING', label: '开始配送' }
      : { code: 'READY_PICKUP', label: '通知取餐' }
  if (order.status === 'DELIVERING') return { code: 'DELIVERED', label: '确认送达' }
  return null
}
