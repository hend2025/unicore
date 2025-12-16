import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  base: '/unicore/',
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:7080/unicore',
        changeOrigin: true
      },
      '/login': {
        target: 'http://localhost:7080/unicore',
        changeOrigin: true
      },
      '/captcha': {
        target: 'http://localhost:7080/unicore',
        changeOrigin: true
      },
      '/logout': {
        target: 'http://localhost:7080/unicore',
        changeOrigin: true
      },
      // 外部系统代理 - 根据实际部署地址修改target
      '/intechk-platform-ui': {
        target: 'http://localhost:8080',  // TODO: 修改为实际的外部系统地址
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: '../src/main/resources/static',
    emptyOutDir: true
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: 'modern-compiler'
      }
    }
  }
})
