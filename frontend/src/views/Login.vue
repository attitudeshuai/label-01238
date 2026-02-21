<template>
  <div class="auth-page">
    <div class="auth-container">
      <!-- Left Side - Branding -->
      <div class="auth-branding">
        <div class="brand-content">
          <div class="brand-logo">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="1.5"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="1.5"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="1.5"/>
            </svg>
            <span>LUXE</span>
          </div>
          <h1 class="brand-title">欢迎回来</h1>
          <p class="brand-subtitle">登录您的账户，开启精致购物之旅</p>
        </div>
        <div class="brand-decoration">
          <div class="deco-ring ring-1"></div>
          <div class="deco-ring ring-2"></div>
          <div class="deco-ring ring-3"></div>
        </div>
      </div>

      <!-- Right Side - Form -->
      <div class="auth-form-wrapper">
        <div class="auth-form-container">
          <div class="form-header">
            <h2>账户登录</h2>
            <p>请输入您的登录信息</p>
          </div>

          <el-form ref="formRef" :model="form" :rules="rules" class="auth-form">
            <el-form-item prop="username">
              <div class="input-group">
                <label>用户名</label>
                <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
              </div>
            </el-form-item>

            <el-form-item prop="password">
              <div class="input-group">
                <label>密码</label>
                <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password />
              </div>
            </el-form-item>

            <el-form-item>
              <button type="button" class="submit-btn" :class="{ loading }" :disabled="loading" @click="handleLogin">
                <span v-if="!loading">登录</span>
                <span v-else class="loading-spinner"></span>
              </button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <span>还没有账户？</span>
            <router-link to="/register" class="link">立即注册</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../api'
import { useUserStore } from '../stores/user'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await authApi.login(form)
    userStore.login(res.data.token, res.data.user)
    ElMessage.success('登录成功')
    cartStore.fetchCart()
    const redirect = route.query.redirect || '/home'
    router.push(redirect)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page { min-height: 100vh; background: var(--color-light-gray); }
.auth-container { display: flex; min-height: 100vh; }

.auth-branding {
  flex: 1;
  background: linear-gradient(135deg, var(--color-primary) 0%, #2a2a2a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: var(--space-2xl);
}

.brand-content { position: relative; z-index: 2; text-align: center; color: var(--color-white); }
.brand-logo { display: flex; align-items: center; justify-content: center; gap: var(--space-sm); margin-bottom: var(--space-2xl); }
.brand-logo svg { width: 40px; height: 40px; color: var(--color-accent); }
.brand-logo span { font-size: 1.75rem; font-weight: 700; letter-spacing: 0.15em; }
.brand-title { font-size: 2.5rem; font-weight: 700; margin-bottom: var(--space-md); letter-spacing: -0.02em; }
.brand-subtitle { font-size: 1rem; color: rgba(255, 255, 255, 0.7); max-width: 300px; margin: 0 auto; }

.brand-decoration { position: absolute; inset: 0; pointer-events: none; }
.deco-ring { position: absolute; border-radius: 50%; border: 1px solid rgba(201, 169, 98, 0.2); }
.ring-1 { width: 400px; height: 400px; top: -100px; right: -100px; }
.ring-2 { width: 300px; height: 300px; bottom: -50px; left: -50px; }
.ring-3 { width: 200px; height: 200px; top: 50%; left: 50%; transform: translate(-50%, -50%); border-color: rgba(201, 169, 98, 0.3); }

.auth-form-wrapper { flex: 1; display: flex; align-items: center; justify-content: center; padding: var(--space-2xl); background: var(--color-white); }
.auth-form-container { width: 100%; max-width: 400px; }
.form-header { margin-bottom: var(--space-2xl); }
.form-header h2 { font-size: 1.75rem; font-weight: 700; margin-bottom: var(--space-xs); letter-spacing: -0.02em; }
.form-header p { color: var(--color-medium-gray); }

.auth-form { margin-bottom: var(--space-xl); }
.input-group { width: 100%; }
.input-group label { display: block; font-size: 0.875rem; font-weight: 500; margin-bottom: var(--space-sm); color: var(--color-charcoal); }
.auth-form :deep(.el-input__wrapper) { padding: 14px 16px; border-radius: var(--radius-md); }

.submit-btn {
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
  position: relative;
}
.submit-btn:hover:not(:disabled) { background: var(--color-secondary); transform: translateY(-1px); box-shadow: var(--shadow-md); }
.submit-btn:disabled { opacity: 0.7; cursor: not-allowed; }
.submit-btn.loading { color: transparent; }

.loading-spinner {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: var(--color-white);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: translate(-50%, -50%) rotate(360deg); } }

.form-footer { text-align: center; color: var(--color-medium-gray); }
.form-footer .link { color: var(--color-accent); font-weight: 500; margin-left: var(--space-xs); transition: color var(--transition-fast); }
.form-footer .link:hover { color: var(--color-accent-dark); }

@media (max-width: 968px) {
  .auth-branding { display: none; }
  .auth-form-wrapper { padding: var(--space-xl); }
}
</style>
