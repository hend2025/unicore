<!--
  框架布局组件 - 整个应用的主框架
  包含：顶部导航栏、侧边菜单、内容区域（多标签页）
  
  布局结构：
  ┌─────────────────────────────────────────────────┐
  │  顶部导航栏 (el-header)                          │
  │  - Logo + 折叠按钮                               │
  │  - 系统切换菜单（水平）                           │
  │  - 搜索/全屏/用户下拉                            │
  ├──────────┬──────────────────────────────────────┤
  │ 侧边菜单  │  内容区域                             │
  │(el-aside)│  - 标签页导航                         │
  │          │  - 页面内容 / iframe                  │
  └──────────┴──────────────────────────────────────┘
-->
<template>
  <!-- 最外层容器，使用 Element Plus 的布局组件 -->
  <el-container class="layout-container">
    
    <!-- ==================== 顶部导航栏 ==================== -->
    <el-header class="top-header">
      <!-- 左侧区域：Logo + 系统切换菜单 -->
      <div class="header-left">
        <!-- Logo 和折叠按钮 -->
        <div class="logo">
          <span>医保基金综合监管平台</span>
          <!-- 点击切换侧边栏折叠状态 -->
          <el-icon class="menu-collapse-btn" @click="isCollapse = !isCollapse">
            <!-- 根据折叠状态显示不同图标 -->
            <Fold v-if="!isCollapse" />   <!-- 展开状态显示"折叠"图标 -->
            <Expand v-else />              <!-- 折叠状态显示"展开"图标 -->
          </el-icon>
        </div>
        
        <!-- 顶部系统切换菜单（水平模式） -->
        <!-- 用于在多个子系统之间切换，切换后会重新加载对应系统的菜单 -->
        <el-menu 
          mode="horizontal" 
          :default-active="activeSystem" 
          class="system-menu" 
          background-color="#1976d2" 
          text-color="#fff" 
          active-text-color="#fff" 
          @select="handleSystemSelect"
        >
          <!-- 遍历渲染系统列表 -->
          <el-menu-item 
            v-for="sys in systems" 
            :key="sys.sysId" 
            :index="String(sys.sysId)"
          >
            {{ sys.sysName }}
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 右侧区域：功能按钮 + 用户信息 -->
      <div class="header-right">
        <!-- 菜单搜索按钮 -->
        <el-icon class="header-icon" @click="showMenuSearch = true" title="菜单搜索">
          <Search />
        </el-icon>
        
        <!-- 全屏切换按钮 -->
        <el-icon class="header-icon" @click="toggleFullscreen" :title="isFullscreen ? '退出全屏' : '全屏'">
          <FullScreen />
        </el-icon>
        
        <!-- 用户下拉菜单 -->
        <!-- command 事件会传递 el-dropdown-item 的 command 属性值 -->
        <el-dropdown @command="handleCommand">
          <!-- 触发下拉的元素 -->
          <span class="user-info">
            <el-avatar :size="28" icon="UserFilled" />
            <!-- 优先显示真实姓名，其次用户名，最后显示"用户" -->
            <span class="username">{{ userStore.userInfo.realName || userStore.userInfo.userName || '未登录' }}</span>
          </span>
          <!-- 下拉菜单内容 -->
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="changePwd">修改密码</el-dropdown-item>
              <el-dropdown-item command="logout">退出系统</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    
    <!-- ==================== 修改密码对话框 ==================== -->
    <el-dialog v-model="showChangePwd" title="修改密码" width="450px" :close-on-click-modal="false">
      <!-- 密码表单，@submit.prevent 阻止表单默认提交行为 -->
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
      <!-- 对话框底部按钮 -->
      <template #footer>
        <el-button @click="showChangePwd = false">取消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="handleChangePwd">确定</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 菜单搜索对话框 ==================== -->
    <el-dialog v-model="showMenuSearch" title="菜单搜索" width="600px" :close-on-click-modal="true">
      <!-- 搜索输入框，@input 实时搜索 -->
      <el-input v-model="menuSearchKeyword" placeholder="请输入菜单名称" prefix-icon="Search" clearable @input="handleMenuSearch" />
      <!-- 搜索结果列表 -->
      <div class="menu-search-results">
        <div v-if="searchResults.length === 0" class="no-result">暂无搜索结果</div>
        <div v-else class="result-list">
          <!-- 点击搜索结果跳转到对应菜单 -->
          <div v-for="item in searchResults" :key="item.path" class="result-item" @click="handleSearchResultClick(item)">
            <el-icon><Document /></el-icon>
            <span>{{ item.title }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
    
    <!-- ==================== 主内容区域 ==================== -->
    <el-container class="main-container">
      
      <!-- ========== 侧边栏菜单 ========== -->
      <!-- v-if 控制侧边栏显示/隐藏 -->
      <!-- :width 根据折叠状态动态设置宽度 -->
      <el-aside v-if="isAsideVisible" :width="isCollapse ? '42px' : `${asideWidth}px`" class="aside">
        <!-- 
          侧边栏菜单组件
          :key - 用于强制刷新菜单组件，使 default-openeds 生效
          :default-active - 当前激活的菜单项
          :default-openeds - 默认展开的子菜单（菜单少于5个时自动展开第一个）
          :collapse - 是否折叠菜单
          :unique-opened - 是否只保持一个子菜单展开
          @select - 菜单选择事件
        -->
        <el-menu 
          :key="menuKey" 
          :default-active="activeMenu" 
          :default-openeds="defaultOpeneds" 
          :collapse="isCollapse" 
          :unique-opened="true" 
          @select="handleMenuSelect" 
          background-color="#1b6eb3" 
          text-color="rgba(255,255,255,0.9)" 
          active-text-color="#fff"
        >
          <!-- 使用递归组件渲染多级菜单 -->
          <MenuItem v-for="menu in menus" :key="menu.menuId" :menu="menu" />
        </el-menu>
        
        <!-- 拖拽调整侧边栏宽度的手柄（仅在非折叠状态显示） -->
        <div 
          v-if="!isCollapse" 
          class="resize-handle" 
          @mousedown="startResize"
        />
      </el-aside>

      <!-- ========== 内容区域容器 ========== -->
      <el-container class="content-container">
        <div class="app-main">
          <div class="tabs-wrapper">
            <!-- 
              多标签页组件
              v-model - 当前激活的标签页
              type="card" - 卡片风格
              @tab-remove - 关闭标签事件
              @tab-change - 切换标签事件
            -->
            <el-tabs v-model="activeTab" type="card" @tab-remove="closeTab" @tab-change="handleTabChange">
              <!-- 遍历已访问的页面列表，为每个页面创建一个标签页 -->
              <el-tab-pane 
                v-for="view in visitedViews" 
                :key="view.path" 
                :label="view.title" 
                :name="view.path" 
                :closable="view.path !== '/home'"  
                lazy
              >
                <!-- 
                  根据页面类型渲染不同内容：
                  1. 外部系统 - 使用 iframe 嵌入
                  2. 内部页面 - 使用动态组件渲染
                -->
                <!-- 外部系统用 iframe 嵌入 -->
                <iframe 
                  v-if="view.isIframe" 
                  :src="iframeCache[view.path]?.url" 
                  class="iframe-content" 
                  frameborder="0"
                />
                <!-- 内部页面用动态组件，从 componentMap 中获取对应组件 -->
                <component v-else :is="componentMap[view.path]" />
              </el-tab-pane>
            </el-tabs>
            
            <!-- 标签页批量关闭按钮 -->
            <el-dropdown class="tabs-close-btn" @command="handleTabsCommand">
              <el-icon><Close /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="closeOther">关闭其他</el-dropdown-item>
                  <el-dropdown-item command="closeAll">关闭全部</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-container>
    </el-container>
  </el-container>
</template>

<script setup>
/**
 * ==================== 导入依赖 ====================
 */
// Vue 核心 API
import { ref, computed, onMounted, watch, defineAsyncComponent } from 'vue'
// Vue Router - 用于页面跳转（仅登录/登出时使用）
import { useRouter } from 'vue-router'
// Element Plus 消息提示
import { ElMessage } from 'element-plus'
// Pinia 用户状态管理
import { useUserStore } from '@/stores/user'
// API 接口
import { changePassword, getSystems } from '@/api/auth'
// 递归菜单组件
import MenuItem from '@/components/MenuItem.vue'
// 验证器
import { passwordValidator, createConfirmPasswordValidator } from '@/utils/validators'

/**
 * ==================== 内部页面组件映射表 ====================
 * 使用 defineAsyncComponent 实现组件懒加载
 * key: 菜单路径
 * value: 异步加载的组件
 * 
 * 这种方式的好处：
 * 1. 不依赖 Vue Router 切换页面，可以实现多标签页同时保持状态
 * 2. 组件按需加载，提升首屏性能
 * 3. 配合 el-tabs 实现类似浏览器多标签的效果
 */
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

/**
 * ==================== 基础状态定义 ====================
 */
const router = useRouter()           // 路由实例
const userStore = useUserStore()     // 用户状态 store
const isCollapse = ref(false)        // 侧边栏是否折叠
const isAsideVisible = ref(true)     // 侧边栏是否可见
const menus = ref([])                // 左侧菜单数据（处理后的二级菜单）
const systems = ref([])              // 顶部系统列表
const isFullscreen = ref(false)      // 是否全屏状态
const showMenuSearch = ref(false)    // 是否显示菜单搜索对话框
const menuSearchKeyword = ref('')    // 菜单搜索关键词
const searchResults = ref([])        // 菜单搜索结果
const menuKey = ref(0)               // 菜单组件 key，用于强制刷新

/**
 * ==================== 侧边栏宽度调整 ====================
 * 实现拖拽调整侧边栏宽度的功能
 */
const asideWidth = ref(210)          // 侧边栏宽度（像素）
const isResizing = ref(false)        // 是否正在拖拽调整

/**
 * 开始拖拽调整宽度
 * @param {MouseEvent} e - 鼠标事件
 */
const startResize = (e) => {
  isResizing.value = true
  // 添加全局鼠标事件监听
  document.addEventListener('mousemove', handleResize)
  document.addEventListener('mouseup', stopResize)
  // 设置鼠标样式为左右调整
  document.body.style.cursor = 'col-resize'
  // 禁止文本选择，避免拖拽时选中文字
  document.body.style.userSelect = 'none'
}

/**
 * 处理拖拽过程中的宽度调整
 * @param {MouseEvent} e - 鼠标事件
 */
const handleResize = (e) => {
  if (!isResizing.value) return
  const newWidth = e.clientX  // 鼠标 X 坐标即为新宽度
  // 限制宽度范围：最小 150px，最大 400px
  if (newWidth >= 150 && newWidth <= 400) {
    asideWidth.value = newWidth
  }
}

/**
 * 停止拖拽调整
 */
const stopResize = () => {
  isResizing.value = false
  // 移除全局事件监听
  document.removeEventListener('mousemove', handleResize)
  document.removeEventListener('mouseup', stopResize)
  // 恢复鼠标样式和文本选择
  document.body.style.cursor = ''
  document.body.style.userSelect = ''
}

/**
 * ==================== 菜单默认展开逻辑 ====================
 * 当菜单数量少于 5 个时，自动展开第一个有子菜单的菜单项
 */
const defaultOpeneds = computed(() => {
  if (menus.value.length > 0 && menus.value.length < 5) {
    const firstMenu = menus.value[0]
    if (firstMenu && firstMenu.children && firstMenu.children.length) {
      return [String(firstMenu.menuId)]
    }
  }
  return []
})

/**
 * ==================== 修改密码相关 ====================
 */
const showChangePwd = ref(false)     // 是否显示修改密码对话框
const pwdLoading = ref(false)        // 修改密码按钮 loading 状态
const pwdFormRef = ref(null)         // 密码表单引用，用于调用 validate()
const pwdForm = ref({                // 密码表单数据
  oldPassword: '', 
  newPassword: '', 
  confirmPassword: '' 
})

/**
 * 密码表单验证规则
 */
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, validator: passwordValidator, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: createConfirmPasswordValidator(() => pwdForm.value.newPassword), trigger: 'blur' }
  ]
}

/**
 * ==================== 系统和菜单状态 ====================
 */
// 从 sessionStorage 恢复上次选中的系统，实现刷新后保持状态
const savedSystem = sessionStorage.getItem('activeSystem')
const activeSystem = ref(savedSystem || '')  // 当前选中的系统 ID

const activeMenu = ref('')           // 当前激活的菜单项路径
const activeTab = ref('/home')       // 当前激活的标签页路径

// 已访问的页面列表（标签页数据源）
// 刷新后只保留首页，其他标签页会丢失
const visitedViews = ref([{ path: '/home', title: '首页' }])

// iframe 缓存，存储外部系统页面的 URL 和标题
// key: iframe 路径标识（如 'iframe_123'）
// value: { url: 完整URL, title: 页面标题 }
const iframeCache = ref({})

// 原始菜单数据（包含一级节点），用于菜单搜索
const rawMenus = ref([])

/**
 * ==================== 加载菜单数据 ====================
 * @param {string|number} sysId - 系统 ID
 */
const loadMenus = async (sysId) => {
  try {
    const res = await userStore.getMenuList(sysId)
    if (res && res.code === 0) {
      // 保存原始菜单数据（用于搜索）
      rawMenus.value = res.data
      
      // 处理菜单数据：跳过一级节点，直接展示二级节点
      // 因为一级节点通常对应顶部的系统菜单
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
    // 错误已在 request 拦截器中处理
  }
}

/**
 * 监听系统切换
 * 当用户切换系统时，保存选择并重新加载对应系统的菜单
 */
watch(activeSystem, async (newSystem) => {
  sessionStorage.setItem('activeSystem', newSystem)
  await loadMenus(newSystem)
})

/**
 * ==================== 页面初始化 ====================
 * 组件挂载后执行的初始化逻辑
 */
onMounted(async () => {
  try {
    // 1. 获取当前登录用户信息
    await userStore.getUserInfo()
    
    // 2. 获取用户有权限访问的系统列表
    const sysRes = await getSystems()
    if (sysRes.code === 0 && sysRes.data.length > 0) {
      systems.value = sysRes.data
      
      // 如果没有保存的系统选择，默认选择第一个
      if (!activeSystem.value) {
        activeSystem.value = String(sysRes.data[0].sysId)
      }
      
      // 验证保存的系统是否在用户有权限的列表中
      // 如果不在（可能权限变更），重置为第一个
      const savedSystemExists = sysRes.data.some(s => String(s.sysId) === activeSystem.value)
      if (!savedSystemExists) {
        activeSystem.value = String(sysRes.data[0].sysId)
      }
    }
    
    // 3. 加载当前系统的菜单
    await loadMenus(activeSystem.value)
    
    // 4. 初始化激活状态
    activeMenu.value = '/home'
    activeTab.value = '/home'
  } catch (error) {
    // 错误已在 request 拦截器中处理（如 401 会自动跳转登录页）
  }
})

/**
 * ==================== 系统切换处理 ====================
 * @param {string} index - 选中的系统 ID
 */
const handleSystemSelect = (index) => {
  activeSystem.value = index
}

/**
 * ==================== 菜单项查找 ====================
 * 递归查找菜单项（在原始菜单数据中查找，包含所有层级）
 * @param {string} menuUrl - 菜单 URL
 * @param {Array} menuList - 菜单列表
 * @param {Object} parent - 父级菜单
 * @returns {Object|null} 找到的菜单项
 */
const findMenuItem = (menuUrl, menuList = rawMenus.value, parent = null) => {
  for (const menu of menuList) {
    if (menu.menuUrl === menuUrl) {
      return parent ? { ...menu, parentMenu: parent } : menu
    }
    // 递归查找子菜单
    if (menu.children && menu.children.length) {
      const found = findMenuItem(menuUrl, menu.children, menu)
      if (found) return found
    }
  }
  return null
}

/**
 * 获取当前选中的系统信息
 * @returns {Object|undefined} 系统信息对象
 */
const getCurrentSystem = () => {
  return systems.value.find(s => String(s.sysId) === activeSystem.value)
}

/**
 * ==================== 菜单标题映射表 ====================
 * 用于显示标签页标题
 */
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

/**
 * ==================== 菜单选择处理（核心逻辑） ====================
 * 处理侧边栏菜单点击事件
 * 
 * 逻辑说明：
 * 1. 首先检查点击的菜单是否在 componentMap 中（内部页面）
 * 2. 如果是内部页面，使用动态组件渲染
 * 3. 如果不是内部页面，当作外部链接处理，使用 iframe 嵌入
 * 
 * @param {string} index - 菜单的 menuUrl
 */
const handleMenuSelect = (index) => {
  // 查找菜单项详细信息
  const menuItem = findMenuItem(index)
  // 获取当前系统信息（用于拼接外部系统 URL）
  const currentSystem = getCurrentSystem()
  
  // 检查是否为内部页面组件
  const comp = componentMap[index]
  
  if (comp) {
    // ========== 内部页面处理 ==========
    // 获取页面标题：优先使用映射表，其次使用菜单名称
    const title = menuTitleMap[index] || (menuItem?.menuName) || '页面'
    
    // 检查是否已经打开过这个页面
    const existingView = visitedViews.value.find(v => v.path === index)
    if (!existingView) {
      // 添加到已访问列表（新增标签页）
      visitedViews.value.push({
        path: index,
        title: title
      })
    }
    
    // 激活对应的标签页和菜单项
    activeTab.value = index
    activeMenu.value = index
  } else {
    // ========== 外部系统链接处理 ==========
    // 获取系统基础 URL
    const sysUrl = currentSystem?.sysUrl || ''
    
    // 构建完整的外部系统 URL
    let externalUrl = sysUrl + (menuItem?.menuUrl || index)
    
    // 如果不是完整 URL（不以 http:// 或 https:// 开头），拼接当前域名
    if (!externalUrl.startsWith('http://') && !externalUrl.startsWith('https://')) {
      if (!externalUrl.startsWith('/')) {
        externalUrl = '/' + externalUrl
      }
      externalUrl = window.location.origin + externalUrl
    }
    
    // 生成唯一的 iframe 路径标识
    const menuId = menuItem?.menuId || Date.now()
    const title = menuItem?.menuName || '外部页面'
    const iframePath = `iframe_${menuId}`
    
    // 缓存 iframe 信息（避免重复创建）
    if (!iframeCache.value[iframePath]) {
      iframeCache.value[iframePath] = { url: externalUrl, title: title }
    }
    
    // 添加到已访问列表
    const existingView = visitedViews.value.find(v => v.path === iframePath)
    if (!existingView) {
      visitedViews.value.push({
        path: iframePath,
        title: title,
        isIframe: true  // 标记为 iframe 类型
      })
    }
    
    // 激活标签页（菜单高亮保持原始 index）
    activeTab.value = iframePath
    activeMenu.value = index
  }
}

/**
 * ==================== 标签页切换处理 ====================
 * @param {string} tabPath - 切换到的标签页路径
 */
const handleTabChange = (tabPath) => {
  // 如果不是 iframe 页面，同步更新菜单高亮
  if (!tabPath.startsWith('iframe_')) {
    activeMenu.value = tabPath
  }
}

/**
 * ==================== 关闭单个标签页 ====================
 * @param {string} targetPath - 要关闭的标签页路径
 */
const closeTab = (targetPath) => {
  // 查找要关闭的标签页索引
  const index = visitedViews.value.findIndex(v => v.path === targetPath)
  if (index > -1) {
    // 如果是 iframe 页面，清理缓存
    if (targetPath.startsWith('iframe_')) {
      delete iframeCache.value[targetPath]
    }
    
    // 从列表中移除
    visitedViews.value.splice(index, 1)
    
    // 如果关闭的是当前激活的标签页，需要切换到其他标签
    if (targetPath === activeTab.value) {
      // 优先切换到后一个，没有则切换到前一个
      const nextTab = visitedViews.value[index] || visitedViews.value[index - 1]
      if (nextTab) {
        activeTab.value = nextTab.path
        // 同步更新菜单高亮（非 iframe 页面）
        if (!nextTab.path.startsWith('iframe_')) {
          activeMenu.value = nextTab.path
        }
      }
    }
  }
}

/**
 * 关闭其他标签页（保留首页和当前标签）
 */
const closeOtherTabs = () => {
  visitedViews.value = visitedViews.value.filter(view => 
    view.path === '/home' || view.path === activeTab.value
  )
  // 清理不再需要的 iframe 缓存
  Object.keys(iframeCache.value).forEach(key => {
    if (key !== activeTab.value) {
      delete iframeCache.value[key]
    }
  })
}

/**
 * 关闭全部标签页（只保留首页）
 */
const closeAllTabs = () => {
  visitedViews.value = visitedViews.value.filter(view => view.path === '/home')
  activeTab.value = '/home'
  activeMenu.value = '/home'
  // 清理所有 iframe 缓存
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

/**
 * ==================== 用户下拉菜单命令处理 ====================
 * @param {string} command - 命令类型（'logout' | 'changePwd'）
 */
const handleCommand = async (command) => {
  if (command === 'logout') {
    // 退出登录
    try {
      await userStore.doLogout()
    } catch (e) {
      // 忽略登出接口错误，确保能跳转到登录页
    }
    router.push('/login')
  } else if (command === 'changePwd') {
    // 打开修改密码对话框
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    showChangePwd.value = true
  }
}

/**
 * ==================== 修改密码提交 ====================
 */
const handleChangePwd = async () => {
  if (!pwdFormRef.value) return
  
  // 表单验证
  await pwdFormRef.value.validate()
  
  pwdLoading.value = true
  try {
    // 调用修改密码接口
    await changePassword({
      oldPassword: pwdForm.value.oldPassword,
      newPassword: pwdForm.value.newPassword
    })
    
    ElMessage.success('密码修改成功，请重新登录')
    showChangePwd.value = false
    
    // 清除 sessionStorage 中的系统选择
    sessionStorage.removeItem('activeSystem')
    
    // 强制退出登录，跳转到登录页
    try {
      await userStore.doLogout()
    } catch (e) {
      // 忽略登出接口错误
    }
    router.push('/login')
  } catch (error) {
    // 错误已在 request 拦截器中处理
  } finally {
    pwdLoading.value = false
  }
}

/**
 * ==================== 全屏切换 ====================
 */
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    // 进入全屏
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    // 退出全屏
    if (document.exitFullscreen) {
      document.exitFullscreen()
      isFullscreen.value = false
    }
  }
}

// 监听全屏状态变化（用户可能通过 ESC 键退出全屏）
onMounted(() => {
  document.addEventListener('fullscreenchange', () => {
    isFullscreen.value = !!document.fullscreenElement
  })
})

/**
 * ==================== 菜单搜索功能 ====================
 * 递归搜索所有层级的菜单，支持模糊匹配
 */
const handleMenuSearch = () => {
  // 空关键词时清空结果
  if (!menuSearchKeyword.value.trim()) {
    searchResults.value = []
    return
  }
  
  const keyword = menuSearchKeyword.value.toLowerCase()
  const results = []
  
  /**
   * 递归遍历菜单查找匹配项
   * @param {Array} menuList - 菜单列表
   * @param {string} parentPath - 父级路径（用于显示完整路径）
   */
  const searchInMenus = (menuList, parentPath = '') => {
    menuList.forEach(menu => {
      // 构建当前菜单的完整路径（如：系统管理 / 用户管理）
      const currentPath = parentPath ? `${parentPath} / ${menu.menuName}` : menu.menuName
      
      // 检查菜单名称是否包含关键词
      if (menu.menuName.toLowerCase().includes(keyword)) {
        results.push({
          path: menu.menuUrl,
          title: currentPath
        })
      }
      
      // 递归搜索子菜单
      if (menu.children && menu.children.length) {
        searchInMenus(menu.children, currentPath)
      }
    })
  }
  
  // 在原始菜单数据中搜索（包含所有层级）
  searchInMenus(rawMenus.value)
  searchResults.value = results
}

/**
 * 点击搜索结果，跳转到对应菜单
 * @param {Object} item - 搜索结果项 { path, title }
 */
const handleSearchResultClick = (item) => {
  // 关闭搜索对话框
  showMenuSearch.value = false
  menuSearchKeyword.value = ''
  searchResults.value = []
  
  // 触发菜单选择
  handleMenuSelect(item.path)
}
</script>

<!-- ==================== 样式部分 ==================== -->
<style scoped lang="scss">
/**
 * scoped: 样式只作用于当前组件
 * lang="scss": 使用 SCSS 预处理器
 */

/* 最外层布局容器 - 占满整个视口 */
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

/* ==================== 顶部导航栏样式 ==================== */
.top-header {
  height: 50px;
  background: #1976d2;  /* 主题蓝色 */
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0;
  
  /* 左侧区域：Logo + 系统菜单 */
  .header-left {
    display: flex;
    align-items: center;
    flex: 1;
    
    /* Logo 区域 */
    .logo {
      display: flex;
      align-items: center;
      padding: 0 20px 0 10px;
      color: #fff;
      font-size: 16px;
      font-weight: 500;
      white-space: nowrap;  /* 防止文字换行 */
      gap: 10px;
      
      /* 折叠/展开按钮 */
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
    
    /* 顶部系统切换菜单 */
    .system-menu {
      flex: 1;
      border: none;
      background-color: #1976d2;
      height: 50px;
      
      /* 
        :deep() - 深度选择器，用于穿透 scoped 样式
        修改 Element Plus 组件的内部样式
      */
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
        
        /* 激活状态：红色下划线 */
        &.is-active {
          background-color: #1976d2;
          color: #ff5252;
          border-bottom: 3px solid #ff5252;
          font-weight: 500;
        }
      }
    }
  }

  /* 右侧区域：功能按钮 + 用户信息 */
  .header-right {
    display: flex;
    align-items: center;
    padding: 0 20px;
    gap: 20px;
    
    /* 图标按钮样式 */
    .header-icon {
      color: #fff;
      font-size: 18px;
      cursor: pointer;
      
      &:hover {
        opacity: 0.8;
      }
    }
    
    /* 去除下拉菜单触发器的默认 focus 边框 */
    :deep(.el-dropdown) {
      outline: none;
      
      &:focus-visible {
        outline: none;
      }
    }
    
    /* 用户信息区域 */
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

/* ==================== 主内容区域容器 ==================== */
.main-container {
  flex: 1;
  overflow: hidden;  /* 防止内容溢出 */
}

/* ==================== 侧边栏样式 ==================== */
.aside {
  background: #1b6eb3;  /* 侧边栏背景色 */
  overflow-x: hidden;
  overflow-y: auto;
  position: relative;  /* 为拖拽手柄定位 */
  
  /* 自定义滚动条样式（Webkit 浏览器） */
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

  /* 侧边栏菜单样式 */
  .el-menu {
    border-right: none;  /* 去除默认右边框 */
    
    /* 一级菜单项样式 */
    > :deep(.el-sub-menu) > .el-sub-menu__title,
    > :deep(.el-menu-item) {
      height: 46px;
      line-height: 46px;
      font-size: 14px;
      padding: 0 8px 0 8px !important;
    }
    
    /* 菜单项通用样式 */
    :deep(.el-sub-menu__title),
    :deep(.el-menu-item) {
      .el-icon {
        margin-right: 2px;
      }
      
      &:hover {
        background-color: rgba(255, 255, 255, 0.1);
      }
    }
    
    /* 子菜单展开箭头位置 */
    :deep(.el-sub-menu__icon-arrow) {
      right: 6px;
    }
    
    /* 激活状态的菜单项 */
    :deep(.el-menu-item.is-active) {
      background-color: rgba(255, 255, 255, 0.15);
    }
    
    /* 
      多级菜单缩进样式
      通过嵌套选择器实现不同层级的缩进
    */
    
    /* 二级菜单 */
    :deep(.el-sub-menu .el-menu-item) {
      height: 44px;
      line-height: 44px;
      padding-left: 20px !important;
    }

    :deep(.el-sub-menu .el-sub-menu > .el-sub-menu__title) {
      height: 44px;
      line-height: 44px;
      padding-left: 20px !important;
    }
    
    /* 三级菜单 */
    :deep(.el-sub-menu .el-sub-menu .el-menu-item),
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu > .el-sub-menu__title) {
      height: 42px;
      line-height: 42px;
      padding-left: 32px !important;
    }

    /* 四级菜单 */
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-menu-item),
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu > .el-sub-menu__title) {
      padding-left: 44px !important;
    }

    /* 五级菜单 */
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-menu-item),
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu > .el-sub-menu__title) {
      padding-left: 56px !important;
    }

    /* 六级菜单 */
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-menu-item),
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu > .el-sub-menu__title) {
      padding-left: 68px !important;
    }

    /* 七级菜单 */
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-menu-item),
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu > .el-sub-menu__title) {
      padding-left: 80px !important;
    }
  }
  
  /* 拖拽调整宽度的手柄 */
  .resize-handle {
    position: absolute;
    top: 0;
    right: 0;
    width: 4px;
    height: 100%;
    cursor: col-resize;  /* 左右调整光标 */
    background: transparent;
    transition: background 0.2s;
    z-index: 10;
    
    &:hover {
      background: rgba(255, 255, 255, 0.3);
    }
  }
}

/* ==================== 内容区域样式 ==================== */
.content-container {
  display: flex;
  flex-direction: column;
  background: #f0f2f5;  /* 浅灰色背景 */
  overflow: hidden;
}

/* 主内容区域 */
.app-main {
  height: 100%;
  
  /* 标签页包装器 */
  .tabs-wrapper {
    height: 100%;
    display: flex;
    flex-direction: column;
    position: relative;
    
    /* 标签页批量关闭按钮 */
    .tabs-close-btn {
      position: absolute;
      top: 0;
      right: 0;
      height: 40px;
      width: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f5f5f5;
      border-bottom: 1px solid #e8e8e8;
      cursor: pointer;
      color: #606266;
      z-index: 10;
      
      &:hover {
        color: #1976d2;
        background: #e8e8e8;
      }
    }
  }
  
  /* Element Plus Tabs 组件样式覆盖 */
  :deep(.el-tabs) {
    height: 100%;
    display: flex;
    flex-direction: column;
    
    /* 标签页头部 */
    .el-tabs__header {
      margin: 0;
      background: #f5f5f5;
      border-bottom: 1px solid #e8e8e8;
      padding: 0 48px 0 8px;  /* 右侧留出关闭按钮的空间 */
      
      /* 隐藏默认的底部线条 */
      .el-tabs__nav-wrap::after {
        display: none;
      }
      
      .el-tabs__nav {
        border: none;
      }
      
      /* 单个标签项样式 */
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
        position: relative;
        
        /* 标签之间的分割线 */
        &::after {
          content: '';
          position: absolute;
          right: -2px;
          top: 50%;
          transform: translateY(-50%);
          width: 1px;
          height: 14px;
          background: #d9d9d9;
        }
        
        /* 最后一个标签不显示分割线 */
        &:last-child::after {
          display: none;
        }
        
        &:hover {
          background: rgba(25, 118, 210, 0.05);
          color: #1976d2;
        }
        
        /* 激活状态的标签 */
        &.is-active {
          background: #fff;
          color: #1976d2;
          font-weight: 500;
        }
      }
    }
    
    /* 标签页内容区域 */
    .el-tabs__content {
      flex: 1;
      padding: 0;
      overflow: hidden;
    }
  }
  
  /* 单个标签页面板 */
  :deep(.el-tab-pane) {
    height: 100%;
    background: #fff;
    overflow: auto;
    
    /* iframe 页面不需要外层滚动条 */
    &:has(.iframe-content) {
      overflow: hidden;
    }
  }
  
  /* iframe 内容样式 */
  .iframe-content {
    width: 100%;
    height: 100%;
    border: none;
    display: block;
  }
}

/* ==================== 菜单搜索结果样式 ==================== */
.menu-search-results {
  margin-top: 20px;
  max-height: 400px;
  overflow-y: auto;
  
  /* 无结果提示 */
  .no-result {
    text-align: center;
    color: #999;
    padding: 40px 0;
  }
  
  /* 结果列表 */
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
