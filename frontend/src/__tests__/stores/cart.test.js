import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useCartStore } from '../../stores/cart'

// Mock cart API
vi.mock('../../api', () => ({
  cartApi: {
    list: vi.fn(),
    add: vi.fn(),
    updateQuantity: vi.fn(),
    updateSelected: vi.fn(),
    selectAll: vi.fn(),
    delete: vi.fn(),
    clear: vi.fn()
  }
}))

import { cartApi } from '../../api'

describe('Cart Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('初始状态应为空购物车', () => {
    const store = useCartStore()
    expect(store.items).toEqual([])
    expect(store.totalCount).toBe(0)
    expect(store.totalPrice).toBe(0)
  })

  it('totalCount 应正确计算商品总数', () => {
    const store = useCartStore()
    store.items = [
      { id: 1, quantity: 2, price: 100, selected: true },
      { id: 2, quantity: 3, price: 200, selected: true }
    ]
    expect(store.totalCount).toBe(5)
  })

  it('totalPrice 应只计算选中商品的总价', () => {
    const store = useCartStore()
    store.items = [
      { id: 1, quantity: 2, price: 100, selected: true },
      { id: 2, quantity: 1, price: 200, selected: false }
    ]
    expect(store.totalPrice).toBe(200) // 只计算选中的: 2 * 100
  })

  it('selectedItems 应返回选中的商品', () => {
    const store = useCartStore()
    store.items = [
      { id: 1, quantity: 1, price: 100, selected: true },
      { id: 2, quantity: 1, price: 200, selected: false }
    ]
    expect(store.selectedItems).toHaveLength(1)
    expect(store.selectedItems[0].id).toBe(1)
  })

  it('isAllSelected 应正确判断是否全选', () => {
    const store = useCartStore()
    
    // 空购物车
    expect(store.isAllSelected).toBe(false)
    
    // 全部选中
    store.items = [
      { id: 1, selected: true },
      { id: 2, selected: true }
    ]
    expect(store.isAllSelected).toBe(true)
    
    // 部分选中
    store.items[1].selected = false
    expect(store.isAllSelected).toBe(false)
  })

  it('fetchCart 应从 API 获取购物车数据', async () => {
    const mockItems = [{ id: 1, quantity: 1, price: 100, selected: true }]
    cartApi.list.mockResolvedValue({ data: mockItems })
    
    const store = useCartStore()
    await store.fetchCart()
    
    expect(cartApi.list).toHaveBeenCalled()
    expect(store.items).toEqual(mockItems)
  })

  it('removeItem 应删除指定商品', async () => {
    cartApi.delete.mockResolvedValue({})
    
    const store = useCartStore()
    store.items = [
      { id: 1, quantity: 1 },
      { id: 2, quantity: 1 }
    ]
    
    await store.removeItem(1)
    
    expect(cartApi.delete).toHaveBeenCalledWith(1)
    expect(store.items).toHaveLength(1)
    expect(store.items[0].id).toBe(2)
  })
})
