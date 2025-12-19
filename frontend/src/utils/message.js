/**
 * 通用对话框工具函数
 */
import { ElMessageBox, ElMessage } from 'element-plus'

/**
 * 确认对话框
 * @param {string} message - 确认消息
 * @param {string} title - 标题
 * @param {Object} options - 额外配置
 * @returns {Promise<boolean>}
 */
export async function confirm(message, title = '提示', options = {}) {
    try {
        await ElMessageBox.confirm(message, title, {
            type: 'warning',
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            ...options
        })
        return true
    } catch {
        return false
    }
}

/**
 * 确认删除对话框
 * @param {string} entityName - 实体名称，如 "该用户"
 * @returns {Promise<boolean>}
 */
export async function confirmDelete(entityName = '该数据') {
    return confirm(`确认删除${entityName}？删除后将无法恢复！`, '删除确认', {
        type: 'warning',
        confirmButtonText: '确定删除',
        confirmButtonClass: 'el-button--danger'
    })
}

/**
 * 成功提示
 * @param {string} message - 消息内容
 */
export function success(message = '操作成功') {
    ElMessage.success(message)
}

/**
 * 错误提示
 * @param {string} message - 消息内容
 */
export function error(message = '操作失败') {
    ElMessage.error(message)
}

/**
 * 警告提示
 * @param {string} message - 消息内容
 */
export function warning(message) {
    ElMessage.warning(message)
}

/**
 * 信息提示
 * @param {string} message - 消息内容
 */
export function info(message) {
    ElMessage.info(message)
}

export default {
    confirm,
    confirmDelete,
    success,
    error,
    warning,
    info
}
