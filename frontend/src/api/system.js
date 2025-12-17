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
    return request({ url: '/api/sys/user', method: 'put', data: encryptedData })
  },
  delete: (id) => request({ url: `/api/sys/user/${id}`, method: 'delete' }),
  resetPwd: (data) => {
    // 密码Base64编码后传输
    const encryptedData = {
      ...data,
      password: data.password ? encodeBase64(data.password) : undefined
    }
    return request({ url: '/api/sys/user/resetPwd', method: 'put', data: encryptedData })
  }
}

// 角色管理
export const roleApi = {
  page: (params) => request({ url: '/api/sys/role/page', method: 'get', params }),
  list: () => request({ url: '/api/sys/role/list', method: 'get' }),
  get: (id) => request({ url: `/api/sys/role/${id}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/role', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/role', method: 'put', data }),
  delete: (id) => request({ url: `/api/sys/role/${id}`, method: 'delete' })
}

// 菜单管理
export const menuApi = {
  page: (params) => request({ url: '/api/sys/menu/page', method: 'get', params }),
  tree: (sysId) => request({ url: '/api/sys/menu/tree', method: 'get', params: { sysId } }),
  list: () => request({ url: '/api/sys/menu/list', method: 'get' }),
  get: (id) => request({ url: `/api/sys/menu/${id}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/menu', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/menu', method: 'put', data }),
  delete: (id) => request({ url: `/api/sys/menu/${id}`, method: 'delete' })
}

// 机构管理
export const orgApi = {
  page: (params) => request({ url: '/api/sys/org/page', method: 'get', params }),
  tree: () => request({ url: '/api/sys/org/tree', method: 'get' }),
  list: (params) => request({ url: '/api/sys/org/list', method: 'get', params }),
  get: (id) => request({ url: `/api/sys/org/${id}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/org', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/org', method: 'put', data }),
  delete: (id) => request({ url: `/api/sys/org/${id}`, method: 'delete' })
}

// 应用管理
export const systemApi = {
  page: (params) => request({ url: '/api/sys/system/page', method: 'get', params }),
  tree: () => request({ url: '/api/sys/system/tree', method: 'get' }),
  list: () => request({ url: '/api/sys/system/list', method: 'get' }),
  get: (id) => request({ url: `/api/sys/system/${id}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/system', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/system', method: 'put', data }),
  delete: (id) => request({ url: `/api/sys/system/${id}`, method: 'delete' })
}

// 字典管理
export const dictApi = {
  typePage: (params) => request({ url: '/api/sys/dict/type/page', method: 'get', params }),
  typeList: () => request({ url: '/api/sys/dict/type/list', method: 'get' }),
  addType: (data) => request({ url: '/api/sys/dict/type', method: 'post', data }),
  updateType: (data) => request({ url: '/api/sys/dict/type', method: 'put', data }),
  deleteType: (code) => request({ url: `/api/sys/dict/type/${code}`, method: 'delete' }),
  dataPage: (params) => request({ url: '/api/sys/dict/data/page', method: 'get', params }),
  dataList: (typeCode) => request({ url: `/api/sys/dict/data/list/${typeCode}`, method: 'get' }),
  addData: (data) => request({ url: '/api/sys/dict/data', method: 'post', data }),
  updateData: (data) => request({ url: '/api/sys/dict/data', method: 'put', data }),
  deleteData: (id) => request({ url: `/api/sys/dict/data/${id}`, method: 'delete' })
}

// 参数配置
export const configApi = {
  page: (params) => request({ url: '/api/sys/config/page', method: 'get', params }),
  get: (id) => request({ url: `/api/sys/config/${id}`, method: 'get' }),
  getByKey: (key) => request({ url: `/api/sys/config/key/${key}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/config', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/config', method: 'put', data }),
  delete: (id) => request({ url: `/api/sys/config/${id}`, method: 'delete' })
}

// 通知公告
export const noticeApi = {
  page: (params) => request({ url: '/api/sys/notice/page', method: 'get', params }),
  get: (id) => request({ url: `/api/sys/notice/${id}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/notice', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/notice', method: 'put', data }),
  delete: (id) => request({ url: `/api/sys/notice/${id}`, method: 'delete' })
}

// 日志
export const logApi = {
  operPage: (params) => request({ url: '/api/sys/log/page', method: 'get', params }),
  loginPage: (params) => request({ url: '/api/sys/log/login/page', method: 'get', params }),
  deleteOper: (id) => request({ url: `/api/sys/log/${id}`, method: 'delete' }),
  deleteLogin: (id) => request({ url: `/api/sys/log/login/${id}`, method: 'delete' }),
  clearOper: () => request({ url: '/api/sys/log/clear', method: 'delete' }),
  clearOperDays: (days) => request({ url: '/api/sys/log/clear', method: 'delete', params: { days } }),
  clearLogin: () => request({ url: '/api/sys/log/login/clear', method: 'delete' }),
  clearLoginDays: (days) => request({ url: '/api/sys/log/login/clear', method: 'delete', params: { days } })
}

// 岗位管理
export const postApi = {
  page: (params) => request({ url: '/api/sys/post/page', method: 'get', params }),
  list: () => request({ url: '/api/sys/post/list', method: 'get' }),
  get: (id) => request({ url: `/api/sys/post/${id}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/post', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/post', method: 'put', data }),
  delete: (id) => request({ url: `/api/sys/post/${id}`, method: 'delete' })
}

// 业务参数
export const paraApi = {
  page: (params) => request({ url: '/api/sys/para/page', method: 'get', params }),
  get: (id) => request({ url: `/api/sys/para/${id}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/para', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/para', method: 'put', data }),
  delete: (id) => request({ url: `/api/sys/para/${id}`, method: 'delete' })
}

// 地区管理
export const areaApi = {
  page: (params) => request({ url: '/api/sys/area/page', method: 'get', params }),
  tree: () => request({ url: '/api/sys/area/tree', method: 'get' }),
  get: (code) => request({ url: `/api/sys/area/${code}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/area', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/area', method: 'put', data }),
  delete: (code) => request({ url: `/api/sys/area/${code}`, method: 'delete' })
}

// 医保区划
export const admdvsApi = {
  page: (params) => request({ url: '/api/sys/admdvs/page', method: 'get', params }),
  tree: () => request({ url: '/api/sys/admdvs/tree', method: 'get' }),
  get: (code) => request({ url: `/api/sys/admdvs/${code}`, method: 'get' }),
  add: (data) => request({ url: '/api/sys/admdvs', method: 'post', data }),
  update: (data) => request({ url: '/api/sys/admdvs', method: 'put', data }),
  delete: (code) => request({ url: `/api/sys/admdvs/${code}`, method: 'delete' })
}

// API日志
export const apiLogApi = {
  page: (params) => request({ url: '/api/sys/apilog/page', method: 'get', params }),
  get: (id) => request({ url: `/api/sys/apilog/${id}`, method: 'get' }),
  delete: (id) => request({ url: `/api/sys/apilog/${id}`, method: 'delete' }),
  deleteBatch: (ids) => request({ url: '/api/sys/apilog/batch', method: 'delete', data: ids }),
  clear: (days) => request({ url: '/api/sys/apilog/clear', method: 'delete', params: { days } })
}
