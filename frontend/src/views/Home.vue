<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title animate-slide-up">
          <span class="title-line">探索</span>
          <span class="title-line title-accent">非凡品质</span>
        </h1>
        <p class="hero-subtitle animate-slide-up">精选全球顶级商品，为您呈现极致购物体验</p>
      </div>
      <div class="hero-decoration">
        <div class="deco-circle deco-1"></div>
        <div class="deco-circle deco-2"></div>
        <div class="deco-circle deco-3"></div>
      </div>
    </section>

    <!-- Categories Section -->
    <section class="categories-section">
      <div class="section-container">
        <div class="categories-bar">
          <div class="categories-scroll">
            <button 
              class="category-chip" 
              :class="{ active: !selectedCategory }"
              @click="selectCategory(null)"
            >
              全部精选
            </button>
            <button 
              v-for="cat in categories" 
              :key="cat.id"
              class="category-chip"
              :class="{ active: selectedCategory === cat.id }"
              @click="selectCategory(cat.id)"
            >
              {{ cat.name }}
            </button>
          </div>
          <div class="search-box">
            <svg class="search-icon" viewBox="0 0 24 24" fill="none">
              <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2"/>
              <path d="M21 21L16.65 16.65" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <input 
              v-model="searchKeyword" 
              type="text" 
              placeholder="搜索商品..."
              @keyup.enter="handleSearch"
            />
            <button class="search-btn" @click="handleSearch">搜索</button>
          </div>
        </div>
      </div>
    </section>

    <!-- Products Section -->
    <section class="products-section" id="products-section">
      <div class="section-container">

        <div class="products-grid" v-if="paginatedProducts.length">
          <article 
            v-for="(product, index) in paginatedProducts" 
            :key="product.id" 
            class="product-card stagger-item"
            :style="{ animationDelay: `${index * 0.1}s` }"
            @click="goToDetail(product.id)"
          >
            <div class="product-image-wrapper">
              <img :src="product.image" :alt="product.name" class="product-image" />
              <div class="product-overlay">
                <button class="quick-add-btn" @click.stop="addToCart(product)" :disabled="product.stock <= 0">
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M6 2L3 6V20C3 20.5304 3.21071 21.0391 3.58579 21.4142C3.96086 21.7893 4.46957 22 5 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6L18 2H6Z" stroke="currentColor" stroke-width="1.5"/>
                    <path d="M3 6H21" stroke="currentColor" stroke-width="1.5"/>
                    <path d="M16 10C16 11.0609 15.5786 12.0783 14.8284 12.8284C14.0783 13.5786 13.0609 14 12 14C10.9391 14 9.92172 13.5786 9.17157 12.8284C8.42143 12.0783 8 11.0609 8 10" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                  {{ product.stock <= 0 ? '暂无库存' : '加入购物袋' }}
                </button>
              </div>
              <span v-if="product.stock <= 0" class="product-badge sold-out">售罄</span>
              <span v-else-if="product.sales > 100" class="product-badge">热销</span>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-desc">{{ product.description }}</p>
              <div class="product-footer">
                <span class="product-price">
                  <span class="currency">¥</span>{{ product.price }}
                </span>
                <span class="product-sales">{{ product.sales }}+ 人购买</span>
              </div>
            </div>
          </article>
        </div>

        <!-- Pagination -->
        <div class="pagination-wrapper" v-if="totalProducts > pageSize">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="totalProducts"
            layout="prev, pager, next"
            background
          />
        </div>

        <!-- Empty State -->
        <div v-else-if="!paginatedProducts.length" class="empty-state">
          <div class="empty-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M21 21L16.65 16.65M19 11C19 15.4183 15.4183 19 11 19C6.58172 19 3 15.4183 3 11C3 6.58172 6.58172 3 11 3C15.4183 3 19 6.58172 19 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <h3>暂无相关商品</h3>
          <p>换个关键词试试吧</p>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
      <div class="section-container">
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M12 22C12 22 20 18 20 12V5L12 2L4 5V12C4 18 12 22 12 22Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M9 12L11 14L15 10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3>正品保障</h3>
            <p>100%正品，假一赔十</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M5 12H19" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                <path d="M12 5L19 12L12 19" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3>极速配送</h3>
            <p>全国包邮，闪电到达</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M4 4H20C21.1 4 22 4.9 22 6V18C22 19.1 21.1 20 20 20H4C2.9 20 2 19.1 2 18V6C2 4.9 2.9 4 4 4Z" stroke="currentColor" stroke-width="1.5"/>
                <path d="M22 6L12 13L2 6" stroke="currentColor" stroke-width="1.5"/>
              </svg>
            </div>
            <h3>贴心服务</h3>
            <p>7x24小时在线客服</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M23 6L13.5 15.5L8.5 10.5L1 18" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M17 6H23V12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3>无忧退换</h3>
            <p>30天无理由退换货</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { productApi, categoryApi } from '../api'
import { useCartStore } from '../stores/cart'
import { useUserStore } from '../stores/user'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const products = ref([])
const categories = ref([])
const searchKeyword = ref('')
const selectedCategory = ref(null)
const currentPage = ref(1)
const pageSize = 8

const filteredProducts = computed(() => {
  let result = products.value
  if (selectedCategory.value) {
    result = result.filter(p => p.categoryId === selectedCategory.value)
  }
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(p => 
      p.name.toLowerCase().includes(keyword) || 
      p.description?.toLowerCase().includes(keyword)
    )
  }
  return result
})

const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredProducts.value.slice(start, start + pageSize)
})

const totalProducts = computed(() => filteredProducts.value.length)

const fetchData = async () => {
  const [productRes, categoryRes] = await Promise.all([
    productApi.list(),
    categoryApi.list()
  ])
  products.value = productRes.data || []
  categories.value = categoryRes.data || []
}

const handleSearch = async () => {
  if (searchKeyword.value) {
    const res = await productApi.search(searchKeyword.value)
    products.value = res.data || []
    selectedCategory.value = null
  } else {
    fetchData()
  }
}

const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  currentPage.value = 1
  // 滚动到精选商品区域
  const productsSection = document.getElementById('products-section')
  if (productsSection) {
    productsSection.scrollIntoView({ behavior: 'smooth' })
  }
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const addToCart = async (product) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  if (product.stock <= 0) {
    ElMessage.warning('商品库存不足')
    return
  }
  await cartStore.addToCart(product.id, 1)
  ElMessage.success('已加入购物袋')
}

onMounted(fetchData)
</script>

<style scoped>
.home-page {
  min-height: 100vh;
}

/* Hero Section */
.hero {
  position: relative;
  min-height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, var(--color-white) 0%, var(--color-light-gray) 100%);
  overflow: hidden;
  padding: var(--space-4xl) var(--space-xl);
}

.hero-content {
  position: relative;
  z-index: 2;
  text-align: center;
  max-width: 800px;
}

.hero-title {
  font-size: clamp(2.5rem, 8vw, 5rem);
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: -0.03em;
  margin-bottom: var(--space-lg);
}

.title-line {
  display: block;
}

.title-accent {
  background: linear-gradient(135deg, var(--color-accent) 0%, var(--color-accent-dark) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1.25rem;
  color: var(--color-medium-gray);
  margin-bottom: var(--space-xl);
  animation-delay: 0.2s;
}

/* Hero Decoration */
.hero-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-accent) 0%, transparent 70%);
  opacity: 0.1;
}

.deco-1 {
  width: 600px;
  height: 600px;
  top: -200px;
  right: -200px;
}

.deco-2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  left: -100px;
}

.deco-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 10%;
}

/* Categories Section */
.categories-section {
  background: var(--color-white);
  padding: var(--space-lg) 0;
  border-bottom: 1px solid var(--color-light-gray);
  position: sticky;
  top: 80px;
  z-index: 10;
}

.section-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--space-xl);
}

.categories-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-lg);
}

.categories-scroll {
  display: flex;
  gap: var(--space-sm);
  overflow-x: auto;
  padding: var(--space-xs) 0;
  scrollbar-width: none;
  -ms-overflow-style: none;
  flex: 1;
  min-width: 0;
}

.categories-scroll::-webkit-scrollbar {
  display: none;
}

.category-chip {
  padding: 10px 24px;
  background: var(--color-light-gray);
  border: none;
  border-radius: var(--radius-full);
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-dark-gray);
  cursor: pointer;
  white-space: nowrap;
  transition: all var(--transition-fast);
}

.category-chip:hover {
  background: var(--color-primary);
  color: var(--color-white);
}

.category-chip.active {
  background: var(--color-primary);
  color: var(--color-white);
}

/* Search Box */
.search-box {
  display: flex;
  align-items: center;
  background: var(--color-light-gray);
  border-radius: var(--radius-full);
  padding: 4px 4px 4px 16px;
  flex-shrink: 0;
}

.search-box .search-icon {
  width: 18px;
  height: 18px;
  color: var(--color-medium-gray);
  flex-shrink: 0;
}

.search-box input {
  border: none;
  outline: none;
  font-size: 0.875rem;
  padding: 8px 12px;
  background: transparent;
  color: var(--color-charcoal);
  width: 160px;
}

.search-box input::placeholder {
  color: var(--color-medium-gray);
}

.search-box .search-btn {
  padding: 10px 20px;
  background: var(--color-primary);
  color: var(--color-white);
  border: none;
  border-radius: var(--radius-full);
  font-size: 0.8125rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.search-box .search-btn:hover {
  background: var(--color-secondary);
}

/* Products Section */
.products-section {
  padding: var(--space-3xl) 0;
  background: var(--color-off-white);
}


.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--space-xl);
}

/* Product Card */
.product-card {
  background: var(--color-white);
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-xl);
}

.product-image-wrapper {
  position: relative;
  aspect-ratio: 1;
  overflow: hidden;
  background: var(--color-light-gray);
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.product-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.quick-add-btn {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: 14px 28px;
  background: var(--color-white);
  border: none;
  border-radius: var(--radius-full);
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-primary);
  cursor: pointer;
  transform: translateY(20px);
  transition: all var(--transition-normal);
}

.product-card:hover .quick-add-btn {
  transform: translateY(0);
}

.quick-add-btn:hover {
  background: var(--color-primary);
  color: var(--color-white);
}

.quick-add-btn svg {
  width: 18px;
  height: 18px;
}

.product-badge {
  position: absolute;
  top: var(--space-md);
  left: var(--space-md);
  padding: 6px 14px;
  background: var(--color-accent);
  color: var(--color-white);
  font-size: 0.75rem;
  font-weight: 600;
  border-radius: var(--radius-full);
  letter-spacing: 0.05em;
}

.product-badge.sold-out {
  background: var(--color-medium-gray);
}

.quick-add-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Pagination */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: var(--space-2xl);
}

.product-info {
  padding: var(--space-lg);
}

.product-name {
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: var(--space-xs);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 0.8125rem;
  color: var(--color-medium-gray);
  margin-bottom: var(--space-md);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.product-price {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--color-primary);
}

.product-price .currency {
  font-size: 0.875rem;
  font-weight: 500;
}

.product-sales {
  font-size: 0.75rem;
  color: var(--color-medium-gray);
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: var(--space-4xl) var(--space-xl);
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

.empty-state h3 {
  font-size: 1.25rem;
  margin-bottom: var(--space-sm);
}

.empty-state p {
  color: var(--color-medium-gray);
}

/* Features Section */
.features-section {
  padding: var(--space-3xl) 0;
  background: var(--color-white);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-xl);
}

.feature-card {
  text-align: center;
  padding: var(--space-xl);
}

.feature-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto var(--space-md);
  color: var(--color-accent);
}

.feature-icon svg {
  width: 100%;
  height: 100%;
}

.feature-card h3 {
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: var(--space-xs);
}

.feature-card p {
  font-size: 0.875rem;
  color: var(--color-medium-gray);
}

/* Responsive */
@media (max-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero {
    min-height: 50vh;
    padding: var(--space-2xl) var(--space-md);
  }

  .categories-section {
    top: 64px;
    padding: var(--space-md) 0;
  }

  .section-container {
    padding: 0 var(--space-md);
  }

  .categories-bar {
    flex-direction: column;
    gap: var(--space-sm);
    align-items: stretch;
  }

  .categories-scroll {
    width: 100%;
    padding-bottom: var(--space-xs);
  }

  .search-box {
    width: 100%;
  }

  .search-box input {
    flex: 1;
    width: auto;
  }

  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--space-md);
  }

  .features-grid {
    grid-template-columns: 1fr;
  }
}
</style>
