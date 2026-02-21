import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { cartApi } from '../api'

// 购物车状态管理
export const useCartStore = defineStore('cart', () => {
  // 状态
  const items = ref([])
  const loading = ref(false)

  // 计算属性
  const totalCount = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))
  const selectedItems = computed(() => items.value.filter(item => item.selected))
  const selectedCount = computed(() => selectedItems.value.reduce((sum, item) => sum + item.quantity, 0))
  const totalPrice = computed(() => 
    selectedItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  )
  const isAllSelected = computed(() => items.value.length > 0 && items.value.every(item => item.selected))

  // 获取购物车列表
  async function fetchCart() {
    loading.value = true
    try {
      const res = await cartApi.list()
      items.value = res.data || []
    } finally {
      loading.value = false
    }
  }

  // 添加商品到购物车
  async function addToCart(productId, quantity = 1) {
    await cartApi.add({ productId, quantity })
    await fetchCart()
  }

  // 更新数量
  async function updateQuantity(id, quantity) {
    await cartApi.updateQuantity(id, quantity)
    const item = items.value.find(i => i.id === id)
    if (item) item.quantity = quantity
  }

  // 更新选中状态
  async function updateSelected(id, selected) {
    await cartApi.updateSelected(id, selected)
    const item = items.value.find(i => i.id === id)
    if (item) item.selected = selected
  }

  // 全选/取消全选
  async function selectAll(selected) {
    await cartApi.selectAll(selected)
    items.value.forEach(item => item.selected = selected)
  }

  // 删除购物车项
  async function removeItem(id) {
    await cartApi.delete(id)
    items.value = items.value.filter(item => item.id !== id)
  }

  // 清空购物车
  async function clearCart() {
    await cartApi.clear()
    items.value = []
  }

  return {
    items,
    loading,
    totalCount,
    selectedItems,
    selectedCount,
    totalPrice,
    isAllSelected,
    fetchCart,
    addToCart,
    updateQuantity,
    updateSelected,
    selectAll,
    removeItem,
    clearCart
  }
})
