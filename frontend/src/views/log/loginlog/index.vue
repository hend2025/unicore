<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <div class="query-card">
      <div class="section-title"><i></i>信息查询</div>
      <div class="query-section">
        <el-form :model="queryParams" inline class="query-form" label-width="80px">
          <el-form-item label="用户账号">
            <el-input v-model="queryParams.userName" placeholder="请输入用户账号" clearable />
          </el-form-item>
          <el-form-item>
            <el-button @click="handleReset">重置</el-button>
            <el-button type="primary" @click="loadData">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 登录日志列表 -->
    <div class="table-card">
      <div class="table-header">
        <div class="section-title"><i></i>登录日志列表</div>
        <div>
          <el-button type="danger" @click="handleClearDays">清空N天前</el-button>
          <el-button type="danger" @click="handleClear">清空全部</el-button>
        </div>
      </div>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%; flex: 1" height="100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="userName" label="用户账号" width="120" />
        <el-table-column prop="loginIp" label="登录IP" width="140" />
        <el-table-column prop="loginLoc" label="登录地点" min-width="150" />
        <el-table-column prop="browser" label="浏览器" width="120" />
        <el-table-column prop="os" label="操作系统" width="120" />
        <el-table-column prop="loginStat" label="登录状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.loginStat === '1' ? 'success' : 'danger'">{{ row.loginStat === '1' ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="loginTime" label="登录时间" width="180" />
        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-footer">
        <span class="total-info">总共{{ total }}条 显示{{ showStart }}-{{ showEnd }}条</span>
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :page-sizes="[10, 20, 50, 100]" :total="total" background small layout="sizes, prev, pager, next, jumper" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { logApi } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({ pageNum: 1, pageSize: 10, userName: '' })

const showStart = computed(() => total.value ? (queryParams.pageNum - 1) * queryParams.pageSize + 1 : 0)
const showEnd = computed(() => Math.min(queryParams.pageNum * queryParams.pageSize, total.value))

const loadData = async () => {
  loading.value = true
  try {
    const res = await logApi.loginPage(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  queryParams.userName = ''
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该日志?', '提示', { type: 'warning' }).then(async () => {
    await logApi.deleteLogin(row.loginId)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleClear = () => {
  ElMessageBox.confirm('确认清空全部登录日志?', '提示', { type: 'warning' }).then(async () => {
    await logApi.clearLogin()
    ElMessage.success('清空成功')
    loadData()
  })
}

const handleClearDays = () => {
  ElMessageBox.prompt('请输入天数，将清空N天前的日志', '清空N天前日志', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^\d+$/,
    inputErrorMessage: '请输入有效的天数'
  }).then(async ({ value }) => {
    await logApi.clearLoginDays(value)
    ElMessage.success('清空成功')
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
