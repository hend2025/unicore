import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { generateChildRoutes } from './routes'
import { ROUTES, STORAGE_KEYS } from '@/constants'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: ROUTES.HOME,
    children: generateChildRoutes()
  }
]

const router = createRouter({
  history: createWebHistory(__BASE_PATH__),
  routes
})

router.beforeEach((to, _from, next) => {
  NProgress.start()
  const token = localStorage.getItem('access_token')

  // 登录页直接放行
  if (to.path === '/login') {
    // 如果已登录，跳转到首页
    if (token) {
      next('/home')
    } else {
      next()
    }
  } else if (!token) {
    // 未登录，跳转到登录页
    next('/login')
  } else {
    next()
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
