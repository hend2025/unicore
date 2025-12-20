<template>
  <el-dialog v-model="dialogVisible" :width="width" :close-on-click-modal="false" @close="handleClose" class="custom-dialog">
    <template #header>
      <slot name="header">
        <div class="dialog-title">
          <i class="title-bar"></i>
          <span>{{ title }}</span>
        </div>
      </slot>
    </template>
    <el-form ref="formRef" :model="modelValue" :rules="rules" :label-width="labelWidth" label-position="right">
      <slot></slot>
    </el-form>
    <template #footer>
      <slot name="footer" :loading="loading" :close="handleClose" :submit="handleSubmit">
        <el-button @click="handleClose">{{ cancelText }}</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">{{ submitText }}</el-button>
      </slot>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, useSlots } from 'vue'

const props = defineProps({
  modelValue: { type: Object, default: () => ({}) },
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  width: { type: String, default: '500px' },
  labelWidth: { type: String, default: '100px' },
  rules: { type: Object, default: () => ({}) },
  loading: { type: Boolean, default: false },
  resetOnClose: { type: Boolean, default: true },
  cancelText: { type: String, default: '取消' },
  submitText: { type: String, default: '保存' }
})

const emit = defineEmits(['update:visible', 'submit', 'close'])
const slots = useSlots()

const formRef = ref()

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const handleClose = () => {
  if (props.resetOnClose) {
    formRef.value?.resetFields()
  }
  emit('update:visible', false)
  emit('close')
}

const handleSubmit = async () => {
  await formRef.value.validate()
  emit('submit')
}

// 手动验证表单
const validate = () => formRef.value?.validate()

// 手动重置表单
const resetFields = () => formRef.value?.resetFields()

// 清除验证
const clearValidate = (props) => formRef.value?.clearValidate(props)

defineExpose({ formRef, validate, resetFields, clearValidate })
</script>

<style scoped lang="scss">
@use "@/styles/variables.scss" as *;

.dialog-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: $text-primary;
}

.title-bar {
  width: 4px;
  height: 18px;
  background: $primary-color;
  margin-right: 8px;
  border-radius: $border-radius-sm;
}

:deep(.el-dialog) {
  border-radius: $border-radius-lg;
}

:deep(.el-dialog__header) {
  padding: 16px 20px;
  border-bottom: 1px solid $border-color;
  margin-right: 0;
}

:deep(.el-dialog__body) {
  padding: 24px 20px;
}

:deep(.el-dialog__footer) {
  padding: 12px 20px;
  border-top: 1px solid $border-color;
}

:deep(.el-form-item) {
  margin-bottom: 18px;
}

:deep(.el-form-item__label) {
  color: $text-primary;
  font-size: 14px;
  white-space: nowrap;
  flex-shrink: 0;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-textarea__inner) {
  border-radius: $border-radius-sm;
  box-shadow: 0 0 0 1px $border-dark inset;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover),
:deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px $text-placeholder inset;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focus),
:deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px $primary-color inset;
}

:deep(.el-button--primary) {
  background-color: $primary-color;
  border-color: $primary-color;
  border-radius: $border-radius-md;
}

:deep(.el-button--primary:hover),
:deep(.el-button--primary:focus) {
  background-color: $primary-hover;
  border-color: $primary-hover;
}

:deep(.el-button--default) {
  border-radius: $border-radius-md;
}
</style>
