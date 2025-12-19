import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { STORAGE_KEYS, ROUTES } from '@/constants'

const service = axios.create({
  baseURL: import.meta.env.DEV ? '' : __API_BASE_URL__,
  timeout: 30000
})

// 跳转到登录页（防止重复跳转）
let isRedirecting = false
const redirectToLogin = () => {
  if (isRedirecting) return
  isRedirecting = true
  localStorage.removeItem(STORAGE_KEYS.TOKEN)
  sessionStorage.clear()
  router.push(ROUTES.LOGIN).finally(() => {
    isRedirecting = false
  })
}

service.interceptors.request.use(
  config => {
    const token = localStorage.getItem(STORAGE_KEYS.TOKEN)
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    const code = Number(res.code)
    if (code !== 0) {
      // 401 未登录或会话过期，直接跳转登录页，不提示
      if (code === 401) {
        redirectToLogin()
        return Promise.reject(new Error(res.message || '未登录'))
      }
      // -4 非白名单，无法获取用户信息，跳转登录页
      if (code === -4) {
        redirectToLogin()
        return Promise.reject(new Error(res.message || '无法获取用户信息'))
      }
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    // HTTP 401 状态码
    if (error.response && error.response.status === 401) {
      redirectToLogin()
      return Promise.reject(error)
    }
    // HTTP 403 禁止访问（可能是会话过期）
    if (error.response && error.response.status === 403) {
      redirectToLogin()
      return Promise.reject(error)
    }
    // 网络错误或超时
    if (!error.response) {
      ElMessage.error('网络连接失败，请检查网络')
    } else {
      ElMessage.error(error.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

export default service
