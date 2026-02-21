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
          <h1 class="brand-title">加入我们</h1>
          <p class="brand-subtitle">创建账户，探索无限精彩</p>
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
            <h2>创建账户</h2>
            <p>填写以下信息完成注册</p>
          </div>

          <el-form ref="formRef" :model="form" :rules="rules" class="auth-form">
            <el-form-item prop="username">
              <div class="input-group">
                <label>用户名</label>
                <el-input v-model="form.username" placeholder="3-20个字符" size="large" />
              </div>
            </el-form-item>

            <el-form-item prop="password">
              <div class="input-group">
                <label>密码</label>
                <el-input v-model="form.password" type="password" placeholder="6-20个字符" size="large" show-password />
              </div>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <div class="input-group">
                <label>确认密码</label>
                <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" size="large" show-password />
              </div>
            </el-form-item>

            <el-form-item prop="nickname">
              <div class="input-group">
                <label>昵称 <span class="optional">(选填)</span></label>
                <el-input v-model="form.nickname" placeholder="您的昵称" size="large" />
              </div>
            </el-form-item>

            <el-form-item>
              <button type="button" class="submit-btn" :class="{ loading }" :disabled="loading" @click="handleRegister">
                <span v-if="!loading">注册</span>
                <span v-else class="loading-spinner"></span>
              </button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <span>已有账户？</span>
            <router-link to="/login" class="link">立即登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '', confirmPassword: '', nickname: '' })

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await authApi.register({
      username: form.username,
      password: form.password,
      nickname: form.nickname || form.username
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
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
  background: linear-gradient(135deg, #1a1a2e 0%, var(--color-primary) 100%);
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
.input-group .optional { color: var(--color-medium-gray); font-weight: 400; }
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
