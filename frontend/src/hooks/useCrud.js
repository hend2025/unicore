import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

/**
 * 通用 CRUD Hook
 * @param {Object} api - 包含 page, get, add, update, delete 方法的 API 对象
 * @param {Object} options - 配置项
 * @param {Object} options.defaultQuery - 默认查询参数（不含分页）
 * @param {Object} options.defaultForm - 表单默认值
 * @param {String} options.rowKey - 主键字段名，默认 'id'
 * @param {String} options.title - 实体名称，用于弹窗标题和提示，如 '岗位'
 * @param {Boolean} options.immediate - 是否立即加载数据，默认 true
 */
export function useCrud(api, options = {}) {
  const {
    defaultQuery = {},
    defaultForm = {},
    rowKey = 'id',
    title = '数据',
    immediate = true
  } = options

  // 状态
  const loading = ref(false)
  const submitLoading = ref(false)
  const tableData = ref([])
  const total = ref(0)
  const dialogVisible = ref(false)
  const dialogTitle = ref('')

  // 查询参数（带分页）
  const queryParams = reactive({ pageNum: 1, pageSize: 10, ...defaultQuery })

  // 加载列表数据
  const loadData = async () => {
    loading.value = true
    try {
      const res = await api.page(queryParams)
      tableData.value = res.data?.records || res.data?.list || []
      total.value = res.data?.total || 0
    } finally {
      loading.value = false
    }
  }

  // 重置查询
  const handleReset = () => {
    loadData()
  }

  // 打开新增弹窗
  const handleAdd = (form) => {
    Object.assign(form, { [rowKey]: null, ...defaultForm })
    dialogTitle.value = `新增${title}`
    dialogVisible.value = true
  }

  // 打开编辑弹窗
  const handleEdit = async (row, form) => {
    const res = await api.get(row[rowKey])
    Object.assign(form, res.data)
    dialogTitle.value = `编辑${title}`
    dialogVisible.value = true
  }

  // 提交表单
  const handleSubmit = async (form, callback) => {
    submitLoading.value = true
    try {
      if (form[rowKey]) {
        await api.update(form)
      } else {
        await api.add(form)
      }
      ElMessage.success('操作成功')
      dialogVisible.value = false
      loadData()
      callback?.()
    } finally {
      submitLoading.value = false
    }
  }

  // 删除
  const handleDelete = (row, callback) => {
    ElMessageBox.confirm(`确认删除该${title}?`, '提示', { type: 'warning' })
      .then(async () => {
        await api.delete(row[rowKey])
        ElMessage.success('删除成功')
        loadData()
        callback?.()
      })
      .catch(() => {})
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
    // 方法
    loadData,
    handleReset,
    handleAdd,
    handleEdit,
    handleSubmit,
    handleDelete
  }
}
