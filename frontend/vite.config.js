import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 商业购物管理系统 Vite 配置
export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8238',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets'
  },
  test: {
    globals: true,
    environment: 'jsdom'
  }
})
