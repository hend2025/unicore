<template>
  <el-container class="layout-container">
    
    <!-- 顶部导航栏 -->
    <HeaderNav
      :system-title="systemTitle"
      :systems="systems"
      :active-system="activeSystem"
      :is-collapse="isCollapse"
      :user-info="userStore.userInfo"
      @toggle-collapse="isCollapse = !isCollapse"
      @system-select="handleSystemSelect"
      @show-search="showMenuSearch = true"
      @change-password="showChangePwd = true"
      @logout="handleLogout"
    />
    
    <!-- 主内容区域 -->
    <el-container class="main-container">
      
      <!-- 侧边栏菜单 -->
      <SideMenu
        :menus="menus"
        :active-menu="activeMenu"
        :is-collapse="isCollapse"
        :visible="isAsideVisible"
        :menu-key="menuKey"
        @menu-select="handleMenuSelect"
      />

      <!-- 内容区域容器 -->
      <el-container class="content-container">
        <div class="app-main">
          <TabsView
            :active-tab="activeTab"
            :visited-views="visitedViews"
            :iframe-cache="iframeCache"
            :component-map="componentMap"
            @update:active-tab="activeTab = $event"
            @tab-change="handleTabChange"
            @close-tab="closeTab"
            @tabs-command="handleTabsCommand"
          />
        </div>
      </el-container>
    </el-container>
    
    <!-- 菜单搜索对话框 -->
    <MenuSearch
      v-model:show="showMenuSearch"
      :menus="rawMenus"
      @select="handleMenuSelect"
    />
    
    <!-- 修改密码对话框 -->
    <ChangePassword
      v-model:show="showChangePwd"
      @success="handlePasswordChanged"
    />
  </el-container>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getSystems } from '@/api/auth'
import { configApi } from '@/api/system'

import HeaderNav from './components/HeaderNav.vue'
import SideMenu from './components/SideMenu.vue'
import TabsView from './components/TabsView.vue'
import MenuSearch from './components/MenuSearch.vue'
import ChangePassword from './components/ChangePassword.vue'

import { useTabsView, componentMap } from './hooks/useTabsView'

const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)
const isAsideVisible = ref(true)
const menus = ref([])
const rawMenus = ref([])
const systems = ref([])
const menuKey = ref(0)
const showMenuSearch = ref(false)
const showChangePwd = ref(false)
const systemTitle = ref('医保基金综合监管平台')

// 从 sessionStorage 恢复上次选中的系统
const savedSystem = sessionStorage.getItem('activeSystem')
const activeSystem = ref(savedSystem || '')

/**
 * ==================== 标签页管理 ====================
 */
const {
  activeTab,
  activeMenu,
  visitedViews,
  iframeCache,
  handleMenuSelect: doMenuSelect,
  handleTabChange,
  closeTab,
  handleTabsCommand
} = useTabsView({
  findMenuItem: (menuUrl) => findMenuItem(menuUrl),
  getCurrentSystem: () => getCurrentSystem()
})

/**
 * ==================== 菜单项查找 ====================
 */
const findMenuItem = (menuUrl, menuList = rawMenus.value, parent = null) => {
  for (const menu of menuList) {
    if (menu.menuUrl === menuUrl) {
      return parent ? { ...menu, parentMenu: parent } : menu
    }
    if (menu.children && menu.children.length) {
      const found = findMenuItem(menuUrl, menu.children, menu)
      if (found) return found
    }
  }
  return null
}

/**
 * 获取当前选中的系统信息
 */
const getCurrentSystem = () => {
  return systems.value.find(s => String(s.sysId) === activeSystem.value)
}

/**
 * 处理菜单选择
 */
const handleMenuSelect = (index) => {
  doMenuSelect(index)
}

/**
 * ==================== 加载菜单数据 ====================
 */
const loadMenus = async (sysId) => {
  try {
    const res = await userStore.getMenuList(sysId)
    if (res && res.code === 0) {
      rawMenus.value = res.data
      
      // 跳过一级节点，直接展示二级节点
      const secondLevelMenus = []
      res.data.forEach(rootMenu => {
        if (rootMenu.children && rootMenu.children.length) {
          secondLevelMenus.push(...rootMenu.children)
        }
      })
      menus.value = secondLevelMenus
      menuKey.value++
    }
  } catch (error) {
    // 错误已在 request 拦截器中处理
  }
}

/**
 * 加载系统标题
 */
const loadSystemTitle = async () => {
  try {
    const res = await configApi.getByKey('system_title')
    if (res.data?.configValue) {
      systemTitle.value = res.data.configValue
    }
  } catch (e) {
    // 忽略错误，使用默认标题
  }
}

/**
 * 监听系统切换
 */
watch(activeSystem, async (newSystem) => {
  sessionStorage.setItem('activeSystem', newSystem)
  await loadMenus(newSystem)
})

/**
 * ==================== 系统切换处理 ====================
 */
const handleSystemSelect = (index) => {
  activeSystem.value = index
}

/**
 * ==================== 用户操作 ====================
 */
const handleLogout = async () => {
  try {
    await userStore.doLogout()
  } catch (e) {
    // 忽略登出接口错误
  }
  router.push('/login')
}

const handlePasswordChanged = async () => {
  sessionStorage.removeItem('activeSystem')
  try {
    await userStore.doLogout()
  } catch (e) {
    // 忽略登出接口错误
  }
  router.push('/login')
}

onMounted(async () => {
  try {
    // 加载系统标题
    loadSystemTitle()
    
    // 获取当前登录用户信息
    await userStore.getUserInfo()
    
    // 获取用户有权限访问的系统列表
    const sysRes = await getSystems()
    if (sysRes.code === 0 && sysRes.data.length > 0) {
      systems.value = sysRes.data
      
      if (!activeSystem.value) {
        activeSystem.value = String(sysRes.data[0].sysId)
      }
      
      const savedSystemExists = sysRes.data.some(s => String(s.sysId) === activeSystem.value)
      if (!savedSystemExists) {
        activeSystem.value = String(sysRes.data[0].sysId)
      }
    }
    
    // 加载当前系统的菜单
    await loadMenus(activeSystem.value)
    
    // 初始化激活状态
    activeMenu.value = '/home'
    activeTab.value = '/home'
  } catch (error) {
    // 错误已在 request 拦截器中处理
  }
})
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-container {
  flex: 1;
  overflow: hidden;
}

.content-container {
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
  overflow: hidden;
}

.app-main {
  height: 100%;
}
</style>
