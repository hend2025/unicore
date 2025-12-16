import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

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
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'system/user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'system/role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'system/menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理' }
      },
      {
        path: 'system/org',
        name: 'Org',
        component: () => import('@/views/system/org/index.vue'),
        meta: { title: '机构管理' }
      },
      {
        path: 'system/system',
        name: 'System',
        component: () => import('@/views/system/system/index.vue'),
        meta: { title: '应用管理' }
      },
      {
        path: 'system/dict',
        name: 'Dict',
        component: () => import('@/views/system/dict/index.vue'),
        meta: { title: '字典管理' }
      },
      {
        path: 'system/config',
        name: 'Config',
        component: () => import('@/views/system/config/index.vue'),
        meta: { title: '参数设置' }
      },
      {
        path: 'system/notice',
        name: 'Notice',
        component: () => import('@/views/system/notice/index.vue'),
        meta: { title: '通知公告' }
      },
      {
        path: 'log/operlog',
        name: 'Operlog',
        component: () => import('@/views/log/operlog/index.vue'),
        meta: { title: '操作日志' }
      },
      {
        path: 'log/loginlog',
        name: 'Loginlog',
        component: () => import('@/views/log/loginlog/index.vue'),
        meta: { title: '登录日志' }
      },
      {
        path: 'log/apilog',
        name: 'Apilog',
        component: () => import('@/views/log/apilog/index.vue'),
        meta: { title: 'API日志' }
      },
      {
        path: 'system/post',
        name: 'Post',
        component: () => import('@/views/system/post/index.vue'),
        meta: { title: '岗位管理' }
      },
      {
        path: 'system/para',
        name: 'Para',
        component: () => import('@/views/system/para/index.vue'),
        meta: { title: '业务参数' }
      },
      {
        path: 'system/area',
        name: 'Area',
        component: () => import('@/views/system/area/index.vue'),
        meta: { title: '地区管理' }
      },
      {
        path: 'system/admdvs',
        name: 'Admdvs',
        component: () => import('@/views/system/admdvs/index.vue'),
        meta: { title: '医保区划' }
      },

    ]
  }
]

const router = createRouter({
  history: createWebHistory('/unicore/'),
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
