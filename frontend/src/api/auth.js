import request from '@/utils/request'

/**
 * Base64编码工具函数
 * @param {string} str - 要编码的字符串
 * @returns {string} Base64编码后的字符串
 */
function encodeBase64(str) {
  return btoa(unescape(encodeURIComponent(str)))
}

/**
 * 登录数据
 * @typedef {Object} LoginData
 * @property {string} username - 用户名
 * @property {string} password - 密码
 * @property {string} captchaKey - 验证码key
 * @property {string} captchaCode - 验证码
 */

/**
 * 登录接口
 */
export const AuthAPI = {
  login(data) {
    const formData = new FormData()
    // 用户名和密码Base64编码后传输
    formData.append('username', encodeBase64(data.username))
    formData.append('password', encodeBase64(data.password))
    formData.append('captchaKey', data.captchaKey)
    formData.append('captchaCode', data.captchaCode)
    return request({
      url: '/login',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

export function logout() {
  return request({
    url: '/logout',
    method: 'get'
  })
}

export function getInfo() {
  return request({
    url: '/api/auth/info',
    method: 'get'
  })
}

export function getMenus(sysId) {
  return request({
    url: '/api/auth/menus',
    method: 'get',
    params: { sysId }
  })
}

export function changePassword(data) {
  return request({
    url: '/api/auth/password',
    method: 'put',
    data
  })
}

export function getSystems() {
  return request({
    url: '/api/auth/systems',
    method: 'get'
  })
}
