/**
 * 加载状态管理 Hook
 * 用于管理异步操作的加载状态
 */
import { ref } from 'vue'

/**
 * 创建加载状态
 * @param {boolean} initialValue - 初始值
 * @returns {Object} - 加载状态和控制方法
 */
export function useLoading(initialValue = false) {
    const loading = ref(initialValue)

    const start = () => {
        loading.value = true
    }

    const stop = () => {
        loading.value = false
    }

    /**
     * 包装异步函数，自动管理加载状态
     * @param {Function} fn - 异步函数
     * @returns {Function} - 包装后的函数
     */
    const wrap = (fn) => {
        return async (...args) => {
            try {
                start()
                return await fn(...args)
            } finally {
                stop()
            }
        }
    }

    return {
        loading,
        start,
        stop,
        wrap
    }
}

/**
 * 多加载状态管理
 * @param {string[]} keys - 状态名称数组
 * @returns {Object} - 加载状态对象和控制方法
 */
export function useMultiLoading(keys = []) {
    const loadingMap = ref({})

    // 初始化
    keys.forEach(key => {
        loadingMap.value[key] = false
    })

    const isLoading = (key) => {
        return loadingMap.value[key] || false
    }

    const start = (key) => {
        loadingMap.value[key] = true
    }

    const stop = (key) => {
        loadingMap.value[key] = false
    }

    const wrap = (key, fn) => {
        return async (...args) => {
            try {
                start(key)
                return await fn(...args)
            } finally {
                stop(key)
            }
        }
    }

    // 检查是否有任何加载中
    const isAnyLoading = () => {
        return Object.values(loadingMap.value).some(v => v)
    }

    return {
        loadingMap,
        isLoading,
        start,
        stop,
        wrap,
        isAnyLoading
    }
}

export default useLoading
