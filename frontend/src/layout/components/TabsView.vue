<template>
  <div class="tabs-view-wrapper">
    <!-- 多标签页组件 -->
    <el-tabs 
      :model-value="activeTab" 
      type="card" 
      @tab-remove="closeTab" 
      @tab-change="handleTabChange"
    >
      <el-tab-pane 
        v-for="view in visitedViews" 
        :key="view.path" 
        :label="view.title" 
        :name="view.path" 
        :closable="view.path !== '/home'"  
        lazy
      >
        <!-- 外部系统用 iframe 嵌入 -->
        <iframe 
          v-if="view.isIframe" 
          :src="iframeCache[view.path]?.url" 
          class="iframe-content" 
          frameborder="0"
        />
        <!-- 内部页面用动态组件 -->
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
</template>

<script setup>
defineProps({
  activeTab: { type: String, default: '/home' },
  visitedViews: { type: Array, default: () => [] },
  iframeCache: { type: Object, default: () => ({}) },
  componentMap: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['update:activeTab', 'tab-change', 'close-tab', 'tabs-command'])

const handleTabChange = (tabPath) => {
  emit('update:activeTab', tabPath)
  emit('tab-change', tabPath)
}

const closeTab = (targetPath) => {
  emit('close-tab', targetPath)
}

const handleTabsCommand = (command) => {
  emit('tabs-command', command)
}
</script>

<style scoped lang="scss">
.tabs-view-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
  
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
  
  :deep(.el-tabs) {
    height: 100%;
    display: flex;
    flex-direction: column;
    
    .el-tabs__header {
      margin: 0;
      background: #f5f5f5;
      border-bottom: 1px solid #e8e8e8;
      padding: 0 48px 0 8px;
      
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
        position: relative;
        
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
        
        &:last-child::after {
          display: none;
        }
        
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
</style>
