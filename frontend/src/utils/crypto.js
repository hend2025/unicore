/**
 * 加密工具函数
 */

/**
 * Base64编码
 * @param {string} str - 要编码的字符串
 * @returns {string} Base64编码后的字符串
 */
export function encodeBase64(str) {
  return btoa(unescape(encodeURIComponent(str)))
}

/**
 * Base64解码
 * @param {string} str - Base64编码的字符串
 * @returns {string} 解码后的字符串
 */
export function decodeBase64(str) {
  return decodeURIComponent(escape(atob(str)))
}
