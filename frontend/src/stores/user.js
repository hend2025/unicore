import { defineStore } from 'pinia'
import { ref } from 'vue'
import { AuthAPI, logout, getInfo, getMenus } from '@/api/auth'
import { STORAGE_KEYS } from '@/constants'

const { TOKEN: TOKEN_KEY } = STORAGE_KEYS

function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const userInfo = ref({})
  const menus = ref([])
  const permissions = ref([])

  /**
   * 登录
   * @param {Object} loginData - { username, password, captchaKey, captchaCode }
   */
  const doLogin = (loginData) => {
    return new Promise((resolve, reject) => {
      AuthAPI.login(loginData)
        .then((res) => {
          // 兼容两种返回格式
          // 格式1: { code: 0, data: { tokenType, accessToken } }
          // 格式2: { code: 0, data: "1", message: "登录成功" } (token可能在cookie中)
          const data = res.data
          if (data && typeof data === 'object' && data.accessToken) {
            const { tokenType, accessToken } = data
            const fullToken = (tokenType || 'Bearer') + ' ' + accessToken
            token.value = fullToken
            setToken(fullToken)
          } else if (data && typeof data === 'object' && data.token) {
            // 格式3: { code: 0, data: { token } }
            token.value = data.token
            setToken(data.token)
          }
          // 如果没有token信息，可能是session/cookie方式认证，直接resolve
          resolve()
        })
        .catch((error) => {
          reject(error)
        })
    })
  }

  const doLogout = async () => {
    await logout()
    token.value = ''
    userInfo.value = {}
    menus.value = []
    permissions.value = []
    removeToken()
  }

  const getUserInfo = async () => {
    const res = await getInfo()
    if (res.code === 0) {
      userInfo.value = res.data
      permissions.value = res.data.permissions || []
    }
    return res
  }

  const getMenuList = async (sysId) => {
    const res = await getMenus(sysId)
    if (res.code === 0) {
      menus.value = res.data
    }
    return res
  }

  return {
    token,
    userInfo,
    menus,
    permissions,
    doLogin,
    doLogout,
    getUserInfo,
    getMenuList
  }
})
