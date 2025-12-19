<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <PageCard title="信息查询">
      <SearchForm v-model="queryParams" @search="handleQuery" @reset="handleReset">
        <el-form-item label="地区名称">
          <el-input v-model="queryParams.areaName" placeholder="请输入地区名称" clearable />
        </el-form-item>
        <el-form-item label="地区编码">
          <el-input v-model="queryParams.areaCode" placeholder="请输入地区编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.stasFlag" placeholder="请选择状态" clearable>
            <el-option label="正常" value="1" />
            <el-option label="停用" value="0" />
          </el-select>
        </el-form-item>
      </SearchForm>
    </PageCard>

    <!-- 地区列表 -->
    <PageCard title="地区列表" flex>
      <template #extra>
        <el-button type="primary" @click="handleAdd">新增地区</el-button>
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
        <el-table-column prop="areaCode" label="地区编码" width="150" show-overflow-tooltip />
        <el-table-column prop="areaName" label="地区名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="prntAreaCode" label="上级地区编码" width="150" show-overflow-tooltip />
        <el-table-column prop="areaLv" label="地区级别" width="100" show-overflow-tooltip />
        <el-table-column prop="latlnt" label="经纬度" min-width="150" show-overflow-tooltip />
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
      </DataTable>
    </PageCard>

    <!-- 对话框 -->
    <FormDialog v-model:show="dialogVisible" :title="dialogTitle" :rules="rules" :modelValue="form" :loading="submitLoading" @submit="handleSubmit">
      <el-form-item label="地区编码" prop="areaCode">
        <el-input v-model="form.areaCode" placeholder="请输入地区编码" :disabled="!!form.areaCode && dialogTitle === '编辑地区'" />
      </el-form-item>
      <el-form-item label="地区名称" prop="areaName">
        <el-input v-model="form.areaName" placeholder="请输入地区名称" />
      </el-form-item>
      <el-form-item label="上级地区" prop="prntAreaCode">
        <el-tree-select v-model="form.prntAreaCode" :data="treeData" :props="{ label: 'areaName', value: 'areaCode' }" check-strictly clearable placeholder="请选择上级地区" />
      </el-form-item>
      <el-form-item label="地区级别" prop="areaLv">
        <el-input v-model="form.areaLv" placeholder="请输入地区级别" />
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
import PageCard from '@/components/PageCard.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataTable from '@/components/DataTable.vue'
import FormDialog from '@/components/FormDialog.vue'
import { areaApi } from '@/api/system'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const treeData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const queryParams = reactive({ pageNum: 1, pageSize: 10, areaName: '', areaCode: '', stasFlag: '' })
const form = reactive({ areaCode: '', areaName: '', prntAreaCode: null, areaLv: '', latlnt: '', stasFlag: '1' })
const rules = {
  areaCode: [{ required: true, message: '请输入地区编码', trigger: 'blur' }],
  areaName: [{ required: true, message: '请输入地区名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await areaApi.page(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const loadTreeData = async () => {
  const res = await areaApi.tree()
  treeData.value = res.data
}

const handleQuery = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  loadData()
}

const handleAdd = () => {
  Object.assign(form, { areaCode: '', areaName: '', prntAreaCode: null, areaLv: '', latlnt: '', stasFlag: '1' })
  dialogTitle.value = '新增地区'
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  const res = await areaApi.get(row.areaCode)
  Object.assign(form, res.data)
  dialogTitle.value = '编辑地区'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    if (dialogTitle.value === '编辑地区') {
      await areaApi.update(form)
    } else {
      await areaApi.add(form)
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
  ElMessageBox.confirm('确认删除该地区?', '提示', { type: 'warning' }).then(async () => {
    await areaApi.delete(row.areaCode)
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
