<template>
  <div class="cart-page">
    <div class="cart-container">
      <!-- Header -->
      <div class="page-header">
        <h1>购物袋</h1>
        <p v-if="cartStore.items.length">{{ cartStore.items.length }} 件商品</p>
      </div>

      <!-- Cart Content -->
      <div class="cart-content" v-if="cartStore.items.length">
        <div class="cart-main">
          <!-- Select All -->
          <div class="cart-toolbar">
            <label class="select-all">
              <input type="checkbox" :checked="allSelected" @change="handleSelectAll" />
              <span class="checkmark"></span>
              <span>全选</span>
            </label>
            <button class="clear-btn" @click="handleClear">清空购物袋</button>
          </div>

          <!-- Cart Items -->
          <div class="cart-items">
            <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
              <label class="item-checkbox">
                <input type="checkbox" v-model="item.selected" @change="handleSelect(item)" />
                <span class="checkmark"></span>
              </label>
              
              <div class="item-image">
                <img :src="item.productImage" :alt="item.productName" />
              </div>
              
              <div class="item-info">
                <h3 class="item-name">{{ item.productName }}</h3>
                <p class="item-price">¥{{ item.price }}</p>
              </div>
              
              <div class="item-quantity">
                <button class="qty-btn" @click="decreaseQty(item)">−</button>
                <span class="qty-value">{{ item.quantity }}</span>
                <button class="qty-btn" @click="increaseQty(item)">+</button>
              </div>
              
              <div class="item-subtotal">
                <span class="subtotal-label">小计</span>
                <span class="subtotal-value">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
              </div>
              
              <button class="item-remove" @click="handleDelete(item)">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <!-- Summary -->
        <div class="cart-summary">
          <div class="summary-card">
            <h3>订单摘要</h3>
            <div class="summary-row">
              <span>已选商品</span>
              <span>{{ cartStore.selectedCount }} 件</span>
            </div>
            <div class="summary-row">
              <span>商品金额</span>
              <span>¥{{ cartStore.totalPrice.toFixed(2) }}</span>
            </div>
            <div class="summary-row">
              <span>运费</span>
              <span class="free">免运费</span>
            </div>
            <div class="summary-divider"></div>
            <div class="summary-total">
              <span>合计</span>
              <span class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
            </div>
            <button class="checkout-btn" :disabled="!cartStore.selectedItems.length" @click="showCheckout = true">
              去结算
            </button>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M6 2L3 6V20C3 20.5304 3.21071 21.0391 3.58579 21.4142C3.96086 21.7893 4.46957 22 5 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6L18 2H6Z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M3 6H21" stroke="currentColor" stroke-width="1.5"/>
            <path d="M16 10C16 11.0609 15.5786 12.0783 14.8284 12.8284C14.0783 13.5786 13.0609 14 12 14C10.9391 14 9.92172 13.5786 9.17157 12.8284C8.42143 12.0783 8 11.0609 8 10" stroke="currentColor" stroke-width="1.5"/>
          </svg>
        </div>
        <h2>购物袋是空的</h2>
        <p>快去挑选心仪的商品吧</p>
        <router-link to="/home" class="shop-btn">去购物</router-link>
      </div>
    </div>

    <!-- Checkout Dialog -->
    <el-dialog v-model="showCheckout" title="确认订单" width="500px" class="checkout-dialog">
      <el-form ref="checkoutFormRef" :model="checkoutForm" :rules="checkoutRules" label-position="top">
        <el-form-item label="收货人" prop="receiver">
          <el-input v-model="checkoutForm.receiver" placeholder="请输入收货人姓名" size="large" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="checkoutForm.phone" placeholder="请输入联系电话" size="large" />
        </el-form-item>
        <el-form-item label="收货地址" prop="address">
          <el-input v-model="checkoutForm.address" type="textarea" :rows="3" placeholder="请输入详细收货地址" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="checkoutForm.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <button class="dialog-btn cancel" @click="showCheckout = false">取消</button>
        <button class="dialog-btn confirm" :disabled="submitting" @click="handleCheckout">
          {{ submitting ? '提交中...' : '确认下单' }}
        </button>
      </template>
    </el-dialog>

    <!-- Payment Dialog -->
    <el-dialog v-model="showPayment" title="订单支付" width="480px" :close-on-click-modal="false" class="payment-dialog">
      <div class="payment-content">
        <div class="payment-amount">
          <span class="amount-label">支付金额</span>
          <span class="amount-value">¥{{ paymentAmount.toFixed(2) }}</span>
        </div>
        
        <div class="payment-methods">
          <div 
            v-for="method in paymentMethods" 
            :key="method.id"
            class="payment-method"
            :class="{ active: selectedPayment === method.id }"
            @click="selectedPayment = method.id"
          >
            <div class="method-icon" :style="{ background: method.color }">
              <span v-html="method.icon"></span>
            </div>
            <div class="method-info">
              <span class="method-name">{{ method.name }}</span>
              <span class="method-desc">{{ method.desc }}</span>
            </div>
            <div class="method-check">
              <svg v-if="selectedPayment === method.id" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="10" fill="currentColor"/>
                <path d="M8 12L11 15L16 9" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <svg v-else viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5"/>
              </svg>
            </div>
          </div>
        </div>

        <!-- Payment Processing -->
        <div v-if="paymentStep === 'processing'" class="payment-processing">
          <div class="processing-spinner"></div>
          <p>正在处理支付...</p>
        </div>

        <!-- Payment Success -->
        <div v-if="paymentStep === 'success'" class="payment-success">
          <div class="success-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" fill="#34C759"/>
              <path d="M8 12L11 15L16 9" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>支付成功</h3>
          <p>感谢您的购买，订单已提交</p>
        </div>
      </div>
      
      <template #footer>
        <template v-if="paymentStep === 'select'">
          <button class="dialog-btn cancel" @click="cancelPayment">取消支付</button>
          <button class="dialog-btn confirm" @click="processPayment">
            确认支付 ¥{{ paymentAmount.toFixed(2) }}
          </button>
        </template>
        <template v-else-if="paymentStep === 'success'">
          <button class="dialog-btn confirm" @click="finishPayment">查看订单</button>
        </template>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '../stores/cart'
import { orderApi } from '../api'

const router = useRouter()
const cartStore = useCartStore()

const showCheckout = ref(false)
const submitting = ref(false)
const checkoutFormRef = ref()

const checkoutForm = reactive({ receiver: '', phone: '', address: '', remark: '' })
const checkoutRules = {
  receiver: [
    { required: true, message: '请输入收货人', trigger: 'blur' },
    { min: 2, max: 20, message: '收货人姓名2-20个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入收货地址', trigger: 'blur' },
    { min: 5, max: 200, message: '地址长度5-200个字符', trigger: 'blur' }
  ]
}

// Payment related
const showPayment = ref(false)
const paymentStep = ref('select') // select, processing, success
const selectedPayment = ref('alipay')
const paymentAmount = ref(0)
const currentOrderId = ref(null)

const paymentMethods = [
  { id: 'alipay', name: '支付宝', desc: '推荐使用', color: '#1677FF', icon: '<svg viewBox="0 0 24 24" fill="none" width="24" height="24"><circle cx="12" cy="12" r="10" fill="white"/><text x="12" y="16" text-anchor="middle" font-size="10" font-weight="bold" fill="#1677FF">支</text></svg>' },
  { id: 'wechat', name: '微信支付', desc: '微信扫码支付', color: '#07C160', icon: '<svg viewBox="0 0 24 24" fill="white" width="24" height="24"><path d="M9.5 8.5a1 1 0 1 1-2 0 1 1 0 0 1 2 0zm5 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0z"/><path d="M12 3c-4.97 0-9 3.58-9 8 0 2.4 1.2 4.55 3.08 6.04L5 20l3.5-1.8c1.1.3 2.27.47 3.5.47 4.97 0 9-3.58 9-8s-4.03-8-9-8z"/></svg>' },
  { id: 'card', name: '银行卡', desc: '储蓄卡/信用卡', color: '#FF6B35', icon: '<svg viewBox="0 0 24 24" fill="white" width="24" height="24"><rect x="3" y="5" width="18" height="14" rx="2"/><rect x="3" y="9" width="18" height="3" fill="#FF6B35" opacity="0.5"/></svg>' }
]

const allSelected = computed(() => cartStore.isAllSelected)

const handleSelectAll = (e) => { cartStore.selectAll(e.target.checked) }
const handleSelect = (row) => { cartStore.updateSelected(row.id, row.selected) }

const increaseQty = (item) => {
  if (item.quantity >= item.stock) {
    ElMessage.warning('已达到最大库存数量')
    return
  }
  item.quantity++
  cartStore.updateQuantity(item.id, item.quantity)
}

const decreaseQty = (item) => {
  if (item.quantity > 1) {
    item.quantity--
    cartStore.updateQuantity(item.id, item.quantity)
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要移除该商品吗？', '提示', { type: 'warning' })
  await cartStore.removeItem(row.id)
  ElMessage.success('已移除')
}

const handleClear = async () => {
  await ElMessageBox.confirm('确定要清空购物袋吗？', '提示', { type: 'warning' })
  await cartStore.clearCart()
  ElMessage.success('已清空')
}

const handleCheckout = async () => {
  await checkoutFormRef.value.validate()
  submitting.value = true
  try {
    const res = await orderApi.create(checkoutForm)
    showCheckout.value = false
    // Show payment dialog
    paymentAmount.value = cartStore.totalPrice
    currentOrderId.value = res.data?.id
    paymentStep.value = 'select'
    showPayment.value = true
  } finally {
    submitting.value = false
  }
}

const processPayment = () => {
  paymentStep.value = 'processing'
  // Simulate payment processing
  setTimeout(async () => {
    // Update order status to paid
    if (currentOrderId.value) {
      try {
        await orderApi.updateStatus(currentOrderId.value, 1)
      } catch (e) {
        // ignore
      }
    }
    paymentStep.value = 'success'
  }, 2000)
}

const cancelPayment = () => {
  showPayment.value = false
  ElMessage.info('订单已创建，可稍后在订单页面支付')
  router.push('/order')
}

const finishPayment = () => {
  showPayment.value = false
  ElMessage.success('支付成功')
  router.push('/order')
}

onMounted(() => { cartStore.fetchCart() })
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  background: var(--color-off-white);
  padding: var(--space-xl) 0 var(--space-4xl);
}

.cart-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-xl);
}

.page-header {
  margin-bottom: var(--space-xl);
}

.page-header h1 {
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.page-header p {
  color: var(--color-medium-gray);
  margin-top: var(--space-xs);
}

/* Cart Content */
.cart-content {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: var(--space-xl);
  align-items: start;
}

.cart-main {
  background: var(--color-white);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

/* Toolbar */
.cart-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-lg) var(--space-xl);
  border-bottom: 1px solid var(--color-light-gray);
}

.select-all {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  cursor: pointer;
  font-size: 0.9375rem;
}

.select-all input { display: none; }

.checkmark {
  width: 20px;
  height: 20px;
  border: 2px solid var(--color-medium-gray);
  border-radius: 4px;
  position: relative;
  transition: all var(--transition-fast);
}

.select-all input:checked + .checkmark {
  background: var(--color-primary);
  border-color: var(--color-primary);
}

.select-all input:checked + .checkmark::after {
  content: '';
  position: absolute;
  left: 6px;
  top: 2px;
  width: 5px;
  height: 10px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.clear-btn {
  background: none;
  border: none;
  color: var(--color-medium-gray);
  font-size: 0.875rem;
  cursor: pointer;
  transition: color var(--transition-fast);
}

.clear-btn:hover { color: var(--color-error); }

/* Cart Items */
.cart-items {
  padding: var(--space-md);
}

.cart-item {
  display: flex;
  align-items: center;
  gap: var(--space-lg);
  padding: var(--space-lg);
  border-radius: var(--radius-md);
  transition: background var(--transition-fast);
}

.cart-item:hover {
  background: var(--color-light-gray);
}

.item-checkbox {
  cursor: pointer;
}

.item-checkbox input { display: none; }

.item-checkbox .checkmark {
  width: 20px;
  height: 20px;
  border: 2px solid var(--color-medium-gray);
  border-radius: 4px;
  position: relative;
  transition: all var(--transition-fast);
}

.item-checkbox input:checked + .checkmark {
  background: var(--color-primary);
  border-color: var(--color-primary);
}

.item-checkbox input:checked + .checkmark::after {
  content: '';
  position: absolute;
  left: 6px;
  top: 2px;
  width: 5px;
  height: 10px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.item-image {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--color-light-gray);
  flex-shrink: 0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 1rem;
  font-weight: 500;
  margin-bottom: var(--space-xs);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-price {
  color: var(--color-medium-gray);
  font-size: 0.875rem;
}

.item-quantity {
  display: flex;
  align-items: center;
  border: 1px solid var(--color-light-gray);
  border-radius: var(--radius-sm);
  overflow: hidden;
}

.qty-btn {
  width: 36px;
  height: 36px;
  background: var(--color-white);
  border: none;
  font-size: 1rem;
  cursor: pointer;
  transition: background var(--transition-fast);
}

.qty-btn:hover { background: var(--color-light-gray); }

.qty-value {
  width: 48px;
  text-align: center;
  font-size: 0.9375rem;
  font-weight: 500;
}

.item-subtotal {
  text-align: right;
  min-width: 100px;
}

.subtotal-label {
  display: block;
  font-size: 0.75rem;
  color: var(--color-medium-gray);
  margin-bottom: 2px;
}

.subtotal-value {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-primary);
}

.item-remove {
  width: 36px;
  height: 36px;
  background: none;
  border: none;
  color: var(--color-medium-gray);
  cursor: pointer;
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
}

.item-remove:hover {
  background: rgba(255, 59, 48, 0.1);
  color: var(--color-error);
}

.item-remove svg {
  width: 18px;
  height: 18px;
}

/* Summary */
.cart-summary {
  position: sticky;
  top: 100px;
}

.summary-card {
  background: var(--color-white);
  border-radius: var(--radius-xl);
  padding: var(--space-xl);
  box-shadow: var(--shadow-sm);
}

.summary-card h3 {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: var(--space-lg);
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--space-md);
  font-size: 0.9375rem;
}

.summary-row .free {
  color: var(--color-success);
}

.summary-divider {
  height: 1px;
  background: var(--color-light-gray);
  margin: var(--space-lg) 0;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: var(--space-xl);
}

.total-price {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--color-primary);
}

.checkout-btn {
  width: 100%;
  padding: 16px;
  background: var(--color-primary);
  color: var(--color-white);
  border: none;
  border-radius: var(--radius-full);
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.checkout-btn:hover:not(:disabled) {
  background: var(--color-secondary);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.checkout-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: var(--space-4xl) var(--space-xl);
  background: var(--color-white);
  border-radius: var(--radius-xl);
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto var(--space-lg);
  color: var(--color-medium-gray);
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

.empty-state h2 {
  font-size: 1.5rem;
  margin-bottom: var(--space-sm);
}

.empty-state p {
  color: var(--color-medium-gray);
  margin-bottom: var(--space-xl);
}

.shop-btn {
  display: inline-block;
  padding: 14px 40px;
  background: var(--color-primary);
  color: var(--color-white);
  border-radius: var(--radius-full);
  font-weight: 500;
  transition: all var(--transition-fast);
}

.shop-btn:hover {
  background: var(--color-secondary);
  transform: translateY(-1px);
}

/* Dialog */
.dialog-btn {
  padding: 12px 28px;
  border-radius: var(--radius-full);
  font-size: 0.9375rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.dialog-btn.cancel {
  background: var(--color-light-gray);
  border: none;
  color: var(--color-dark-gray);
  margin-right: var(--space-sm);
}

.dialog-btn.confirm {
  background: var(--color-primary);
  border: none;
  color: var(--color-white);
}

.dialog-btn.confirm:hover:not(:disabled) {
  background: var(--color-secondary);
}

/* Responsive */
@media (max-width: 968px) {
  .cart-content {
    grid-template-columns: 1fr;
  }

  .cart-summary {
    position: static;
  }

  .cart-item {
    flex-wrap: wrap;
  }

  .item-info {
    width: calc(100% - 140px);
  }

  .item-quantity, .item-subtotal {
    margin-left: auto;
  }
}

/* Payment Dialog */
.payment-content {
  padding: var(--space-md) 0;
}

.payment-amount {
  text-align: center;
  padding: var(--space-xl);
  background: var(--color-light-gray);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-xl);
}

.amount-label {
  display: block;
  font-size: 0.875rem;
  color: var(--color-medium-gray);
  margin-bottom: var(--space-xs);
}

.amount-value {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--color-primary);
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.payment-method {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-lg);
  border: 2px solid var(--color-light-gray);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.payment-method:hover {
  border-color: var(--color-medium-gray);
}

.payment-method.active {
  border-color: var(--color-primary);
  background: rgba(0, 0, 0, 0.02);
}

.method-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.method-icon span {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.method-icon span svg {
  width: 24px;
  height: 24px;
}

.method-info {
  flex: 1;
}

.method-name {
  display: block;
  font-weight: 600;
  margin-bottom: 2px;
}

.method-desc {
  font-size: 0.8125rem;
  color: var(--color-medium-gray);
}

.method-check {
  width: 24px;
  height: 24px;
  color: var(--color-primary);
}

.method-check svg {
  width: 100%;
  height: 100%;
}

/* Payment Processing */
.payment-processing {
  text-align: center;
  padding: var(--space-2xl);
}

.processing-spinner {
  width: 48px;
  height: 48px;
  border: 3px solid var(--color-light-gray);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  margin: 0 auto var(--space-lg);
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.payment-processing p {
  color: var(--color-medium-gray);
}

/* Payment Success */
.payment-success {
  text-align: center;
  padding: var(--space-xl);
}

.success-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto var(--space-lg);
}

.success-icon svg {
  width: 100%;
  height: 100%;
}

.payment-success h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: var(--space-xs);
}

.payment-success p {
  color: var(--color-medium-gray);
}
</style>
