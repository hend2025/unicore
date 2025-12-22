<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <PageCard title="信息查询">
      <SearchForm v-model="queryParams" label-width="100px" @search="handleQuery" @reset="handleReset">
        <el-form-item label="区划名称">
          <el-input v-model="queryParams.admdvsName" placeholder="请输入区划名称" clearable />
        </el-form-item>
        <el-form-item label="区划编码">
          <el-input v-model="queryParams.admdvsCode" placeholder="请输入区划编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.stasFlag" placeholder="请选择状态" clearable>
            <el-option label="正常" value="1" />
            <el-option label="停用" value="0" />
          </el-select>
        </el-form-item>
      </SearchForm>
    </PageCard>

    <!-- 医保区划列表 -->
    <PageCard title="医保区划列表" flex>
      <template #extra>
        <el-button type="primary" @click="handleAdd">新增医保区划</el-button>
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
        <el-table-column prop="admdvsCode" label="医保区划编码" width="200" show-overflow-tooltip />
        <el-table-column prop="admdvsName" label="医保区划名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="prntAdmdvsCode" label="上级区划编码" width="200" show-overflow-tooltip />
        <el-table-column prop="admdvsLv" label="区划级别" width="100" show-overflow-tooltip />
        <el-table-column prop="latlnt" label="经纬度" min-width="200" show-overflow-tooltip />
        <el-table-column prop="stasFlag" label="状态" width="80">
          <template #default="{ row }">
            <StatusTag :status="row.stasFlag" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="128" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </DataTable>
    </PageCard>

    <!-- 对话框 -->
    <FormDialog v-model:visible="dialogVisible" :title="dialogTitle" :rules="rules" :modelValue="form" :loading="submitLoading" label-width="120px" @submit="handleSubmit">
      <el-form-item label="医保区划编码" prop="admdvsCode">
        <el-input v-model="form.admdvsCode" placeholder="请输入医保区划编码(6位)" maxlength="6" :disabled="!!form.admdvsCode && dialogTitle === '编辑医保区划'" />
      </el-form-item>
      <el-form-item label="医保区划名称" prop="admdvsName">
        <el-input v-model="form.admdvsName" placeholder="请输入医保区划名称" />
      </el-form-item>
      <el-form-item label="上级医保区划" prop="prntAdmdvsCode">
        <el-tree-select v-model="form.prntAdmdvsCode" :data="treeData" :props="{ label: 'admdvsName', value: 'admdvsCode' }" check-strictly clearable placeholder="请选择上级医保区划" />
      </el-form-item>
      <el-form-item label="区划级别" prop="admdvsLv">
        <el-input v-model="form.admdvsLv" placeholder="请输入区划级别" />
      </el-form-item>
      <el-form-item label="经纬度" prop="latlnt">
        <el-input v-model="form.latlnt" placeholder="请输入经纬度" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { admdvsApi } from '@/api/system'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const treeData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const queryParams = reactive({ pageNum: 1, pageSize: 10, admdvsName: '', admdvsCode: '', stasFlag: '' })
const form = reactive({ admdvsCode: '', admdvsName: '', prntAdmdvsCode: null, admdvsLv: '', latlnt: '', stasFlag: '1' })
const rules = {
  admdvsCode: [
    { required: true, message: '请输入医保区划编码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '医保区划编码必须为6位数字', trigger: 'blur' }
  ],
  admdvsName: [{ required: true, message: '请输入医保区划名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await admdvsApi.page(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const loadTreeData = async () => {
  const res = await admdvsApi.tree()
  treeData.value = res.data
}

const handleQuery = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleAdd = () => {
  Object.assign(form, { admdvsCode: '', admdvsName: '', prntAdmdvsCode: null, admdvsLv: '', latlnt: '', stasFlag: '1' })
  dialogTitle.value = '新增医保区划'
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  const res = await admdvsApi.get(row.admdvsCode)
  Object.assign(form, res.data)
  dialogTitle.value = '编辑医保区划'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    if (dialogTitle.value === '编辑医保区划') {
      await admdvsApi.update(form)
    } else {
      await admdvsApi.add(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
    loadTreeData()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该医保区划?', '提示', { type: 'warning' }).then(async () => {
    await admdvsApi.delete(row.admdvsCode)
    ElMessage.success('删除成功')
    loadData()
    loadTreeData()
  })
}

onMounted(() => {
  loadData()
  loadTreeData()
})
</script>
