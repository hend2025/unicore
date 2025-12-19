<template>
  <!-- 顶部导航栏 -->
  <el-header class="top-header">
    <!-- 左侧区域：Logo + 系统切换菜单 -->
    <div class="header-left">
      <!-- Logo 和折叠按钮 -->
      <div class="logo">
        <span>{{ systemTitle }}</span>
        <!-- 点击切换侧边栏折叠状态 -->
        <el-icon class="menu-collapse-btn" @click="$emit('toggle-collapse')">
          <!-- 根据折叠状态显示不同图标 -->
          <Fold v-if="!isCollapse" />
          <Expand v-else />
        </el-icon>
      </div>
      
      <!-- 顶部系统切换菜单（水平模式） -->
      <el-menu 
        mode="horizontal" 
        :default-active="activeSystem" 
        class="system-menu" 
        background-color="#1976d2" 
        text-color="#fff" 
        active-text-color="#fff" 
        @select="handleSystemSelect"
      >
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
      <el-icon class="header-icon" @click="$emit('show-search')" title="菜单搜索">
        <Search />
      </el-icon>
      
      <!-- 全屏切换按钮 -->
      <el-icon class="header-icon" @click="toggleFullscreen" :title="isFullscreen ? '退出全屏' : '全屏'">
        <FullScreen />
      </el-icon>
      
      <!-- 用户下拉菜单 -->
      <UserDropdown 
        :user-info="userInfo" 
        @change-password="$emit('change-password')"
        @logout="$emit('logout')"
      />
    </div>
  </el-header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import UserDropdown from './UserDropdown.vue'

const props = defineProps({
  systemTitle: { type: String, default: '医保基金综合监管平台' },
  systems: { type: Array, default: () => [] },
  activeSystem: { type: String, default: '' },
  isCollapse: { type: Boolean, default: false },
  userInfo: { type: Object, default: () => ({}) }
})

const emit = defineEmits([
  'toggle-collapse', 
  'system-select', 
  'show-search', 
  'change-password', 
  'logout'
])

const isFullscreen = ref(false)

const handleSystemSelect = (index) => {
  emit('system-select', index)
}

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
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}

onMounted(() => {
  document.addEventListener('fullscreenchange', handleFullscreenChange)
})

onUnmounted(() => {
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
})
</script>

<style scoped lang="scss">
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
  }
}
</style>
