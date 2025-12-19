<template>
  <el-tag :type="statusType">{{ statusLabel }}</el-tag>
</template>

<script setup>
import { computed } from 'vue'
import { STATUS, STATUS_MAP } from '@/constants'

const props = defineProps({
  status: {
    type: [String, Number],
    default: STATUS.NORMAL
  }
})

const statusLabel = computed(() => {
  const key = String(props.status)
  return STATUS_MAP[key]?.label || '未知'
})

const statusType = computed(() => {
  const key = String(props.status)
  return STATUS_MAP[key]?.type || 'info'
})
</script>
