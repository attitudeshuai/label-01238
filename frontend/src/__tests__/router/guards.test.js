import { describe, it, expect, beforeEach, vi } from 'vitest'

// Mock localStorage
const localStorageMock = {
  store: {},
  getItem: vi.fn((key) => localStorageMock.store[key] || null),
  setItem: vi.fn((key, value) => { localStorageMock.store[key] = value }),
  removeItem: vi.fn((key) => { delete localStorageMock.store[key] }),
  clear: vi.fn(() => { localStorageMock.store = {} })
}
Object.defineProperty(global, 'localStorage', { value: localStorageMock })

describe('Router Guards', () => {
  beforeEach(() => {
    localStorageMock.clear()
    vi.clearAllMocks()
  })

  // 模拟路由守卫逻辑
  const checkAuth = (to, token, user) => {
    if (to.meta.requireAuth && !token) {
      return { path: '/login', query: { redirect: to.fullPath } }
    }
    if (to.meta.requireAdmin && (!user || user.role !== 'admin')) {
      return { path: '/home' }
    }
    return null // 允许通过
  }

  it('未登录用户访问需要认证的页面应重定向到登录页', () => {
    const to = { meta: { requireAuth: true }, fullPath: '/cart' }
    const result = checkAuth(to, null, null)
    
    expect(result).toEqual({ path: '/login', query: { redirect: '/cart' } })
  })

  it('已登录用户可以访问需要认证的页面', () => {
    const to = { meta: { requireAuth: true }, fullPath: '/cart' }
    const result = checkAuth(to, 'valid-token', { role: 'user' })
    
    expect(result).toBeNull()
  })

  it('非管理员访问管理页面应重定向到首页', () => {
    const to = { meta: { requireAuth: true, requireAdmin: true }, fullPath: '/admin/dashboard' }
    const result = checkAuth(to, 'valid-token', { role: 'user' })
    
    expect(result).toEqual({ path: '/home' })
  })

  it('管理员可以访问管理页面', () => {
    const to = { meta: { requireAuth: true, requireAdmin: true }, fullPath: '/admin/dashboard' }
    const result = checkAuth(to, 'valid-token', { role: 'admin' })
    
    expect(result).toBeNull()
  })

  it('公开页面无需认证', () => {
    const to = { meta: {}, fullPath: '/home' }
    const result = checkAuth(to, null, null)
    
    expect(result).toBeNull()
  })
})
