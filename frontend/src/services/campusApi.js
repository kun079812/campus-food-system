// B-system uses JSESSIONID cookies, not bearer tokens. Vite proxies /api to Spring Boot.
export class ApiError extends Error {
  constructor(message, status) {
    super(message)
    this.status = status
  }
}

export async function request(path, { method = 'GET', body, signal } = {}) {
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), 12000)
  try {
    const response = await fetch(`/api${path}`, {
      method,
      credentials: 'include',
      signal: signal || controller.signal,
      headers: {
        Accept: 'application/json',
        ...(body !== undefined ? { 'Content-Type': 'application/json' } : {})
      },
      ...(body !== undefined ? { body: JSON.stringify(body) } : {})
    })
    const text = await response.text()
    let result
    try {
      result = text ? JSON.parse(text) : null
    } catch {
      throw new ApiError('暂时无法连接食堂服务，请确认 Spring Boot 后端已启动。', response.status)
    }
    if (!response.ok || (result?.code !== undefined && result.code !== 200)) {
      throw new ApiError(
        result?.message || '请求失败，请稍后重试。',
        result?.code || response.status
      )
    }
    return result && Object.hasOwn(result, 'data') ? result.data : result
  } catch (error) {
    if (error instanceof ApiError) throw error
    throw new ApiError(
      error.name === 'AbortError'
        ? '请求超时，请稍后重试。'
        : '暂时无法连接食堂服务，请确认 Spring Boot 后端已启动。',
      0
    )
  } finally {
    clearTimeout(timer)
  }
}

export const api = {
  me: () => request('/users/me'),
  login: (body) => request('/auth/login', { method: 'POST', body }),
  register: (body) => request('/auth/register', { method: 'POST', body }),
  logout: () => request('/auth/logout', { method: 'POST' }),
  profile: (body) => request('/users/me', { method: 'PUT', body }),
  password: (body) => request('/users/me/password', { method: 'PUT', body }),
  dishes: () => request('/dishes'),
  canteens: () => request('/canteens'),
  stalls: (id) => request(`/canteens/${id}/stalls`),
  categories: (id) => request(`/stalls/${id}/dish-categories`),
  cart: () => request('/cart/items'),
  addCart: (dishId) => request('/cart/items', { method: 'POST', body: { dishId, quantity: 1 } }),
  cartQuantity: (dishId, quantity) =>
    request(`/cart/items/${dishId}`, { method: 'PUT', body: { quantity } }),
  removeCart: (dishId) => request(`/cart/items/${dishId}`, { method: 'DELETE' }),
  orders: (merchant = false) => request(merchant ? '/merchant/orders' : '/orders'),
  orderDetail: (id, merchant = false) => request(`${merchant ? '/merchant' : ''}/orders/${id}`),
  createOrder: (body) => request('/orders', { method: 'POST', body }),
  pay: (id) => request(`/orders/${id}/pay`, { method: 'POST', body: { paymentMethod: 'MOCK' } }),
  cancel: (id) => request(`/orders/${id}/cancel`, { method: 'POST' }),
  confirm: (id, deliveryType) =>
    request(`/orders/${id}/${deliveryType === 'DELIVERY' ? 'confirm-receipt' : 'confirm-pickup'}`, {
      method: 'POST'
    }),
  orderStatus: (id, status) =>
    request(`/merchant/orders/${id}/status?status=${encodeURIComponent(status)}`, {
      method: 'PUT'
    }),
  addresses: () => request('/addresses'),
  saveAddress: (body) =>
    request(`/addresses${body.id ? `/${body.id}` : ''}`, {
      method: body.id ? 'PUT' : 'POST',
      body
    }),
  deleteAddress: (id) => request(`/addresses/${id}`, { method: 'DELETE' }),
  merchantDishes: () => request('/merchant/dishes'),
  merchantCategories: () => request('/merchant/dish-categories'),
  saveCategory: (name) => request('/merchant/dish-categories', { method: 'POST', body: { name } }),
  saveDish: (body) =>
    request(`/merchant/dishes${body.id ? `/${body.id}` : ''}`, {
      method: body.id ? 'PUT' : 'POST',
      body
    }),
  dishStatus: (id, status) =>
    request(`/merchant/dishes/${id}/status?status=${status}`, { method: 'PUT' }),
  stall: () => request('/merchant/stall'),
  saveStall: (body) => request('/merchant/stall', { method: 'PUT', body }),
  stallStatus: (status) => request(`/merchant/stall/status?status=${status}`, { method: 'PUT' }),
  summary: () => request('/merchant/dashboard/summary'),
  sales: () => request('/merchant/dashboard/dish-sales'),
  reviews: (merchant = false) => request(merchant ? '/merchant/reviews' : '/student/reviews'),
  review: (body) => request('/reviews', { method: 'POST', body })
}
