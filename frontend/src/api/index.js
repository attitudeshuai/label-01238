import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

// 认证相关API
export const authApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  getInfo: () => api.get('/auth/info')
}

// 商品相关API
export const productApi = {
  list: () => api.get('/product/list'),
  all: () => api.get('/product/all'),
  detail: (id) => api.get(`/product/detail/${id}`),
  search: (keyword) => api.get('/product/search', { params: { keyword } }),
  byCategory: (categoryId) => api.get(`/product/category/${categoryId}`),
  hot: (limit = 10) => api.get('/product/hot', { params: { limit } }),
  add: (data) => api.post('/product/add', data),
  update: (data) => api.put('/product/update', data),
  delete: (id) => api.delete(`/product/delete/${id}`)
}

// 分类相关API
export const categoryApi = {
  list: () => api.get('/category/list'),
  add: (data) => api.post('/category/add', data),
  update: (data) => api.put('/category/update', data),
  delete: (id) => api.delete(`/category/delete/${id}`)
}

// 购物车相关API
export const cartApi = {
  list: () => api.get('/cart/list'),
  add: (data) => api.post('/cart/add', data),
  updateQuantity: (id, quantity) => api.put(`/cart/update/${id}`, { quantity }),
  updateSelected: (id, selected) => api.put(`/cart/select/${id}`, { selected }),
  selectAll: (selected) => api.put('/cart/selectAll', { selected }),
  delete: (id) => api.delete(`/cart/delete/${id}`),
  clear: () => api.delete('/cart/clear')
}

// 订单相关API
export const orderApi = {
  list: () => api.get('/order/list'),
  all: () => api.get('/order/all'),
  detail: (id) => api.get(`/order/detail/${id}`),
  create: (data) => api.post('/order/create', data),
  updateStatus: (id, status) => api.put(`/order/status/${id}`, { status }),
  delete: (id) => api.delete(`/order/delete/${id}`)
}

// 用户相关API
export const userApi = {
  list: () => api.get('/user/list'),
  detail: (id) => api.get(`/user/detail/${id}`),
  add: (data) => api.post('/user/add', data),
  update: (data) => api.put('/user/update', data),
  delete: (id) => api.delete(`/user/delete/${id}`),
  updateStatus: (id, status) => api.put(`/user/status/${id}`, { status })
}

// 统计相关API
export const statisticsApi = {
  overview: () => api.get('/statistics/overview'),
  orderStatus: () => api.get('/statistics/orderStatus'),
  recentOrders: (days = 7) => api.get('/statistics/recentOrders', { params: { days } }),
  monthlySales: (months = 6) => api.get('/statistics/monthlySales', { params: { months } }),
  hotProducts: (limit = 10) => api.get('/statistics/hotProducts', { params: { limit } })
}

// AI客服API
export const aiApi = {
  chat: (message) => api.post('/ai/chat', { message })
}

export default api
