/**
 * 应用常量定义
 * 集中管理魔法字符串，提高代码可维护性
 */

// ==================== 状态常量 ====================
export const STATUS = {
    NORMAL: '1',
    DISABLED: '0'
}

export const STATUS_MAP = {
    [STATUS.NORMAL]: { label: '正常', type: 'success' },
    [STATUS.DISABLED]: { label: '停用', type: 'danger' }
}

// ==================== 路由常量 ====================
export const ROUTES = {
    LOGIN: '/login',
    HOME: '/home'
}

// ==================== 存储 Key ====================
export const STORAGE_KEYS = {
    TOKEN: 'access_token',
    ACTIVE_SYSTEM: 'activeSystem'
}

// ==================== 分页默认值 ====================
export const PAGINATION = {
    DEFAULT_PAGE_NUM: 1,
    DEFAULT_PAGE_SIZE: 10,
    PAGE_SIZES: [10, 20, 50, 100]
}

// ==================== API 响应码 ====================
export const RESPONSE_CODE = {
    SUCCESS: 0,
    UNAUTHORIZED: 401,
    FORBIDDEN: 403,
    NOT_IN_WHITELIST: -4
}

// ==================== 菜单类型 ====================
export const MENU_TYPE = {
    DIRECTORY: 'M',  // 目录
    MENU: 'C',       // 菜单
    BUTTON: 'F'      // 按钮
}
