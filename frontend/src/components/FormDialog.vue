<template>
  <el-dialog v-model="visible" :width="width" :close-on-click-modal="false" @close="handleClose" class="custom-dialog">
    <template #header>
      <div class="dialog-title">
        <i class="title-bar"></i>
        <span>{{ title }}</span>
      </div>
    </template>
    <el-form ref="formRef" :model="modelValue" :rules="rules" :label-width="labelWidth" label-position="right">
      <slot></slot>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: { type: Object, default: () => ({}) },
  show: { type: Boolean, default: false },
  title: { type: String, default: '' },
  width: { type: String, default: '500px' },
  labelWidth: { type: String, default: '100px' },
  rules: { type: Object, default: () => ({}) },
  loading: { type: Boolean, default: false }
})

const emit = defineEmits(['update:show', 'submit', 'close'])

const formRef = ref()

const visible = computed({
  get: () => props.show,
  set: (val) => emit('update:show', val)
})

const handleClose = () => {
  formRef.value?.resetFields()
  emit('update:show', false)
  emit('close')
}

const handleSubmit = async () => {
  await formRef.value.validate()
  emit('submit')
}

defineExpose({ formRef })
</script>

<style scoped>
.dialog-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #000;
}

.title-bar {
  width: 4px;
  height: 18px;
  background: #0b7ef0ff;
  margin-right: 8px;
  border-radius: 2px;
}

:deep(.el-dialog) {
  border-radius: 8px;
}

:deep(.el-dialog__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
  margin-right: 0;
}

:deep(.el-dialog__body) {
  padding: 24px 20px;
}

:deep(.el-dialog__footer) {
  padding: 12px 20px;
  border-top: 1px solid #eee;
}

:deep(.el-form-item) {
  margin-bottom: 18px;
}

:deep(.el-form-item__label) {
  color: #000;
  font-size: 14px;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-textarea__inner) {
  border-radius: 4px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover),
:deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focus),
:deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px #2b5a9e inset;
}

:deep(.el-button--primary) {
  background-color: #2b5a9e;
  border-color: #2b5a9e;
  border-radius: 6px;
}

:deep(.el-button--primary:hover),
:deep(.el-button--primary:focus) {
  background-color: #3a6db5;
  border-color: #3a6db5;
}

:deep(.el-button--default) {
  border-radius: 6px;
}
</style>
