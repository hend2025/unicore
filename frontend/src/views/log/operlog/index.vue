<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <PageCard title="信息查询">
      <SearchForm v-model="queryParams" @search="loadData" @reset="handleReset">
        <el-form-item label="请求URL">
          <el-input v-model="queryParams.repUrl" placeholder="请输入请求URL" clearable />
        </el-form-item>
      </SearchForm>
    </PageCard>

    <!-- 操作日志列表 -->
    <PageCard title="操作日志列表" flex>
      <template #extra>
        <el-button type="danger" @click="handleClearDays">清空N天前</el-button>
        <el-button type="danger" @click="handleClear">清空全部</el-button>
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
        <el-table-column prop="repMethod" label="请求方法" width="100" show-overflow-tooltip />
        <el-table-column prop="repIp" label="IP地址" width="140" show-overflow-tooltip />
        <el-table-column prop="execStat" label="执行状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.execStat === '1' ? 'success' : 'danger'">{{ row.execStat === '1' ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="execDura" label="执行时长(ms)" width="120" show-overflow-tooltip />
        <el-table-column prop="crteTime" label="操作时间" width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </DataTable>
    </PageCard>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { logApi } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({ pageNum: 1, pageSize: 10, repUrl: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await logApi.operPage(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该日志?', '提示', { type: 'warning' }).then(async () => {
    await logApi.deleteOper(row.logId)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleClear = () => {
  ElMessageBox.confirm('确认清空全部操作日志?', '提示', { type: 'warning' }).then(async () => {
    await logApi.clearOper()
    ElMessage.success('清空成功')
    loadData()
  })
}

const handleClearDays = () => {
  ElMessageBox.prompt('请输入天数，将清空N天前的日志', '清空N天前日志', {
    confirmButtonText: '确定', cancelButtonText: '取消', inputPattern: /^\d+$/, inputErrorMessage: '请输入有效的天数'
  }).then(async ({ value }) => {
    await logApi.clearOperDays(value)
    ElMessage.success('清空成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>
