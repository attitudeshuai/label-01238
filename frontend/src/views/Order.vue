<template>
  <div class="order-page">
    <div class="order-container">
      <!-- Header -->
      <div class="page-header">
        <h1>我的订单</h1>
        <p>查看您的所有订单记录</p>
      </div>

      <!-- Orders List -->
      <div class="orders-list" v-loading="loading">
        <div v-for="order in paginatedOrders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <span class="order-time">{{ order.createTime }}</span>
            </div>
            <div class="order-status" :class="statusClass[order.status]">
              {{ statusText[order.status] }}
            </div>
          </div>

          <div class="order-items">
            <div v-for="item in order.items" :key="item.id" class="order-item">
              <div class="item-image">
                <img :src="item.productImage" :alt="item.productName" />
              </div>
              <div class="item-info">
                <h4>{{ item.productName }}</h4>
                <p>¥{{ item.price }} × {{ item.quantity }}</p>
              </div>
              <div class="item-subtotal">¥{{ item.subtotal }}</div>
            </div>
          </div>

          <!-- Order Details -->
          <div class="order-details">
            <div class="detail-row">
              <span class="detail-label">收货人</span>
              <span class="detail-value">{{ order.receiver }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">联系电话</span>
              <span class="detail-value">{{ order.phone }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">收货地址</span>
              <span class="detail-value">{{ order.address }}</span>
            </div>
            <div class="detail-row" v-if="order.remark">
              <span class="detail-label">备注</span>
              <span class="detail-value">{{ order.remark }}</span>
            </div>
          </div>

          <div class="order-footer">
            <div class="order-total">
              <span>订单金额：</span>
              <span class="total-amount">¥{{ order.totalAmount }}</span>
            </div>
            <button v-if="order.status === 0" class="pay-btn" @click="openPayment(order)">
              去支付
            </button>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div class="pagination-wrapper" v-if="orders.length > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="orders.length"
          layout="prev, pager, next"
          background
        />
      </div>

      <!-- Empty State -->
      <div v-if="!loading && orders.length === 0" class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M9 5H7C5.89543 5 5 5.89543 5 7V19C5 20.1046 5.89543 21 7 21H17C18.1046 21 19 20.1046 19 19V7C19 5.89543 18.1046 5 17 5H15" stroke="currentColor" stroke-width="1.5"/>
            <path d="M9 5C9 3.89543 9.89543 3 11 3H13C14.1046 3 15 3.89543 15 5C15 6.10457 14.1046 7 13 7H11C9.89543 7 9 6.10457 9 5Z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M9 12H15M9 16H12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
        </div>
        <h2>暂无订单</h2>
        <p>快去挑选心仪的商品吧</p>
        <router-link to="/home" class="shop-btn">去购物</router-link>
      </div>
    </div>

    <!-- Payment Dialog -->
    <el-dialog v-model="showPayment" title="订单支付" width="480px" :close-on-click-modal="false">
      <div class="payment-content">
        <div class="payment-amount">
          <span class="amount-label">支付金额</span>
          <span class="amount-value">¥{{ paymentAmount.toFixed(2) }}</span>
        </div>
        
        <div class="payment-methods" v-if="paymentStep === 'select'">
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

        <div v-if="paymentStep === 'processing'" class="payment-processing">
          <div class="processing-spinner"></div>
          <p>正在处理支付...</p>
        </div>

        <div v-if="paymentStep === 'success'" class="payment-success">
          <div class="success-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" fill="#34C759"/>
              <path d="M8 12L11 15L16 9" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>支付成功</h3>
          <p>感谢您的购买</p>
        </div>
      </div>
      
      <template #footer>
        <template v-if="paymentStep === 'select'">
          <button class="dialog-btn cancel" @click="showPayment = false">取消</button>
          <button class="dialog-btn confirm" @click="processPayment">确认支付</button>
        </template>
        <template v-else-if="paymentStep === 'success'">
          <button class="dialog-btn confirm" @click="finishPayment">完成</button>
        </template>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { orderApi } from '../api'
import { ElMessage } from 'element-plus'

const orders = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 5

const statusText = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已完成', 4: '已取消' }
const statusClass = { 0: 'pending', 1: 'paid', 2: 'shipped', 3: 'completed', 4: 'cancelled' }

const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return orders.value.slice(start, start + pageSize)
})

// Payment
const showPayment = ref(false)
const paymentStep = ref('select')
const selectedPayment = ref('alipay')
const paymentAmount = ref(0)
const currentOrderId = ref(null)

const paymentMethods = [
  { id: 'alipay', name: '支付宝', desc: '推荐使用', color: '#1677FF', icon: '<svg viewBox="0 0 24 24" fill="none" width="24" height="24"><circle cx="12" cy="12" r="10" fill="white"/><text x="12" y="16" text-anchor="middle" font-size="10" font-weight="bold" fill="#1677FF">支</text></svg>' },
  { id: 'wechat', name: '微信支付', desc: '微信扫码支付', color: '#07C160', icon: '<svg viewBox="0 0 24 24" fill="white" width="24" height="24"><path d="M9.5 8.5a1 1 0 1 1-2 0 1 1 0 0 1 2 0zm5 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0z"/><path d="M12 3c-4.97 0-9 3.58-9 8 0 2.4 1.2 4.55 3.08 6.04L5 20l3.5-1.8c1.1.3 2.27.47 3.5.47 4.97 0 9-3.58 9-8s-4.03-8-9-8z"/></svg>' },
  { id: 'card', name: '银行卡', desc: '储蓄卡/信用卡', color: '#FF6B35', icon: '<svg viewBox="0 0 24 24" fill="white" width="24" height="24"><rect x="3" y="5" width="18" height="14" rx="2"/><rect x="3" y="9" width="18" height="3" fill="#FF6B35" opacity="0.5"/></svg>' }
]

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await orderApi.list()
    orders.value = res.data || []
  } finally {
    loading.value = false
  }
}

const openPayment = (order) => {
  paymentAmount.value = order.totalAmount
  currentOrderId.value = order.id
  paymentStep.value = 'select'
  showPayment.value = true
}

const processPayment = () => {
  paymentStep.value = 'processing'
  setTimeout(async () => {
    if (currentOrderId.value) {
      try { await orderApi.updateStatus(currentOrderId.value, 1) } catch (e) {}
    }
    paymentStep.value = 'success'
  }, 2000)
}

const finishPayment = () => {
  showPayment.value = false
  ElMessage.success('支付成功')
  fetchOrders()
}

onMounted(fetchOrders)
</script>

<style scoped>
.order-page {
  min-height: 100vh;
  background: var(--color-off-white);
  padding: var(--space-xl) 0 var(--space-4xl);
}

.order-container {
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
}

.page-header p {
  color: var(--color-medium-gray);
  margin-top: var(--space-xs);
}

.order-card {
  background: var(--color-white);
  border-radius: var(--radius-xl);
  overflow: hidden;
  margin-bottom: var(--space-lg);
  box-shadow: var(--shadow-sm);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-lg) var(--space-xl);
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.order-no { font-weight: 500; }
.order-time { font-size: 0.8125rem; color: var(--color-medium-gray); }

.order-status {
  padding: 6px 16px;
  border-radius: var(--radius-full);
  font-size: 0.8125rem;
  font-weight: 500;
}

.order-status.pending { background: rgba(255, 149, 0, 0.1); color: var(--color-warning); }
.order-status.paid { background: rgba(0, 122, 255, 0.1); color: var(--color-info); }
.order-status.shipped { background: rgba(134, 134, 139, 0.1); color: var(--color-medium-gray); }
.order-status.completed { background: rgba(52, 199, 89, 0.1); color: var(--color-success); }
.order-status.cancelled { background: rgba(255, 59, 48, 0.1); color: var(--color-error); }

.order-items { padding: var(--space-lg) var(--space-xl); }

.order-item {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-md) 0;
}

.order-item:not(:last-child) { border-bottom: 1px solid var(--color-light-gray); }

.item-image {
  width: 72px;
  height: 72px;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--color-light-gray);
  flex-shrink: 0;
}

.item-image img { width: 100%; height: 100%; object-fit: cover; }

.item-info { flex: 1; }
.item-info h4 { font-size: 0.9375rem; font-weight: 500; margin-bottom: var(--space-xs); }
.item-info p { font-size: 0.8125rem; color: var(--color-medium-gray); }

.item-subtotal { font-weight: 600; color: var(--color-primary); }

/* Order Details */
.order-details {
  padding: var(--space-lg) var(--space-xl);
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-md);
}

.detail-row {
  display: flex;
  gap: var(--space-md);
}

.detail-label {
  font-size: 0.8125rem;
  color: var(--color-medium-gray);
  min-width: 60px;
}

.detail-value {
  font-size: 0.875rem;
  color: var(--color-charcoal);
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-lg) var(--space-xl);
  border-top: 1px solid var(--color-light-gray);
}

.total-amount {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--color-primary);
  margin-left: var(--space-sm);
}

.pay-btn {
  padding: 10px 28px;
  background: var(--color-primary);
  color: var(--color-white);
  border: none;
  border-radius: var(--radius-full);
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.pay-btn:hover { background: var(--color-secondary); }

/* Pagination */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: var(--space-xl);
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: var(--space-4xl) var(--space-xl);
  background: var(--color-white);
  border-radius: var(--radius-xl);
}

.empty-icon { width: 80px; height: 80px; margin: 0 auto var(--space-lg); color: var(--color-medium-gray); }
.empty-icon svg { width: 100%; height: 100%; }
.empty-state h2 { font-size: 1.5rem; margin-bottom: var(--space-sm); }
.empty-state p { color: var(--color-medium-gray); margin-bottom: var(--space-xl); }

.shop-btn {
  display: inline-block;
  padding: 14px 40px;
  background: var(--color-primary);
  color: var(--color-white);
  border-radius: var(--radius-full);
  font-weight: 500;
}

/* Payment Dialog */
.payment-content { padding: var(--space-md) 0; }

.payment-amount {
  text-align: center;
  padding: var(--space-xl);
  background: var(--color-light-gray);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-xl);
}

.amount-label { display: block; font-size: 0.875rem; color: var(--color-medium-gray); margin-bottom: var(--space-xs); }
.amount-value { font-size: 2.5rem; font-weight: 700; color: var(--color-primary); }

.payment-methods { display: flex; flex-direction: column; gap: var(--space-md); }

.payment-method {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-lg);
  border: 2px solid var(--color-light-gray);
  border-radius: var(--radius-lg);
  cursor: pointer;
}

.payment-method.active { border-color: var(--color-primary); }

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
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.method-name { display: block; font-weight: 600; line-height: 1.4; }
.method-desc { font-size: 0.8125rem; color: var(--color-medium-gray); line-height: 1.4; }
.method-check { 
  width: 24px; 
  height: 24px; 
  color: var(--color-primary);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.method-check svg { width: 100%; height: 100%; }

.payment-processing { text-align: center; padding: var(--space-2xl); }

.processing-spinner {
  width: 48px;
  height: 48px;
  border: 3px solid var(--color-light-gray);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  margin: 0 auto var(--space-lg);
  animation: spin 1s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

.payment-success { text-align: center; padding: var(--space-xl); }
.success-icon { width: 72px; height: 72px; margin: 0 auto var(--space-lg); }
.success-icon svg { width: 100%; height: 100%; }
.payment-success h3 { font-size: 1.25rem; font-weight: 600; }
.payment-success p { color: var(--color-medium-gray); }

.dialog-btn {
  padding: 12px 28px;
  border-radius: var(--radius-full);
  font-weight: 500;
  cursor: pointer;
}

.dialog-btn.cancel { background: var(--color-light-gray); border: none; color: var(--color-dark-gray); margin-right: var(--space-sm); }
.dialog-btn.confirm { background: var(--color-primary); border: none; color: var(--color-white); }

@media (max-width: 768px) {
  .order-details { grid-template-columns: 1fr; }
}
</style>
