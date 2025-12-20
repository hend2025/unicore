<template>
  <!-- 菜单搜索对话框 -->
  <el-dialog v-model="dialogVisible" title="菜单搜索" width="600px" :close-on-click-modal="true">
    <!-- 搜索输入框 -->
    <el-input 
      v-model="keyword" 
      placeholder="请输入菜单名称" 
      prefix-icon="Search" 
      clearable 
      @input="handleSearch" 
    />
    <!-- 搜索结果列表 -->
    <div class="menu-search-results">
      <div v-if="searchResults.length === 0" class="no-result">暂无搜索结果</div>
      <div v-else class="result-list">
        <div 
          v-for="item in searchResults" 
          :key="item.path" 
          class="result-item" 
          @click="handleResultClick(item)"
        >
          <el-icon><Document /></el-icon>
          <span>{{ item.title }}</span>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  menus: { type: Array, default: () => [] }
})

const emit = defineEmits(['update:visible', 'select'])

const keyword = ref('')
const searchResults = ref([])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 重置搜索状态
watch(dialogVisible, (val) => {
  if (!val) {
    keyword.value = ''
    searchResults.value = []
  }
})

/**
 * 递归搜索菜单
 */
const handleSearch = () => {
  if (!keyword.value.trim()) {
    searchResults.value = []
    return
  }
  
  const kw = keyword.value.toLowerCase()
  const results = []
  
  const searchInMenus = (menuList, parentPath = '') => {
    menuList.forEach(menu => {
      const currentPath = parentPath ? `${parentPath} / ${menu.menuName}` : menu.menuName
      
      if (menu.menuName.toLowerCase().includes(kw)) {
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
  
  searchInMenus(props.menus)
  searchResults.value = results
}

/**
 * 点击搜索结果
 */
const handleResultClick = (item) => {
  dialogVisible.value = false
  emit('select', item.path)
}
</script>

<style scoped lang="scss">
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
