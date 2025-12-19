import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// 统一配置应用路径，修改这里即可
const APP_BASE_PATH = '/uniportal'

export default defineConfig({
  base: './',
  plugins: [vue()],
  define: {
    __BASE_PATH__: JSON.stringify(APP_BASE_PATH),
    __API_BASE_URL__: JSON.stringify(APP_BASE_PATH.replace(/\/$/, ''))
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:7081/'+APP_BASE_PATH,
        changeOrigin: true
      },
      '/login': {
        target:  'http://localhost:7081/'+APP_BASE_PATH,
        changeOrigin: true
      },
      '/logout': {
        target:  'http://localhost:7081/'+APP_BASE_PATH,
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
        api: 'modern-compiler',
        additionalData: `@use "@/styles/variables.scss" as *;`
      }
    }
  }
})
