/**
 * 多标签页状态管理 Hook
 * 管理已访问页面、标签页切换、关闭等逻辑
 */
import { ref } from 'vue'
import { componentMap, menuTitleMap } from '@/router/routes'
import { ROUTES } from '@/constants'

// 导出以便其他组件使用
export { componentMap, menuTitleMap }

/**
 * 多标签页管理 Hook
 * @param {Object} options - 配置项
 * @param {Function} options.findMenuItem - 查找菜单项的函数
 * @param {Function} options.getCurrentSystem - 获取当前系统的函数
 */
export function useTabsView(options = {}) {
    const { findMenuItem, getCurrentSystem } = options

    // 当前激活的标签页路径
    const activeTab = ref(ROUTES.HOME)

    // 当前激活的菜单项路径
    const activeMenu = ref('')

    // 已访问的页面列表（标签页数据源）
    const visitedViews = ref([{ path: ROUTES.HOME, title: '首页' }])

    // iframe 缓存
    const iframeCache = ref({})

    /**
     * 处理菜单选择
     * @param {string} index - 菜单的 menuUrl
     */
    const handleMenuSelect = (index) => {
        const menuItem = findMenuItem?.(index)
        const currentSystem = getCurrentSystem?.()

        // 检查是否为内部页面组件
        const comp = componentMap[index]

        if (comp) {
            // 内部页面处理
            const title = menuTitleMap[index] || (menuItem?.menuName) || '页面'

            const existingView = visitedViews.value.find(v => v.path === index)
            if (!existingView) {
                visitedViews.value.push({
                    path: index,
                    title: title
                })
            }

            activeTab.value = index
            activeMenu.value = index
        } else {
            // 外部系统链接处理
            const sysUrl = currentSystem?.sysUrl || ''
            let externalUrl = sysUrl + (menuItem?.menuUrl || index)

            if (!externalUrl.startsWith('http://') && !externalUrl.startsWith('https://')) {
                if (!externalUrl.startsWith('/')) {
                    externalUrl = '/' + externalUrl
                }
                externalUrl = window.location.origin + externalUrl
            }

            const menuId = menuItem?.menuId || Date.now()
            const title = menuItem?.menuName || '外部页面'
            const iframePath = `iframe_${menuId}`

            if (!iframeCache.value[iframePath]) {
                iframeCache.value[iframePath] = { url: externalUrl, title: title }
            }

            const existingView = visitedViews.value.find(v => v.path === iframePath)
            if (!existingView) {
                visitedViews.value.push({
                    path: iframePath,
                    title: title,
                    isIframe: true
                })
            }

            activeTab.value = iframePath
            activeMenu.value = index
        }
    }

    /**
     * 处理标签页切换
     * @param {string} tabPath - 切换到的标签页路径
     */
    const handleTabChange = (tabPath) => {
        if (!tabPath.startsWith('iframe_')) {
            activeMenu.value = tabPath
        }
    }

    /**
     * 关闭单个标签页
     * @param {string} targetPath - 要关闭的标签页路径
     */
    const closeTab = (targetPath) => {
        const index = visitedViews.value.findIndex(v => v.path === targetPath)
        if (index > -1) {
            if (targetPath.startsWith('iframe_')) {
                delete iframeCache.value[targetPath]
            }

            visitedViews.value.splice(index, 1)

            if (targetPath === activeTab.value) {
                const nextTab = visitedViews.value[index] || visitedViews.value[index - 1]
                if (nextTab) {
                    activeTab.value = nextTab.path
                    if (!nextTab.path.startsWith('iframe_')) {
                        activeMenu.value = nextTab.path
                    }
                }
            }
        }
    }

    /**
     * 关闭其他标签页
     */
    const closeOtherTabs = () => {
        visitedViews.value = visitedViews.value.filter(view =>
            view.path === ROUTES.HOME || view.path === activeTab.value
        )
        Object.keys(iframeCache.value).forEach(key => {
            if (key !== activeTab.value) {
                delete iframeCache.value[key]
            }
        })
    }

    /**
     * 关闭全部标签页
     */
    const closeAllTabs = () => {
        visitedViews.value = visitedViews.value.filter(view => view.path === ROUTES.HOME)
        activeTab.value = ROUTES.HOME
        activeMenu.value = ROUTES.HOME
        iframeCache.value = {}
    }

    /**
     * 处理标签页下拉菜单命令
     * @param {string} command - 命令类型
     */
    const handleTabsCommand = (command) => {
        if (command === 'closeOther') {
            closeOtherTabs()
        } else if (command === 'closeAll') {
            closeAllTabs()
        }
    }

    return {
        // 状态
        activeTab,
        activeMenu,
        visitedViews,
        iframeCache,
        componentMap,
        // 方法
        handleMenuSelect,
        handleTabChange,
        closeTab,
        closeOtherTabs,
        closeAllTabs,
        handleTabsCommand
    }
}
