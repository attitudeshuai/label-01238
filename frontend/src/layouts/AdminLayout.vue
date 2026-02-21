<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <aside class="sidebar" :class="{ collapsed: isCollapse }">
      <div class="sidebar-header">
        <div class="logo">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="1.5"/>
            <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="1.5"/>
          </svg>
          <span v-show="!isCollapse">LUXE</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <router-link to="/admin/dashboard" class="nav-item" :class="{ active: route.path === '/admin/dashboard' }">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M3 9L12 2L21 9V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V9Z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M9 22V12H15V22" stroke="currentColor" stroke-width="1.5"/>
          </svg>
          <span v-show="!isCollapse">控制台</span>
        </router-link>

        <router-link to="/admin/product" class="nav-item" :class="{ active: route.path === '/admin/product' }">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M6 2L3 6V20C3 20.5304 3.21071 21.0391 3.58579 21.4142C3.96086 21.7893 4.46957 22 5 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6L18 2H6Z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M3 6H21" stroke="currentColor" stroke-width="1.5"/>
            <path d="M16 10C16 11.0609 15.5786 12.0783 14.8284 12.8284C14.0783 13.5786 13.0609 14 12 14C10.9391 14 9.92172 13.5786 9.17157 12.8284C8.42143 12.0783 8 11.0609 8 10" stroke="currentColor" stroke-width="1.5"/>
          </svg>
          <span v-show="!isCollapse">商品管理</span>
        </router-link>

        <router-link to="/admin/category" class="nav-item" :class="{ active: route.path === '/admin/category' }">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M4 4H10V10H4V4Z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M14 4H20V10H14V4Z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M4 14H10V20H4V14Z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M14 14H20V20H14V14Z" stroke="currentColor" stroke-width="1.5"/>
          </svg>
          <span v-show="!isCollapse">分类管理</span>
        </router-link>

        <router-link to="/admin/order" class="nav-item" :class="{ active: route.path === '/admin/order' }">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M9 5H7C5.89543 5 5 5.89543 5 7V19C5 20.1046 5.89543 21 7 21H17C18.1046 21 19 20.1046 19 19V7C19 5.89543 18.1046 5 17 5H15" stroke="currentColor" stroke-width="1.5"/>
            <path d="M9 5C9 3.89543 9.89543 3 11 3H13C14.1046 3 15 3.89543 15 5C15 6.10457 14.1046 7 13 7H11C9.89543 7 9 6.10457 9 5Z" stroke="currentColor" stroke-width="1.5"/>
          </svg>
          <span v-show="!isCollapse">订单管理</span>
        </router-link>

        <router-link to="/admin/user" class="nav-item" :class="{ active: route.path === '/admin/user' }">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21" stroke="currentColor" stroke-width="1.5"/>
            <path d="M9 11C11.2091 11 13 9.20914 13 7C13 4.79086 11.2091 3 9 3C6.79086 3 5 4.79086 5 7C5 9.20914 6.79086 11 9 11Z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M23 21V19C22.9993 18.1137 22.7044 17.2528 22.1614 16.5523C21.6184 15.8519 20.8581 15.3516 20 15.13" stroke="currentColor" stroke-width="1.5"/>
            <path d="M16 3.13C16.8604 3.35031 17.623 3.85071 18.1676 4.55232C18.7122 5.25392 19.0078 6.11683 19.0078 7.005C19.0078 7.89318 18.7122 8.75608 18.1676 9.45769C17.623 10.1593 16.8604 10.6597 16 10.88" stroke="currentColor" stroke-width="1.5"/>
          </svg>
          <span v-show="!isCollapse">用户管理</span>
        </router-link>

        <router-link to="/admin/statistics" class="nav-item" :class="{ active: route.path === '/admin/statistics' }">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M18 20V10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            <path d="M12 20V4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            <path d="M6 20V14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
          <span v-show="!isCollapse">数据分析</span>
        </router-link>
      </nav>

    </aside>

    <!-- Main -->
    <div class="main-wrapper">
      <!-- Header -->
      <header class="admin-header">
        <div class="header-left">
          <nav class="breadcrumb">
            <span class="breadcrumb-item">管理后台</span>
            <span class="breadcrumb-separator">/</span>
            <span class="breadcrumb-item active">{{ route.meta.title || '控制台' }}</span>
          </nav>
        </div>
        <div class="header-right">
          <router-link to="/home" class="back-btn">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M3 9L12 2L21 9V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V9Z" stroke="currentColor" stroke-width="1.5"/>
              <path d="M9 22V12H15V22" stroke="currentColor" stroke-width="1.5"/>
            </svg>
            <span>返回前台</span>
          </router-link>
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-avatar">
              <span>{{ userStore.username.charAt(0).toUpperCase() }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- Content -->
      <main class="admin-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--color-off-white);
}

/* Sidebar */
.sidebar {
  width: 260px;
  background: var(--color-primary);
  display: flex;
  flex-direction: column;
  transition: width var(--transition-normal);
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar.collapsed {
  width: 80px;
}

.sidebar-header {
  padding: var(--space-xl);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  color: var(--color-white);
}

.logo svg {
  width: 32px;
  height: 32px;
  color: var(--color-accent);
  flex-shrink: 0;
}

.logo span {
  font-size: 1.25rem;
  font-weight: 700;
  letter-spacing: 0.1em;
}

.badge {
  display: inline-block;
  margin-top: var(--space-sm);
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-full);
  font-size: 0.75rem;
  color: var(--color-accent);
}

/* Navigation */
.sidebar-nav {
  flex: 1;
  padding: var(--space-lg);
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: 14px 16px;
  border-radius: var(--radius-md);
  color: rgba(255, 255, 255, 0.7);
  transition: all var(--transition-fast);
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: var(--color-white);
}

.nav-item.active {
  background: var(--color-accent);
  color: var(--color-primary);
}

.nav-item svg {
  width: 22px;
  height: 22px;
  flex-shrink: 0;
}

.nav-item span {
  font-size: 0.9375rem;
  font-weight: 500;
}

/* Main Wrapper */
.main-wrapper {
  flex: 1;
  margin-left: 260px;
  display: flex;
  flex-direction: column;
  transition: margin-left var(--transition-normal);
  min-width: 0;
  overflow-x: hidden;
}

.sidebar.collapsed + .main-wrapper {
  margin-left: 80px;
}

/* Header */
.admin-header {
  height: 72px;
  background: var(--color-white);
  border-bottom: 1px solid var(--color-light-gray);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--space-xl);
  position: sticky;
  top: 0;
  z-index: 50;
}

.header-left {
  display: flex;
  align-items: center;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  font-size: 0.9375rem;
}

.breadcrumb-item {
  color: var(--color-medium-gray);
}

.breadcrumb-item.active {
  color: var(--color-charcoal);
  font-weight: 600;
}

.breadcrumb-separator {
  color: var(--color-medium-gray);
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--space-lg);
}

.back-btn {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: 10px 20px;
  background: var(--color-light-gray);
  border-radius: var(--radius-full);
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-dark-gray);
  transition: all var(--transition-fast);
}

.back-btn:hover {
  background: var(--color-primary);
  color: var(--color-white);
}

.back-btn svg {
  width: 18px;
  height: 18px;
}

.user-avatar {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, var(--color-accent) 0%, var(--color-accent-dark) 100%);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-white);
  font-weight: 600;
  cursor: pointer;
  transition: transform var(--transition-fast);
}

.user-avatar:hover {
  transform: scale(1.05);
}


/* Content */
.admin-content {
  flex: 1;
  padding: var(--space-xl);
  overflow-x: hidden;
}

/* 防止表格溢出 */
.admin-content :deep(.el-table) {
  width: 100% !important;
}

.admin-content :deep(.page-card),
.admin-content :deep(.manage-page) {
  overflow: hidden;
}

/* Transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式 - 小屏幕侧边栏收缩 */
@media (max-width: 1024px) {
  .sidebar {
    width: 80px;
  }
  
  .sidebar .logo span,
  .sidebar .nav-item span {
    display: none;
  }
  
  .sidebar .nav-item {
    justify-content: center;
    padding: 14px;
  }
  
  .main-wrapper {
    margin-left: 80px;
  }
}

</style>
