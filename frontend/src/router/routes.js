/**
 * 统一页面路由配置
 * 作为 componentMap、menuTitleMap 和 Vue Router 的单一数据源
 */
import { defineAsyncComponent } from 'vue'

/**
 * 页面路由配置列表
 * @property {string} path - 路由路径
 * @property {string} title - 页面标题（用于标签页和面包屑）
 * @property {Function} component - 异步加载的组件
 * @property {string} [name] - 路由名称（可选，默认根据 path 生成）
 */
export const pageRoutes = [
    // 首页
    {
        path: '/home',
        title: '首页',
        name: 'Home',
        component: () => import('@/views/home/index.vue')
    },
    // 系统管理
    {
        path: 'system/user',
        title: '用户管理',
        name: 'User',
        component: () => import('@/views/system/user/index.vue')
    },
    {
        path: 'system/role',
        title: '角色管理',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue')
    },
    {
        path: 'system/menu',
        title: '菜单管理',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue')
    },
    {
        path: 'system/org',
        title: '机构管理',
        name: 'Org',
        component: () => import('@/views/system/org/index.vue')
    },
    {
        path: 'system/system',
        title: '应用管理',
        name: 'System',
        component: () => import('@/views/system/system/index.vue')
    },
    {
        path: 'system/dict',
        title: '字典管理',
        name: 'Dict',
        component: () => import('@/views/system/dict/index.vue')
    },
    {
        path: 'system/config',
        title: '参数设置',
        name: 'Config',
        component: () => import('@/views/system/config/index.vue')
    },
    {
        path: 'system/notice',
        title: '通知公告',
        name: 'Notice',
        component: () => import('@/views/system/notice/index.vue')
    },
    {
        path: 'system/post',
        title: '岗位管理',
        name: 'Post',
        component: () => import('@/views/system/post/index.vue')
    },
    {
        path: 'system/para',
        title: '业务参数',
        name: 'Para',
        component: () => import('@/views/system/para/index.vue')
    },
    {
        path: 'system/area',
        title: '地区管理',
        name: 'Area',
        component: () => import('@/views/system/area/index.vue')
    },
    {
        path: 'system/admdvs',
        title: '医保区划',
        name: 'Admdvs',
        component: () => import('@/views/system/admdvs/index.vue')
    },
    // 日志管理
    {
        path: 'log/operlog',
        title: '操作日志',
        name: 'Operlog',
        component: () => import('@/views/log/operlog/index.vue')
    },
    {
        path: 'log/loginlog',
        title: '登录日志',
        name: 'Loginlog',
        component: () => import('@/views/log/loginlog/index.vue')
    },
    {
        path: 'log/apilog',
        title: 'API日志',
        name: 'Apilog',
        component: () => import('@/views/log/apilog/index.vue')
    }
]

/**
 * 生成组件映射表（用于标签页动态组件）
 * key: 路由路径, value: 异步组件
 */
export const componentMap = Object.fromEntries(
    pageRoutes.map(route => [
        route.path,
        defineAsyncComponent(route.component)
    ])
)

/**
 * 生成菜单标题映射表（用于标签页标题）
 * key: 路由路径, value: 页面标题
 */
export const menuTitleMap = Object.fromEntries(
    pageRoutes.map(route => [route.path, route.title])
)

/**
 * 生成 Vue Router 子路由配置
 */
export const generateChildRoutes = () => {
    return pageRoutes.map(route => ({
        path: route.path.startsWith('/') ? route.path.slice(1) : route.path,
        name: route.name,
        component: route.component,
        meta: { title: route.title }
    }))
}
