<template>
  <el-form :model="modelValue" class="search-form" :label-width="maxLabelWidth + 'px'" @submit.prevent="handleSearch">
    <el-row :gutter="20">
      <el-col v-for="(item, index) in visibleItems" :key="index" :span="colSpan">
        <component :is="item" />
      </el-col>
      <el-col :span="btnSpan" class="search-btns">
        <el-form-item>
          <el-button v-if="showExpand" link type="primary" @click="expanded = !expanded">
            {{ expanded ? '收起' : '展开' }}
            <el-icon><component :is="expanded ? 'ArrowUp' : 'ArrowDown'" /></el-icon>
          </el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script setup>
import { ref, computed, useSlots } from 'vue'
import { ArrowUp, ArrowDown } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: { type: Object, default: () => ({}) },
  columns: { type: Number, default: 4 }
})

const emit = defineEmits(['search', 'reset', 'update:modelValue'])

const slots = useSlots()
const expanded = ref(false)
const colSpan = computed(() => 24 / props.columns)

const slotItems = computed(() => {
  const defaultSlot = slots.default?.() || []
  return defaultSlot.filter(vnode => {
    if (!vnode.type) return false
    if (typeof vnode.type === 'symbol') return false
    return true
  })
})

// 计算所有字段中最长标签的宽度
const maxLabelWidth = computed(() => {
  let maxWidth = 0
  slotItems.value.forEach(vnode => {
    const label = vnode.props?.label || ''
    // 每个汉字约14px，英文约8px，加上冒号和padding约20px
    const labelWidth = [...label].reduce((w, char) => w + (/[\u4e00-\u9fa5]/.test(char) ? 14 : 8), 0) + 20
    maxWidth = Math.max(maxWidth, labelWidth)
  })
  return maxWidth
})

// 一行最多显示的字段数（留一格给按钮）
const maxFirstRow = computed(() => props.columns - 1)

// 是否需要显示展开/收起按钮
const showExpand = computed(() => slotItems.value.length > maxFirstRow.value)

// 当前显示的字段
const visibleItems = computed(() => {
  if (!showExpand.value || expanded.value) {
    return slotItems.value
  }
  return slotItems.value.slice(0, maxFirstRow.value)
})

// 计算按钮组占据的span
const btnSpan = computed(() => {
  const itemCount = visibleItems.value.length
  const remainder = itemCount % props.columns
  return remainder === 0 ? 24 : (props.columns - remainder) * colSpan.value
})

const handleSearch = () => {
  emit('search')
}

const handleReset = () => {
  // 直接修改原对象的属性，而不是替换整个对象
  Object.keys(props.modelValue).forEach(key => {
    if (key === 'pageNum') props.modelValue[key] = 1
    else if (key === 'pageSize') { /* 保持不变 */ }
    else props.modelValue[key] = ''
  })
  emit('reset')
}
</script>

<style scoped>
.search-form { overflow: hidden; }
.search-form :deep(.el-row) { width: calc(100% + 20px); margin-right: -10px !important; flex-wrap: wrap; row-gap: 12px; }
.search-form :deep(.el-form-item) { margin-bottom: 0; margin-right: 0; width: 100%; }
.search-form :deep(.el-form-item__label) { flex-shrink: 0; white-space: nowrap; }
.search-form :deep(.el-form-item__content) { flex: 1; min-width: 0; }
.search-form :deep(.el-input), 
.search-form :deep(.el-select), 
.search-form :deep(.el-tree-select) { width: 100%; }
.search-btns { display: flex; justify-content: flex-end; align-items: flex-start; padding-right: 10px; }
.search-btns :deep(.el-form-item) { width: auto; }
.search-btns :deep(.el-form-item__content) { width: auto; gap: 8px; }
</style>
