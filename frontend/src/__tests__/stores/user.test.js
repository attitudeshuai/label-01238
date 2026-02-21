import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '../../stores/user'

// Mock localStorage
const localStorageMock = {
  store: {},
  getItem: vi.fn((key) => localStorageMock.store[key] || null),
  setItem: vi.fn((key, value) => { localStorageMock.store[key] = value }),
  removeItem: vi.fn((key) => { delete localStorageMock.store[key] }),
  clear: vi.fn(() => { localStorageMock.store = {} })
}
Object.defineProperty(global, 'localStorage', { value: localStorageMock })

describe('User Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
    vi.clearAllMocks()
  })

  it('初始状态应为未登录', () => {
    const store = useUserStore()
    expect(store.isLoggedIn).toBe(false)
    expect(store.isAdmin).toBe(false)
    expect(store.username).toBe('')
  })

  it('登录后应更新状态', () => {
    const store = useUserStore()
    const mockUser = { id: 1, username: 'testuser', role: 'user' }
    
    store.login('test-token', mockUser)
    
    expect(store.isLoggedIn).toBe(true)
    expect(store.token).toBe('test-token')
    expect(store.user).toEqual(mockUser)
    expect(store.username).toBe('testuser')
    expect(localStorageMock.setItem).toHaveBeenCalledWith('token', 'test-token')
  })

  it('管理员登录后 isAdmin 应为 true', () => {
    const store = useUserStore()
    const adminUser = { id: 1, username: 'admin', role: 'admin' }
    
    store.login('admin-token', adminUser)
    
    expect(store.isAdmin).toBe(true)
  })

  it('登出后应清除状态', () => {
    const store = useUserStore()
    store.login('test-token', { id: 1, username: 'test', role: 'user' })
    
    store.logout()
    
    expect(store.isLoggedIn).toBe(false)
    expect(store.token).toBe('')
    expect(store.user).toBeNull()
    expect(localStorageMock.removeItem).toHaveBeenCalledWith('token')
    expect(localStorageMock.removeItem).toHaveBeenCalledWith('user')
  })

  it('updateUser 应更新用户信息', () => {
    const store = useUserStore()
    store.login('test-token', { id: 1, username: 'old', role: 'user' })
    
    const newUserInfo = { id: 1, username: 'new', role: 'user' }
    store.updateUser(newUserInfo)
    
    expect(store.user).toEqual(newUserInfo)
    expect(store.username).toBe('new')
  })
})
