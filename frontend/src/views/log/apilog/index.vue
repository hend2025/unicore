<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <PageCard title="信息查询">
      <SearchForm v-model="queryParams" @search="loadData" @reset="handleReset">
        <el-form-item label="请求URL">
          <el-input v-model="queryParams.repUrl" placeholder="请输入请求URL" clearable />
        </el-form-item>
        <el-form-item label="请求方法">
          <el-select v-model="queryParams.repMethod" placeholder="请选择请求方法" clearable>
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行状态">
          <el-select v-model="queryParams.execStat" placeholder="请选择执行状态" clearable>
            <el-option label="成功" value="1" />
            <el-option label="失败" value="0" />
          </el-select>
        </el-form-item>
      </SearchForm>
    </PageCard>

    <!-- API日志列表 -->
    <PageCard title="API日志列表" flex>
      <template #extra>
        <el-button type="danger" @click="handleClear" :disabled="!tableData.length">清空日志</el-button>
        <el-button type="warning" @click="handleClearDays">清空N天前</el-button>
      </template>
      <DataTable
        :data="tableData"
        :loading="loading"
        :total="total"
        v-model:pageNum="queryParams.pageNum"
        v-model:pageSize="queryParams.pageSize"
        @page-change="loadData"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="repUrl" label="请求URL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="repMethod" label="请求方法" width="100" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tag :type="getMethodType(row.repMethod)">{{ row.repMethod }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="repIp" label="IP地址" width="130" show-overflow-tooltip />
        <el-table-column prop="userId" label="用户ID" width="80" show-overflow-tooltip />
        <el-table-column prop="execStat" label="执行状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.execStat === '1' ? 'success' : 'danger'">{{ row.execStat === '1' ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="execDura" label="执行时长(ms)" width="120" show-overflow-tooltip />
        <el-table-column prop="crteTime" label="创建时间" width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="128" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </DataTable>
    </PageCard>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="API日志详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志ID">{{ detail.logId }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.userId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">{{ detail.repUrl }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">
          <el-tag :type="getMethodType(detail.repMethod)">{{ detail.repMethod }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detail.repIp }}</el-descriptions-item>
        <el-descriptions-item label="执行状态">
          <el-tag :type="detail.execStat === '1' ? 'success' : 'danger'">{{ detail.execStat === '1' ? '成功' : '失败' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="执行时长">{{ detail.execDura }} ms</el-descriptions-item>
        <el-descriptions-item label="所属系统">{{ detail.sysId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.crteTime }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <el-input v-model="detail.repPara" type="textarea" :rows="6" readonly />
        </el-descriptions-item>
        <el-descriptions-item label="返回结果" :span="2">
          <el-input v-model="detail.retData" type="textarea" :rows="6" readonly />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { apiLogApi } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const detail = ref({})

const queryParams = reactive({ pageNum: 1, pageSize: 10, repUrl: '', repMethod: '', execStat: '' })

const getMethodType = (method) => {
  const types = { 'GET': 'success', 'POST': 'primary', 'PUT': 'warning', 'DELETE': 'danger' }
  return types[method] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await apiLogApi.page(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleView = async (row) => {
  const res = await apiLogApi.get(row.logId)
  detail.value = res.data
  detailVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该日志?', '提示', { type: 'warning' }).then(async () => {
    await apiLogApi.delete(row.logId)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleClear = () => {
  ElMessageBox.confirm('确认清空所有API日志? 此操作不可恢复!', '警告', { 
    type: 'warning', confirmButtonText: '确认清空', cancelButtonText: '取消'
  }).then(async () => {
    await apiLogApi.clear()
    ElMessage.success('清空成功')
    loadData()
  })
}

const handleClearDays = () => {
  ElMessageBox.prompt('请输入要清空的天数（清空N天前的日志）', '清空日志', {
    confirmButtonText: '确认', cancelButtonText: '取消', inputPattern: /^\d+$/, inputErrorMessage: '请输入有效的天数'
  }).then(async ({ value }) => {
    await apiLogApi.clear(parseInt(value))
    ElMessage.success(`已清空${value}天前的日志`)
    loadData()
  })
}

onMounted(() => loadData())
</script>
