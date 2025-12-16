<template>
  <el-form :model="modelValue" :inline="true" class="search-form" @submit.prevent="handleSearch">
    <slot></slot>
    <el-form-item>
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>查询
      </el-button>
      <el-button @click="handleReset">
        <el-icon><Refresh /></el-icon>重置
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
const props = defineProps({
  modelValue: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['search', 'reset', 'update:modelValue'])

const handleSearch = () => {
  emit('search')
}

const handleReset = () => {
  const resetObj = {}
  Object.keys(props.modelValue).forEach(key => {
    resetObj[key] = ''
  })
  emit('update:modelValue', resetObj)
  emit('reset')
}
</script>
