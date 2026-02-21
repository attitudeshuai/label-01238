<template>
  <div class="product-detail-page" v-loading="loading">
    <div class="detail-container" v-if="product">
      <!-- Breadcrumb -->
      <nav class="breadcrumb">
        <router-link to="/home">首页</router-link>
        <span class="separator">/</span>
        <span>{{ product.categoryName || '商品详情' }}</span>
      </nav>

      <!-- Product Content -->
      <div class="product-content">
        <!-- Image Gallery -->
        <div class="product-gallery">
          <div class="main-image-wrapper">
            <img :src="product.image" :alt="product.name" class="main-image" />
            <span v-if="product.sales > 100" class="product-badge">热销</span>
          </div>
        </div>

        <!-- Product Info -->
        <div class="product-info">
          <h1 class="product-title">{{ product.name }}</h1>
          <p class="product-description">{{ product.description }}</p>

          <!-- Price -->
          <div class="price-section">
            <div class="price-wrapper">
              <span class="price-label">价格</span>
              <span class="price-value">
                <span class="currency">¥</span>{{ product.price }}
              </span>
            </div>
          </div>

          <!-- Meta Info -->
          <div class="meta-section">
            <div class="meta-item">
              <span class="meta-label">分类</span>
              <span class="meta-value">{{ product.categoryName || '未分类' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">库存</span>
              <span class="meta-value">{{ product.stock }} 件</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">销量</span>
              <span class="meta-value">{{ product.sales }}+ 人购买</span>
            </div>
          </div>

          <!-- Quantity -->
          <div class="quantity-section" v-if="product.stock > 0">
            <span class="quantity-label">数量</span>
            <div class="quantity-control">
              <button class="qty-btn" @click="quantity > 1 && quantity--">−</button>
              <span class="qty-value">{{ quantity }}</span>
              <button class="qty-btn" @click="quantity < product.stock && quantity++">+</button>
            </div>
          </div>

          <!-- Actions -->
          <div class="action-section" v-if="product.stock > 0">
            <button class="btn-cart" @click="addToCart">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M6 2L3 6V20C3 20.5304 3.21071 21.0391 3.58579 21.4142C3.96086 21.7893 4.46957 22 5 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6L18 2H6Z" stroke="currentColor" stroke-width="1.5"/>
                <path d="M3 6H21" stroke="currentColor" stroke-width="1.5"/>
                <path d="M16 10C16 11.0609 15.5786 12.0783 14.8284 12.8284C14.0783 13.5786 13.0609 14 12 14C10.9391 14 9.92172 13.5786 9.17157 12.8284C8.42143 12.0783 8 11.0609 8 10" stroke="currentColor" stroke-width="1.5"/>
              </svg>
              加入购物袋
            </button>
            <button class="btn-buy" @click="buyNow">立即购买</button>
          </div>

          <!-- Sold Out -->
          <div class="sold-out-section" v-else>
            <button class="btn-sold-out" disabled>商品已售罄</button>
          </div>

          <!-- Features -->
          <div class="features-section">
            <div class="feature-item">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M12 22C12 22 20 18 20 12V5L12 2L4 5V12C4 18 12 22 12 22Z" stroke="currentColor" stroke-width="1.5"/>
                <path d="M9 12L11 14L15 10" stroke="currentColor" stroke-width="1.5"/>
              </svg>
              <span>正品保障</span>
            </div>
            <div class="feature-item">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M5 12H19M12 5L19 12L12 19" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>极速配送</span>
            </div>
            <div class="feature-item">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M3 9L12 2L21 9V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V9Z" stroke="currentColor" stroke-width="1.5"/>
                <path d="M9 22V12H15V22" stroke="currentColor" stroke-width="1.5"/>
              </svg>
              <span>无忧退换</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { productApi } from '../api'
import { useCartStore } from '../stores/cart'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const product = ref(null)
const loading = ref(false)
const quantity = ref(1)

const fetchProduct = async () => {
  loading.value = true
  try {
    const res = await productApi.detail(route.params.id)
    product.value = res.data
  } finally {
    loading.value = false
  }
}

const addToCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  await cartStore.addToCart(product.value.id, quantity.value)
  ElMessage.success('已加入购物袋')
}

const buyNow = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  await cartStore.addToCart(product.value.id, quantity.value)
  router.push('/cart')
}

onMounted(fetchProduct)
</script>

<style scoped>
.product-detail-page {
  min-height: 100vh;
  background: var(--color-off-white);
  padding: var(--space-xl) 0 var(--space-4xl);
}

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-xl);
}

/* Breadcrumb */
.breadcrumb {
  margin-bottom: var(--space-xl);
  font-size: 0.875rem;
  color: var(--color-medium-gray);
}

.breadcrumb a {
  color: var(--color-medium-gray);
  transition: color var(--transition-fast);
}

.breadcrumb a:hover {
  color: var(--color-primary);
}

.breadcrumb .separator {
  margin: 0 var(--space-sm);
}

/* Product Content */
.product-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-3xl);
  background: var(--color-white);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-sm);
}

/* Gallery */
.product-gallery {
  position: sticky;
  top: 100px;
  align-self: start;
}

.main-image-wrapper {
  position: relative;
  aspect-ratio: 1;
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: var(--color-light-gray);
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-badge {
  position: absolute;
  top: var(--space-md);
  left: var(--space-md);
  padding: 8px 20px;
  background: var(--color-accent);
  color: var(--color-white);
  font-size: 0.8125rem;
  font-weight: 600;
  border-radius: var(--radius-full);
  letter-spacing: 0.05em;
}

/* Product Info */
.product-info {
  padding: var(--space-md) 0;
}

.product-title {
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  margin-bottom: var(--space-md);
  line-height: 1.3;
}

.product-description {
  font-size: 1rem;
  color: var(--color-medium-gray);
  line-height: 1.7;
  margin-bottom: var(--space-xl);
}

/* Price Section */
.price-section {
  background: linear-gradient(135deg, var(--color-light-gray) 0%, var(--color-off-white) 100%);
  padding: var(--space-xl);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-xl);
}

.price-wrapper {
  display: flex;
  align-items: baseline;
  gap: var(--space-md);
}

.price-label {
  font-size: 0.875rem;
  color: var(--color-medium-gray);
}

.price-value {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--color-primary);
}

.price-value .currency {
  font-size: 1.25rem;
  font-weight: 500;
}

/* Meta Section */
.meta-section {
  display: flex;
  gap: var(--space-2xl);
  margin-bottom: var(--space-xl);
  padding-bottom: var(--space-xl);
  border-bottom: 1px solid var(--color-light-gray);
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.meta-label {
  font-size: 0.75rem;
  color: var(--color-medium-gray);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.meta-value {
  font-size: 0.9375rem;
  font-weight: 500;
}

/* Quantity Section */
.quantity-section {
  display: flex;
  align-items: center;
  gap: var(--space-xl);
  margin-bottom: var(--space-xl);
}

.quantity-label {
  font-size: 0.9375rem;
  font-weight: 500;
}

.quantity-control {
  display: flex;
  align-items: center;
  border: 1px solid var(--color-light-gray);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.qty-btn {
  width: 44px;
  height: 44px;
  background: var(--color-white);
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.qty-btn:hover {
  background: var(--color-light-gray);
}

.qty-value {
  width: 60px;
  text-align: center;
  font-size: 1rem;
  font-weight: 500;
}

/* Action Section */
.action-section {
  display: flex;
  gap: var(--space-md);
  margin-bottom: var(--space-xl);
}

.btn-cart, .btn-buy {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-sm);
  padding: 18px 32px;
  border-radius: var(--radius-full);
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-cart {
  background: var(--color-white);
  border: 2px solid var(--color-primary);
  color: var(--color-primary);
}

.btn-cart:hover {
  background: var(--color-primary);
  color: var(--color-white);
}

.btn-cart svg {
  width: 20px;
  height: 20px;
}

.btn-buy {
  background: var(--color-primary);
  border: 2px solid var(--color-primary);
  color: var(--color-white);
}

.btn-buy:hover {
  background: var(--color-secondary);
  border-color: var(--color-secondary);
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

/* Sold Out */
.sold-out-section {
  margin-bottom: var(--space-xl);
}

.btn-sold-out {
  width: 100%;
  padding: 18px 32px;
  background: var(--color-medium-gray);
  border: none;
  border-radius: var(--radius-full);
  font-size: 1rem;
  font-weight: 500;
  color: var(--color-white);
  cursor: not-allowed;
}

/* Features Section */
.features-section {
  display: flex;
  gap: var(--space-xl);
  padding-top: var(--space-xl);
  border-top: 1px solid var(--color-light-gray);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  font-size: 0.875rem;
  color: var(--color-medium-gray);
}

.feature-item svg {
  width: 18px;
  height: 18px;
  color: var(--color-accent);
}

/* Responsive */
@media (max-width: 968px) {
  .product-content {
    grid-template-columns: 1fr;
    gap: var(--space-xl);
  }

  .product-gallery {
    position: static;
  }

  .action-section {
    flex-direction: column;
  }

  .features-section {
    flex-wrap: wrap;
  }
}
</style>
