/**
 * 权限指令
 * 根据用户权限控制元素的显示/隐藏
 * 
 * 使用方式：
 * v-permission="'system:user:add'"      // 单个权限
 * v-permission="['system:user:add', 'system:user:edit']"  // 多个权限（满足其一）
 */
import { useUserStore } from '@/stores/user'

/**
 * 检查是否有权限
 * @param {string|string[]} value - 权限标识
 * @returns {boolean}
 */
function hasPermission(value) {
  const userStore = useUserStore()
  const permissions = userStore.permissions || []
  
  // 超级管理员拥有所有权限
  if (permissions.includes('*:*:*')) {
    return true
  }
  
  if (typeof value === 'string') {
    return permissions.includes(value)
  }
  
  if (Array.isArray(value)) {
    return value.some(perm => permissions.includes(perm))
  }
  
  return false
}

export const permission = {
  mounted(el, binding) {
    const { value } = binding
    
    if (value && !hasPermission(value)) {
      // 没有权限时移除元素
      el.parentNode?.removeChild(el)
    }
  }
}

/**
 * 角色指令
 * 根据用户角色控制元素的显示/隐藏
 * 
 * 使用方式：
 * v-role="'admin'"
 * v-role="['admin', 'editor']"
 */
function hasRole(value) {
  const userStore = useUserStore()
  const roles = userStore.userInfo?.roles || []
  
  if (typeof value === 'string') {
    return roles.some(role => role.roleKey === value)
  }
  
  if (Array.isArray(value)) {
    return value.some(roleKey => roles.some(role => role.roleKey === roleKey))
  }
  
  return false
}

export const role = {
  mounted(el, binding) {
    const { value } = binding
    
    if (value && !hasRole(value)) {
      el.parentNode?.removeChild(el)
    }
  }
}

/**
 * 注册所有指令
 * @param {App} app - Vue 应用实例
 */
export function setupDirectives(app) {
  app.directive('permission', permission)
  app.directive('role', role)
}

export default {
  install: setupDirectives
}
