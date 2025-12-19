/**
 * 密码复杂度验证器
 * 规则：至少8个字符，必须包含数字、字母、特殊符号
 * @param {object} rule - 验证规则
 * @param {string} value - 密码值
 * @param {function} callback - 回调函数
 */
export const passwordValidator = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
    return
  }
  if (value.length < 8) {
    callback(new Error('密码长度至少8个字符'))
    return
  }
  
  let complexity = 0
  if (/[0-9]/.test(value)) complexity++
  if (/[a-zA-Z]/.test(value)) complexity++
  if (/[^0-9a-zA-Z]/.test(value)) complexity++
  
  if (complexity < 3) {
    callback(new Error('密码必须包含数字、字母、特殊符号'))
    return
  }
  callback()
}

/**
 * 创建确认密码验证器
 * @param {function} getPassword - 获取原密码的函数
 * @returns {function} 验证器函数
 */
export const createConfirmPasswordValidator = (getPassword) => {
  return (rule, value, callback) => {
    if (!value) {
      callback(new Error('请再次输入密码'))
      return
    }
    if (value !== getPassword()) {
      callback(new Error('两次输入的密码不一致'))
      return
    }
    callback()
  }
}

/**
 * 手机号验证器
 */
export const phoneValidator = (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
    return
  }
  callback()
}

/**
 * 邮箱验证器
 */
export const emailValidator = (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  if (!/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(value)) {
    callback(new Error('请输入正确的邮箱地址'))
    return
  }
  callback()
}
