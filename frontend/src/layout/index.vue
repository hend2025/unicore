<template>
  <el-container class="layout-container">
    <el-header class="top-header">
      <div class="header-left">
        <div class="logo">
          <span>医保基金综合监管平台</span>
          <el-icon class="menu-collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </div>
        <el-menu mode="horizontal" :default-active="activeSystem" class="system-menu" background-color="#1976d2" text-color="#fff" active-text-color="#fff" @select="handleSystemSelect">
          <el-menu-item v-for="sys in systems" :key="sys.sysId" :index="String(sys.sysId)">{{ sys.sysName }}</el-menu-item>
        </el-menu>
      </div>
      <div class="header-right">
        <el-icon class="header-icon" @click="showMenuSearch = true" title="菜单搜索">
          <Search />
        </el-icon>
        <el-icon class="header-icon" @click="toggleFullscreen" :title="isFullscreen ? '退出全屏' : '全屏'">
          <FullScreen />
        </el-icon>
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="28" icon="UserFilled" />
            <span class="username">{{ userStore.userInfo.realName || userStore.userInfo.userName || '用户' }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="changePwd">修改密码</el-dropdown-item>
              <el-dropdown-item command="logout">退出系统</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    
    <!-- 修改密码对话框 -->
    <el-dialog v-model="showChangePwd" title="修改密码" width="450px" :close-on-click-modal="false">
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px" @submit.prevent>
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChangePwd = false">取消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="handleChangePwd">确定</el-button>
      </template>
    </el-dialog>

    <!-- 菜单搜索对话框 -->
    <el-dialog v-model="showMenuSearch" title="菜单搜索" width="600px" :close-on-click-modal="true">
      <el-input v-model="menuSearchKeyword" placeholder="请输入菜单名称" prefix-icon="Search" clearable @input="handleMenuSearch" />
      <div class="menu-search-results">
        <div v-if="searchResults.length === 0" class="no-result">暂无搜索结果</div>
        <div v-else class="result-list">
          <div v-for="item in searchResults" :key="item.path" class="result-item" @click="handleSearchResultClick(item)">
            <el-icon><Document /></el-icon>
            <span>{{ item.title }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
    
    <el-container class="main-container">
      <el-aside v-if="isAsideVisible" :width="isCollapse ? '64px' : `${asideWidth}px`" class="aside">
        <el-menu :key="menuKey" :default-active="activeMenu" :default-openeds="defaultOpeneds" :collapse="isCollapse" :unique-opened="true" @select="handleMenuSelect" background-color="#1b6eb3" text-color="rgba(255,255,255,0.9)" active-text-color="#fff">
          <MenuItem v-for="menu in menus" :key="menu.menuId" :menu="menu" />
        </el-menu>
        <!-- 拖拽调整宽度的手柄 -->
        <div 
          v-if="!isCollapse" 
          class="resize-handle" 
          @mousedown="startResize"
        />
      </el-aside>
      <el-container class="content-container">
        <div class="app-main">
          <el-tabs v-model="activeTab" type="card" @tab-remove="closeTab" @tab-change="handleTabChange">
            <el-tab-pane 
              v-for="view in visitedViews" 
              :key="view.path" 
              :label="view.title" 
              :name="view.path" 
              :closable="view.path !== '/home'"
              lazy
            >
              <!-- 外部系统iframe -->
              <iframe 
                v-if="view.isIframe" 
                :src="iframeCache[view.path]?.url" 
                class="iframe-content" 
                frameborder="0"
              />
              <!-- 内部页面组件 -->
              <component v-else :is="componentMap[view.path]" />
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-container>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, watch, defineAsyncComponent } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { systemApi } from '@/api/system'
import { changePassword } from '@/api/auth'
import MenuItem from '@/components/MenuItem.vue'

const router = useRouter()

// 内部页面组件映射
const componentMap = {
  '/home': defineAsyncComponent(() => import('@/views/home/index.vue')),
  'system/user': defineAsyncComponent(() => import('@/views/system/user/index.vue')),
  'system/role': defineAsyncComponent(() => import('@/views/system/role/index.vue')),
  'system/menu': defineAsyncComponent(() => import('@/views/system/menu/index.vue')),
  'system/org': defineAsyncComponent(() => import('@/views/system/org/index.vue')),
  'system/system': defineAsyncComponent(() => import('@/views/system/system/index.vue')),
  'system/dict': defineAsyncComponent(() => import('@/views/system/dict/index.vue')),
  'system/config': defineAsyncComponent(() => import('@/views/system/config/index.vue')),
  'system/notice': defineAsyncComponent(() => import('@/views/system/notice/index.vue')),
  'system/post': defineAsyncComponent(() => import('@/views/system/post/index.vue')),
  'system/para': defineAsyncComponent(() => import('@/views/system/para/index.vue')),
  'system/area': defineAsyncComponent(() => import('@/views/system/area/index.vue')),
  'system/admdvs': defineAsyncComponent(() => import('@/views/system/admdvs/index.vue')),
  'log/operlog': defineAsyncComponent(() => import('@/views/log/operlog/index.vue')),
  'log/loginlog': defineAsyncComponent(() => import('@/views/log/loginlog/index.vue')),
  'log/apilog': defineAsyncComponent(() => import('@/views/log/apilog/index.vue'))
}


const userStore = useUserStore()
const isCollapse = ref(false)
const isAsideVisible = ref(true)
const menus = ref([])
const systems = ref([])
const isFullscreen = ref(false)
const showMenuSearch = ref(false)
const menuSearchKeyword = ref('')
const searchResults = ref([])
const menuKey = ref(0) // 用于强制刷新菜单组件

// 侧边栏宽度调整
const asideWidth = ref(210)
const isResizing = ref(false)

const startResize = (e) => {
  isResizing.value = true
  document.addEventListener('mousemove', handleResize)
  document.addEventListener('mouseup', stopResize)
  document.body.style.cursor = 'col-resize'
  document.body.style.userSelect = 'none'
}

const handleResize = (e) => {
  if (!isResizing.value) return
  const newWidth = e.clientX
  // 限制宽度范围：最小150px，最大400px
  if (newWidth >= 150 && newWidth <= 400) {
    asideWidth.value = newWidth
  }
}

const stopResize = () => {
  isResizing.value = false
  document.removeEventListener('mousemove', handleResize)
  document.removeEventListener('mouseup', stopResize)
  document.body.style.cursor = ''
  document.body.style.userSelect = ''
}

// 菜单少于5个时自动展开第一个菜单
const defaultOpeneds = computed(() => {
  if (menus.value.length > 0 && menus.value.length < 5) {
    const firstMenu = menus.value[0]
    if (firstMenu && firstMenu.children && firstMenu.children.length) {
      return [String(firstMenu.menuId)]
    }
  }
  return []
})

// 修改密码相关
const showChangePwd = ref(false)
const pwdLoading = ref(false)
const pwdFormRef = ref(null)
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

// 密码复杂度验证：至少8个字符，包含数字、字母、特殊符号
const validateNewPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入新密码'))
    return
  }
  if (value.length < 8) {
    callback(new Error('密码长度至少8个字符'))
    return
  }
  let complexity = 0
  if (/[0-9]/.test(value)) complexity++
  if (/[a-zA-Z]/.test(value)) complexity++
  if (/[^0-9a-zA-Z]/.test(value)) complexity++
  if (complexity < 3) {
    callback(new Error('密码必须包含数字、字母、特殊符号'))
    return
  }
  callback()
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (value !== pwdForm.value.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ]
}

// 从 sessionStorage 恢复选中的子系统，如果没有则默认为'1'
const savedSystem = sessionStorage.getItem('activeSystem')
const activeSystem = ref(savedSystem || '')

const activeMenu = ref('')
const activeTab = ref('/home')

// 刷新后只保留首页tab
const visitedViews = ref([{ path: '/home', title: '首页' }])

// iframe缓存
const iframeCache = ref({})

// 原始菜单数据（包含一级节点）
const rawMenus = ref([])

// 加载菜单
const loadMenus = async (sysId) => {
  try {
    const res = await userStore.getMenuList(sysId)
    if (res && res.code === 0) {
      rawMenus.value = res.data
      // 跳过一级节点（parent_id=0），直接展示二级节点
      // 将所有一级节点的children合并作为左侧菜单树的根节点
      const secondLevelMenus = []
      res.data.forEach(rootMenu => {
        if (rootMenu.children && rootMenu.children.length) {
          secondLevelMenus.push(...rootMenu.children)
        }
      })
      menus.value = secondLevelMenus
      // 强制刷新菜单组件，使 default-openeds 生效
      menuKey.value++
    }
  } catch (error) {
    // 错误已在request拦截器中处理
  }
}

// 监听子系统切换，保存到 sessionStorage 并重新加载菜单
watch(activeSystem, async (newSystem) => {
  sessionStorage.setItem('activeSystem', newSystem)
  await loadMenus(newSystem)
})

onMounted(async () => {
  try {
    await userStore.getUserInfo()
    
    // 获取子系统列表
    const sysRes = await systemApi.list()
    if (sysRes.code === 0 && sysRes.data.length > 0) {
      systems.value = sysRes.data
      // 如果没有保存的系统，默认选择第一个
      if (!activeSystem.value) {
        activeSystem.value = String(sysRes.data[0].sysId)
      }
    }
    
    // 加载菜单
    await loadMenus(activeSystem.value)
    
    // 初始化
    activeMenu.value = '/home'
    activeTab.value = '/home'
  } catch (error) {
    // 错误已在request拦截器中处理
  }
})

// 不再监听路由变化，因为我们不使用路由切换页面

const handleSystemSelect = (index) => {
  activeSystem.value = index
}

// 递归查找菜单项（在原始菜单数据中查找，包含一级节点）
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

// 获取当前系统信息
const getCurrentSystem = () => {
  return systems.value.find(s => String(s.sysId) === activeSystem.value)
}

// 菜单标题映射
const menuTitleMap = {
  '/home': '首页',
  'system/user': '用户管理',
  'system/role': '角色管理',
  'system/menu': '菜单管理',
  'system/org': '机构管理',
  'system/system': '应用管理',
  'system/dict': '字典管理',
  'system/config': '参数设置',
  'system/notice': '通知公告',
  'system/post': '岗位管理',
  'system/para': '业务参数',
  'system/area': '地区管理',
  'system/admdvs': '医保区划',
  'log/operlog': '操作日志',
  'log/loginlog': '登录日志',
  'log/apilog': 'API日志'
}

const handleMenuSelect = (index) => {
  const menuItem = findMenuItem(index)
  const currentSystem = getCurrentSystem()
  
  // 判断是否为外部系统链接（菜单路径以 /# 开头表示外部系统路由）
  const isExternalLink = index.startsWith('/#/')
  
  if (isExternalLink) {
    // 获取外部系统的基础URL
    const sysUrl = currentSystem?.sysUrl || ''
    // 构建完整的外部系统URL
    let externalUrl = sysUrl + menuItem.menuUrl
    // 如果不是完整URL，拼接当前域名
    if (!externalUrl.startsWith('http://') && !externalUrl.startsWith('https://')) {
      if (!externalUrl.startsWith('/')) {
        externalUrl = '/' + externalUrl
      }
      externalUrl = window.location.origin + externalUrl
    }
    
    const menuId = menuItem.menuId || Date.now()
    const title = menuItem.menuName || '外部页面'
    const iframePath = `iframe_${menuId}`
    
    // 缓存iframe信息
    if (!iframeCache.value[iframePath]) {
      iframeCache.value[iframePath] = { url: externalUrl, title: title }
    }
    
    // 添加到访问记录
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
  } else {
    // 内部页面
    const comp = componentMap[index]
    if (comp) {
      const title = menuTitleMap[index] || (menuItem?.menuName) || '页面'
      
      // 添加到访问记录
      const existingView = visitedViews.value.find(v => v.path === index)
      if (!existingView) {
        visitedViews.value.push({
          path: index,
          title: title
        })
      }
      
      activeTab.value = index
      activeMenu.value = index
    }
  }
}

const handleTabChange = (tabPath) => {
  if (!tabPath.startsWith('iframe_')) {
    activeMenu.value = tabPath
  }
}

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

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await userStore.doLogout()
    } catch (e) {
      // 忽略登出接口错误
    }
    router.push('/login')
  } else if (command === 'changePwd') {
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    showChangePwd.value = true
  }
}

// 修改密码
const handleChangePwd = async () => {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate()
  pwdLoading.value = true
  try {
    await changePassword({
      oldPassword: pwdForm.value.oldPassword,
      newPassword: pwdForm.value.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    showChangePwd.value = false
    // 清除sessionStorage中的系统选择
    sessionStorage.removeItem('activeSystem')
    // 强制退出登录
    try {
      await userStore.doLogout()
    } catch (e) {
      // 忽略登出接口错误
    }
    router.push('/login')
  } catch (error) {
    // 错误已在request拦截器中处理
  } finally {
    pwdLoading.value = false
  }
}

// 全屏切换
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
      isFullscreen.value = false
    }
  }
}

// 监听全屏状态变化
onMounted(() => {
  document.addEventListener('fullscreenchange', () => {
    isFullscreen.value = !!document.fullscreenElement
  })
})

// 菜单搜索 - 递归搜索所有层级
const handleMenuSearch = () => {
  if (!menuSearchKeyword.value.trim()) {
    searchResults.value = []
    return
  }
  
  const keyword = menuSearchKeyword.value.toLowerCase()
  const results = []
  
  // 递归遍历菜单查找匹配项
  const searchInMenus = (menuList, parentPath = '') => {
    menuList.forEach(menu => {
      const currentPath = parentPath ? `${parentPath} / ${menu.menuName}` : menu.menuName
      if (menu.menuName.toLowerCase().includes(keyword)) {
        results.push({
          path: menu.menuUrl,
          title: currentPath
        })
      }
      if (menu.children && menu.children.length) {
        searchInMenus(menu.children, currentPath)
      }
    })
  }
  
  // 在原始菜单数据中搜索（包含所有层级）
  searchInMenus(rawMenus.value)
  searchResults.value = results
}

// 点击搜索结果
const handleSearchResultClick = (item) => {
  showMenuSearch.value = false
  menuSearchKeyword.value = ''
  searchResults.value = []
  handleMenuSelect(item.path)
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.top-header {
  height: 50px;
  background: #1976d2;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0;
  
  .header-left {
    display: flex;
    align-items: center;
    flex: 1;
    
    .logo {
      display: flex;
      align-items: center;
      padding: 0 20px 0 10px;
      color: #fff;
      font-size: 16px;
      font-weight: 500;
      white-space: nowrap;
      gap: 10px;
      
      .menu-toggle-btn,
      .menu-collapse-btn {
        cursor: pointer;
        font-size: 24px;
        padding: 4px;
        border-radius: 4px;
        transition: all 0.2s;
        
        &:hover {
          background-color: rgba(255, 255, 255, 0.15);
        }
      }
    
    }
    
    .system-menu {
      flex: 1;
      border: none;
      background-color: #1976d2;
      height: 50px;
      
      :deep(.el-menu-item) {
        height: 50px;
        line-height: 50px;
        border-bottom: none;
        font-size: 14px;
        background-color: #1976d2;
        padding: 0 15px;
        
        &:hover {
          background-color: rgba(255, 255, 255, 0.15);
        }
        
        &.is-active {
          background-color: #1976d2;
          color: #ff5252;
          border-bottom: 3px solid #ff5252;
          font-weight: 500;
        }
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    padding: 0 20px;
    gap: 20px;
    
    .header-icon {
      color: #fff;
      font-size: 18px;
      cursor: pointer;
      
      &:hover {
        opacity: 0.8;
      }
    }
    
    // 去除下拉菜单触发器的默认 focus 边框
    :deep(.el-dropdown) {
      outline: none;
      
      &:focus-visible {
        outline: none;
      }
    }
    
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      color: #fff;
      outline: none;
      
      .username {
        margin-left: 8px;
        font-size: 14px;
      }
      
      &:hover {
        opacity: 0.8;
      }
    }
  }
}

.main-container {
  flex: 1;
  overflow: hidden;
}

.aside {
  background: #1b6eb3;
  overflow-x: hidden;
  overflow-y: auto;
  position: relative;
  
  // 自定义滚动条样式
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.2);
    border-radius: 3px;
    
    &:hover {
      background: rgba(255, 255, 255, 0.35);
    }
  }
  
  .el-menu {
    border-right: none;
    
    // 一级菜单
    > :deep(.el-sub-menu) > .el-sub-menu__title,
    > :deep(.el-menu-item) {
      height: 46px;
      line-height: 46px;
      font-size: 14px;
      padding: 0 8px 0 8px !important;
    }
    
    :deep(.el-sub-menu__title),
    :deep(.el-menu-item) {
      .el-icon {
        margin-right: 4px;
      }
      
      &:hover {
        background-color: rgba(255, 255, 255, 0.1);
      }
    }
    
    :deep(.el-sub-menu__icon-arrow) {
      right: 6px;
    }
    
    :deep(.el-menu-item.is-active) {
      background-color: rgba(255, 255, 255, 0.15);
    }
    
    // 二级菜单
    :deep(.el-sub-menu .el-menu-item) {
      height: 44px;
      line-height: 44px;
    }
    
    :deep(.el-sub-menu .el-sub-menu > .el-sub-menu__title) {
      height: 44px;
      line-height: 44px;
    }
    
    // 三级菜单
    :deep(.el-sub-menu .el-sub-menu .el-menu-item),
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu > .el-sub-menu__title) {
      height: 42px;
      line-height: 42px;
    }
  }
  
  // 拖拽调整宽度的手柄
  .resize-handle {
    position: absolute;
    top: 0;
    right: 0;
    width: 4px;
    height: 100%;
    cursor: col-resize;
    background: transparent;
    transition: background 0.2s;
    z-index: 10;
    
    &:hover {
      background: rgba(255, 255, 255, 0.3);
    }
  }
}

.content-container {
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
  overflow: hidden;
}

.app-main {
  height: 100%;
  
  :deep(.el-tabs) {
    height: 100%;
    display: flex;
    flex-direction: column;
    
    .el-tabs__header {
      margin: 0;
      background: #f5f5f5;
      border-bottom: 1px solid #e8e8e8;
      padding: 0 8px;
      
      .el-tabs__nav-wrap::after {
        display: none;
      }
      
      .el-tabs__nav {
        border: none;
      }
      
      .el-tabs__item {
        height: 40px;
        line-height: 40px;
        border: none;
        padding: 0 16px;
        margin-right: 4px;
        font-size: 13px;
        background: transparent;
        color: #606266;
        transition: all 0.2s;
        
        &:hover {
          background: rgba(25, 118, 210, 0.05);
          color: #1976d2;
        }
        
        &.is-active {
          background: #fff;
          color: #1976d2;
          font-weight: 500;
        }
      }
    }
    
    .el-tabs__content {
      flex: 1;
      padding: 0;
      overflow: hidden;
    }
  }
  
  :deep(.el-tab-pane) {
    height: 100%;
    background: #fff;
    overflow: auto;
    
    // iframe 页面不需要外层滚动条
    &:has(.iframe-content) {
      overflow: hidden;
    }
  }
  
  .iframe-content {
    width: 100%;
    height: 100%;
    border: none;
    display: block;
  }
}

.menu-search-results {
  margin-top: 20px;
  max-height: 400px;
  overflow-y: auto;
  
  .no-result {
    text-align: center;
    color: #999;
    padding: 40px 0;
  }
  
  .result-list {
    .result-item {
      display: flex;
      align-items: center;
      padding: 12px 16px;
      cursor: pointer;
      border-radius: 4px;
      transition: all 0.2s;
      gap: 10px;
      
      &:hover {
        background-color: #f5f7fa;
        color: #1976d2;
      }
      
      .el-icon {
        font-size: 16px;
      }
    }
  }
}
</style>
