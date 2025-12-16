<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <div class="query-card">
      <div class="section-title"><i></i>信息查询</div>
      <div class="query-section">
        <el-form :model="queryParams" inline class="query-form" label-width="80px" @submit.prevent>
          <el-form-item label="系统名称">
            <el-input v-model="queryParams.sysName" placeholder="请输入系统名称" clearable />
          </el-form-item>
          <el-form-item>
            <el-button @click="handleReset">重置</el-button>
            <el-button type="primary" @click="loadData">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 应用列表 -->
    <div class="table-card">
      <div class="table-header">
        <div class="section-title"><i></i>应用列表</div>
        <el-button type="primary" @click="handleAdd">新增应用</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%; flex: 1" height="100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="sysName" label="系统名称" min-width="150" />
        <el-table-column prop="sysAbbr" label="系统简称" min-width="100" />
        <el-table-column prop="sysUrl" label="系统URL" min-width="200" />
        <el-table-column prop="orderNum" label="排序" width="80" />
        <el-table-column prop="stasFlag" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.stasFlag === '1' ? 'success' : 'danger'">{{ row.stasFlag === '1' ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="128" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-footer">
        <span class="total-info">总共{{ total }}条 显示{{ showStart }}-{{ showEnd }}条</span>
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :page-sizes="[10, 20, 50, 100]" :total="total" background small layout="sizes, prev, pager, next, jumper" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>

    <!-- 对话框 -->
    <FormDialog v-model:show="dialogVisible" :title="dialogTitle" :rules="rules" :modelValue="form" :loading="submitLoading" @submit="handleSubmit">
      <el-form-item label="系统名称" prop="sysName">
        <el-input v-model="form.sysName" placeholder="请输入系统名称" />
      </el-form-item>
      <el-form-item label="系统简称" prop="sysAbbr">
        <el-input v-model="form.sysAbbr" placeholder="请输入系统简称" />
      </el-form-item>
      <el-form-item label="系统URL" prop="sysUrl">
        <el-input v-model="form.sysUrl" placeholder="请输入系统URL" />
      </el-form-item>
      <el-form-item label="首页URL" prop="homeUrl">
        <el-input v-model="form.homeUrl" placeholder="请输入首页URL" />
      </el-form-item>
      <el-form-item label="显示排序" prop="orderNum">
        <el-input-number v-model="form.orderNum" :min="0" />
      </el-form-item>
      <el-form-item label="状态" prop="stasFlag">
        <el-radio-group v-model="form.stasFlag">
          <el-radio label="1">正常</el-radio>
          <el-radio label="0">停用</el-radio>
        </el-radio-group>
      </el-form-item>
    </FormDialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import FormDialog from '@/components/FormDialog.vue'
import { systemApi } from '@/api/system'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const queryParams = reactive({ pageNum: 1, pageSize: 10, sysName: '' })
const form = reactive({ sysId: null, sysName: '', sysAbbr: '', sysUrl: '', homeUrl: '', orderNum: 0, stasFlag: '1', prntId: 0 })
const rules = {
  sysName: [{ required: true, message: '请输入系统名称', trigger: 'blur' }],
  sysAbbr: [{ required: true, message: '请输入系统简称', trigger: 'blur' }]
}

const showStart = computed(() => total.value ? (queryParams.pageNum - 1) * queryParams.pageSize + 1 : 0)
const showEnd = computed(() => Math.min(queryParams.pageNum * queryParams.pageSize, total.value))

const loadData = async () => {
  loading.value = true
  try {
    const res = await systemApi.page(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  queryParams.sysName = ''
  loadData()
}

const handleAdd = () => {
  Object.assign(form, { sysId: null, sysName: '', sysAbbr: '', sysUrl: '', homeUrl: '', orderNum: 0, stasFlag: '1', prntId: 0 })
  dialogTitle.value = '新增应用'
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  const res = await systemApi.get(row.sysId)
  Object.assign(form, res.data)
  dialogTitle.value = '编辑应用'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    if (form.sysId) {
      await systemApi.update(form)
    } else {
      await systemApi.add(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该应用?', '提示', { type: 'warning' }).then(async () => {
    await systemApi.delete(row.sysId)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; padding: 10px; background: #f5f5f5; height: 100%; box-sizing: border-box; overflow: hidden; }
.section-title { display: flex; align-items: center; font-size: 16x; color: #333; font-weight: 700; margin-bottom: 8px; }
.section-title i { width: 4px; height: 18px; background: #0b7ef0ff; margin-right: 8px; border-radius: 2px; }
.query-card { background: #fff; padding: 10px 12px; margin-bottom: 8px; border: 1px solid #eee; border-radius: 4px; }
.query-card .section-title { margin-bottom: 8px; }
.query-section { padding-bottom: 0; }
.query-form { display: flex; flex-wrap: wrap; align-items: center; }
.query-form :deep(.el-form-item) { margin-bottom: 8px; margin-right: 20px; flex: 0 0 calc(25% - 20px); }
.query-form :deep(.el-form-item__label) { padding-right: 8px; color: #000; font-size: 14px; flex-shrink: 0; }
.query-form :deep(.el-form-item__content) { flex: 1; }
.query-form :deep(.el-input), .query-form :deep(.el-select) { width: 100%; }
.query-btns { flex: 1 1 100% !important; display: flex; justify-content: flex-end; margin-right: 0 !important; padding-right: 0; }
.query-btns :deep(.el-form-item__content) { justify-content: flex-end; }
.table-card { flex: 1; background: #fff; padding: 10px 12px; border: 1px solid #eee; border-radius: 4px; display: flex; flex-direction: column; overflow: hidden; min-height: 0; }
.table-header { display: flex; align-items: center; justify-content: space-between; }
.table-header .section-title { margin-bottom: 0; }
:deep(.el-table) { margin-top: 8px; flex: 1; --el-table-border-color: #e8e8e8; }
:deep(.el-table__body-wrapper) { overflow: auto; }
:deep(.el-table th.el-table__cell) { background-color: #f5f5f5; color: #003; font-weight: 700; font-size: 14px; padding: 8px 0; text-align: center; border-right: 1px solid #e8e8e8; border-bottom: 1px solid #e8e8e8; }
:deep(.el-table td.el-table__cell) { padding: 8px 0; font-size: 14px; color: #000; border-right: 1px solid #e8e8e8; border-bottom: 1px solid #e8e8e8; }
:deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) { background-color: #f5faff; }
:deep(.el-table--border) { border: 1px solid #e8e8e8; }
.table-footer { display: flex; align-items: center; justify-content: space-between; padding: 6px 0; margin-top: 6px; flex-shrink: 0; }
.total-info { font-size: 14px; color: #000; }
:deep(.el-pagination) { padding: 0; }
:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) { background-color: #409eff; }
:deep(.el-pagination .el-select .el-input) { width: 100px; }
</style>
