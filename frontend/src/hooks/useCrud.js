import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { PAGINATION } from '@/constants'

/**
 * 通用 CRUD Hook（增强版）
 * @param {Object} api - 包含 page, get, add, update, delete 方法的 API 对象
 * @param {Object} options - 配置项
 * @param {Object} options.defaultQuery - 默认查询参数（不含分页）
 * @param {Object} options.defaultForm - 表单默认值
 * @param {String} options.rowKey - 主键字段名，默认 'id'
 * @param {String} options.title - 实体名称，用于弹窗标题和提示，如 '岗位'
 * @param {Boolean} options.immediate - 是否立即加载数据，默认 true
 * @param {Function} options.beforeSubmit - 提交前的数据处理函数
 * @param {Function} options.afterSubmit - 提交成功后的回调
 * @param {Function} options.beforeDelete - 删除前的校验函数
 */
export function useCrud(api, options = {}) {
  const {
    defaultQuery = {},
    defaultForm = {},
    rowKey = 'id',
    title = '数据',
    immediate = true,
    beforeSubmit,
    afterSubmit,
    beforeDelete
  } = options

  // 状态
  const loading = ref(false)
  const submitLoading = ref(false)
  const tableData = ref([])
  const total = ref(0)
  const dialogVisible = ref(false)
  const dialogTitle = ref('')
  const isEdit = ref(false)
  const currentRow = ref(null)

  // 查询参数（带分页）
  const queryParams = reactive({
    pageNum: PAGINATION.DEFAULT_PAGE_NUM,
    pageSize: PAGINATION.DEFAULT_PAGE_SIZE,
    ...defaultQuery
  })

  // 排序参数
  const sortParams = reactive({
    orderBy: '',
    orderType: ''
  })

  // 加载列表数据
  const loadData = async () => {
    loading.value = true
    try {
      const params = { ...queryParams, ...sortParams }
      const res = await api.page(params)
      tableData.value = res.data?.records || res.data?.list || []
      total.value = res.data?.total || 0
    } finally {
      loading.value = false
    }
  }

  // 搜索（重置到第一页）
  const handleSearch = () => {
    queryParams.pageNum = PAGINATION.DEFAULT_PAGE_NUM
    loadData()
  }

  // 重置查询
  const handleReset = () => {
    loadData()
  }

  // 排序变化
  const handleSortChange = ({ prop, order }) => {
    sortParams.orderBy = prop || ''
    sortParams.orderType = order === 'ascending' ? 'asc' : order === 'descending' ? 'desc' : ''
    loadData()
  }

  // 打开新增弹窗
  const handleAdd = (form) => {
    isEdit.value = false
    currentRow.value = null
    Object.assign(form, { [rowKey]: null, ...defaultForm })
    dialogTitle.value = `新增${title}`
    dialogVisible.value = true
  }

  // 打开编辑弹窗
  const handleEdit = async (row, form) => {
    isEdit.value = true
    currentRow.value = row
    const res = await api.get(row[rowKey])
    Object.assign(form, res.data)
    dialogTitle.value = `编辑${title}`
    dialogVisible.value = true
  }

  // 打开查看弹窗（只读模式）
  const handleView = async (row, form) => {
    isEdit.value = false
    currentRow.value = row
    const res = await api.get(row[rowKey])
    Object.assign(form, res.data)
    dialogTitle.value = `查看${title}`
    dialogVisible.value = true
  }

  // 提交表单
  const handleSubmit = async (form, callback) => {
    submitLoading.value = true
    try {
      // 提交前处理
      const submitData = beforeSubmit ? await beforeSubmit(form) : form

      if (submitData[rowKey]) {
        await api.update(submitData)
      } else {
        await api.add(submitData)
      }
      ElMessage.success('操作成功')
      dialogVisible.value = false
      loadData()

      // 提交后回调
      afterSubmit?.()
      callback?.()
    } finally {
      submitLoading.value = false
    }
  }

  // 删除
  const handleDelete = (row, callback) => {
    // 删除前校验
    if (beforeDelete && !beforeDelete(row)) {
      return
    }

    ElMessageBox.confirm(`确认删除该${title}?`, '提示', { type: 'warning' })
      .then(async () => {
        await api.delete(row[rowKey])
        ElMessage.success('删除成功')
        loadData()
        callback?.()
      })
      .catch(() => { })
  }

  // 批量删除
  const handleBatchDelete = async (selectedRows, callback) => {
    if (!selectedRows || selectedRows.length === 0) {
      ElMessage.warning('请选择要删除的数据')
      return
    }

    try {
      await ElMessageBox.confirm(`确认删除选中的 ${selectedRows.length} 条${title}?`, '提示', { type: 'warning' })

      const ids = selectedRows.map(row => row[rowKey])
      if (api.deleteBatch) {
        await api.deleteBatch(ids)
      } else {
        // 如果没有批量删除接口，则逐个删除
        await Promise.all(ids.map(id => api.delete(id)))
      }

      ElMessage.success('删除成功')
      loadData()
      callback?.()
    } catch {
      // 用户取消
    }
  }

  // 导出数据
  const handleExport = async () => {
    if (!api.export) {
      ElMessage.warning('暂不支持导出功能')
      return
    }

    try {
      loading.value = true
      await api.export(queryParams)
      ElMessage.success('导出成功')
    } finally {
      loading.value = false
    }
  }

  // 刷新当前页
  const refresh = () => {
    loadData()
  }

  // 自动加载
  if (immediate) {
    onMounted(() => loadData())
  }

  return {
    // 状态
    loading,
    submitLoading,
    tableData,
    total,
    dialogVisible,
    dialogTitle,
    queryParams,
    sortParams,
    isEdit,
    currentRow,
    // 方法
    loadData,
    handleSearch,
    handleReset,
    handleSortChange,
    handleAdd,
    handleEdit,
    handleView,
    handleSubmit,
    handleDelete,
    handleBatchDelete,
    handleExport,
    refresh
  }
}
