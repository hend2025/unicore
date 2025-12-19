<template>
  <!-- 侧边栏菜单 -->
  <el-aside v-if="visible" :width="isCollapse ? '42px' : `${width}px`" class="aside">
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
      <MenuItem v-for="menu in menus" :key="menu.menuId" :menu="menu" />
    </el-menu>
    
    <!-- 拖拽调整侧边栏宽度的手柄 -->
    <div 
      v-if="!isCollapse" 
      class="resize-handle" 
      @mousedown="startResize"
    />
  </el-aside>
</template>

<script setup>
import { ref, computed } from 'vue'
import MenuItem from '@/components/MenuItem.vue'

const props = defineProps({
  menus: { type: Array, default: () => [] },
  activeMenu: { type: String, default: '' },
  isCollapse: { type: Boolean, default: false },
  visible: { type: Boolean, default: true },
  menuKey: { type: Number, default: 0 }
})

const emit = defineEmits(['menu-select', 'update:width'])

// 侧边栏宽度
const width = ref(210)
const isResizing = ref(false)

// 计算默认展开的菜单
const defaultOpeneds = computed(() => {
  if (props.menus.length > 0 && props.menus.length < 5) {
    const firstMenu = props.menus[0]
    if (firstMenu && firstMenu.children && firstMenu.children.length) {
      return [String(firstMenu.menuId)]
    }
  }
  return []
})

const handleMenuSelect = (index) => {
  emit('menu-select', index)
}

// 开始拖拽调整宽度
const startResize = (e) => {
  isResizing.value = true
  document.addEventListener('mousemove', handleResize)
  document.addEventListener('mouseup', stopResize)
  document.body.style.cursor = 'col-resize'
  document.body.style.userSelect = 'none'
}

// 处理拖拽
const handleResize = (e) => {
  if (!isResizing.value) return
  const newWidth = e.clientX
  if (newWidth >= 150 && newWidth <= 400) {
    width.value = newWidth
  }
}

// 停止拖拽
const stopResize = () => {
  isResizing.value = false
  document.removeEventListener('mousemove', handleResize)
  document.removeEventListener('mouseup', stopResize)
  document.body.style.cursor = ''
  document.body.style.userSelect = ''
}
</script>

<style scoped lang="scss">
.aside {
  background: #1b6eb3;
  overflow-x: hidden;
  overflow-y: auto;
  position: relative;
  
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
        margin-right: 2px;
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
    
    // 多级菜单缩进样式
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
    
    :deep(.el-sub-menu .el-sub-menu .el-menu-item),
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu > .el-sub-menu__title) {
      height: 42px;
      line-height: 42px;
      padding-left: 32px !important;
    }

    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-menu-item),
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu > .el-sub-menu__title) {
      padding-left: 44px !important;
    }

    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-menu-item),
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu > .el-sub-menu__title) {
      padding-left: 56px !important;
    }

    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-menu-item),
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu > .el-sub-menu__title) {
      padding-left: 68px !important;
    }

    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-menu-item),
    :deep(.el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu .el-sub-menu > .el-sub-menu__title) {
      padding-left: 80px !important;
    }
  }
  
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
</style>
