import request from '@/utils/request'

/**
 * Base64编码工具函数
 * @param {string} str - 要编码的字符串
 * @returns {string} Base64编码后的字符串
 */
function encodeBase64(str) {
  return btoa(unescape(encodeURIComponent(str)))
}

// 用户管理
export const userApi = {
  page: (params) => request({ url: '/api/sys/user/page', method: 'get', params }),
  get: (id) => request({ url: `/api/sys/user/${id}`, method: 'get' }),
  add: (data) => {
    // 用户名和密码Base64编码后传输
    const encryptedData = {
      ...data,
      userName: encodeBase64(data.userName),
      password: data.password ? encodeBase64(data.password) : undefined
    }
    return request({ url: '/api/sys/user', method: 'post', data: encryptedData })
  },
  update: (data) => {
    // 用户名Base64编码后传输（更新时密码通常不传）
    const encryptedData = {
      ...data,
      userName: encodeBase64(data.userName),
      password: data.password ? encodeBase64(data.password) : undefined
    }
    return request({ url: '/api/sys/user/update', method: 'post', data: encryptedData })
  },
  delete: (id) => request({ url: `/api/sys/user/delete/${id}`, method: 'get' }),
  resetPwd: (data) => {
    // 密码Base64编码后传输
    const encryptedData = {
      ...data,
      password: data.password ? encodeBase64(data.password) : undefined
    }
    return request({ url: '/api/sys/user/resetPwd', method: 'post', data: encryptedData })
  }
}

// 角色管理
export const roleApi = {
  page: (params) => request({ url: '/api/sys/role/page', method: 'get', params }),
  list: () => request({ url: '/api/sys/role/list', method: 'get' }),
  get: (id) => request({ url: `/api/sys/role/${id}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/role', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/role/update', method: 'post', data }),
  delete: (id) => request({ url: `/api/sys/role/delete/${id}`, method: 'get' })
}

// 菜单管理
export const menuApi = {
  page: (params) => request({ url: '/api/sys/menu/page', method: 'get', params }),
  tree: (sysId) => request({ url: '/api/sys/menu/tree', method: 'get', params: { sysId } }),
  list: () => request({ url: '/api/sys/menu/list', method: 'get' }),
  get: (id) => request({ url: `/api/sys/menu/${id}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/menu', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/menu/update', method: 'post', data }),
  delete: (id) => request({ url: `/api/sys/menu/delete/${id}`, method: 'get' })
}

// 机构管理
export const orgApi = {
  page: (params) => request({ url: '/api/sys/org/page', method: 'get', params }),
  tree: () => request({ url: '/api/sys/org/tree', method: 'get' }),
  list: (params) => request({ url: '/api/sys/org/list', method: 'get', params }),
  get: (id) => request({ url: `/api/sys/org/${id}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/org', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/org/update', method: 'post', data }),
  delete: (id) => request({ url: `/api/sys/org/delete/${id}`, method: 'get' })
}

// 应用管理
export const systemApi = {
  page: (params) => request({ url: '/api/sys/system/page', method: 'get', params }),
  tree: () => request({ url: '/api/sys/system/tree', method: 'get' }),
  list: () => request({ url: '/api/sys/system/list', method: 'get' }),
  get: (id) => request({ url: `/api/sys/system/${id}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/system', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/system/update', method: 'post', data }),
  delete: (id) => request({ url: `/api/sys/system/delete/${id}`, method: 'get' })
}

// 字典管理
export const dictApi = {
  typePage: (params) => request({ url: '/api/sys/dict/type/page', method: 'get', params }),
  typeList: () => request({ url: '/api/sys/dict/type/list', method: 'get' }),
  addType: (data) => request({ url: '/api/sys/dict/type', method: 'post', data }),
  updateType: (data) => request({ url: '/api/sys/dict/type/update', method: 'post', data }),
  deleteType: (code) => request({ url: `/api/sys/dict/type/delete/${code}`, method: 'get' }),
  dataPage: (params) => request({ url: '/api/sys/dict/data/page', method: 'get', params }),
  dataList: (typeCode) => request({ url: `/api/sys/dict/data/list/${typeCode}`, method: 'get' }),
  addData: (data) => request({ url: '/api/sys/dict/data', method: 'post', data }),
  updateData: (data) => request({ url: '/api/sys/dict/data/update', method: 'post', data }),
  deleteData: (id) => request({ url: `/api/sys/dict/data/delete/${id}`, method: 'get' })
}

// 参数配置
export const configApi = {
  page: (params) => request({ url: '/api/sys/config/page', method: 'get', params }),
  get: (id) => request({ url: `/api/sys/config/${id}`, method: 'get' }),
  getByKey: (key) => request({ url: `/api/sys/config/key/${key}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/config', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/config/update', method: 'post', data }),
  delete: (id) => request({ url: `/api/sys/config/delete/${id}`, method: 'get' })
}

// 通知公告
export const noticeApi = {
  page: (params) => request({ url: '/api/sys/notice/page', method: 'get', params }),
  get: (id) => request({ url: `/api/sys/notice/${id}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/notice', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/notice/update', method: 'post', data }),
  delete: (id) => request({ url: `/api/sys/notice/delete/${id}`, method: 'get' })
}

// 日志
export const logApi = {
  operPage: (params) => request({ url: '/api/sys/log/page', method: 'get', params }),
  loginPage: (params) => request({ url: '/api/sys/log/login/page', method: 'get', params }),
  deleteOper: (id) => request({ url: `/api/sys/log/delete/${id}`, method: 'get' }),
  deleteLogin: (id) => request({ url: `/api/sys/log/login/delete/${id}`, method: 'get' }),
  clearOper: () => request({ url: '/api/sys/log/clear', method: 'get' }),
  clearOperDays: (days) => request({ url: '/api/sys/log/clear', method: 'get', params: { days } }),
  clearLogin: () => request({ url: '/api/sys/log/login/clear', method: 'get' }),
  clearLoginDays: (days) => request({ url: '/api/sys/log/login/clear', method: 'get', params: { days } })
}

// 岗位管理
export const postApi = {
  page: (params) => request({ url: '/api/sys/post/page', method: 'get', params }),
  list: () => request({ url: '/api/sys/post/list', method: 'get' }),
  get: (id) => request({ url: `/api/sys/post/${id}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/post', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/post/update', method: 'post', data }),
  delete: (id) => request({ url: `/api/sys/post/delete/${id}`, method: 'get' })
}

// 业务参数
export const paraApi = {
  page: (params) => request({ url: '/api/sys/para/page', method: 'get', params }),
  get: (id) => request({ url: `/api/sys/para/${id}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/para', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/para/update', method: 'post', data }),
  delete: (id) => request({ url: `/api/sys/para/delete/${id}`, method: 'get' })
}

// 地区管理
export const areaApi = {
  page: (params) => request({ url: '/api/sys/area/page', method: 'get', params }),
  tree: () => request({ url: '/api/sys/area/tree', method: 'get' }),
  get: (code) => request({ url: `/api/sys/area/${code}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/area', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/area/update', method: 'post', data }),
  delete: (code) => request({ url: `/api/sys/area/delete/${code}`, method: 'get' })
}

// 医保区划
export const admdvsApi = {
  page: (params) => request({ url: '/api/sys/admdvs/page', method: 'get', params }),
  tree: () => request({ url: '/api/sys/admdvs/tree', method: 'get' }),
  get: (code) => request({ url: `/api/sys/admdvs/${code}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/admdvs', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/admdvs/update', method: 'post', data }),
  delete: (code) => request({ url: `/api/sys/admdvs/delete/${code}`, method: 'get' })
}

// API日志
export const apiLogApi = {
  page: (params) => request({ url: '/api/sys/apilog/page', method: 'get', params }),
  get: (id) => request({ url: `/api/sys/apilog/${id}`, method: 'get' }),
  delete: (id) => request({ url: `/api/sys/apilog/delete/${id}`, method: 'get' }),
  deleteBatch: (ids) => request({ url: '/api/sys/apilog/batch/delete', method: 'post', data: ids }),
  clear: (days) => request({ url: '/api/sys/apilog/clear', method: 'get', params: { days } })
}
