<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <div class="query-card">
      <div class="section-title"><i></i>信息查询</div>
      <div class="query-section">
        <el-form :model="queryParams" inline class="query-form" label-width="80px" @submit.prevent>
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
          <el-form-item>
            <el-button @click="handleReset">重置</el-button>
            <el-button type="primary" @click="loadData">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 地区列表 -->
    <div class="table-card">
      <div class="table-header">
        <div class="section-title"><i></i>地区列表</div>
        <div>
          <el-button @click="toggleExpand">{{ isExpand ? '折叠' : '展开' }}</el-button>
          <el-button type="primary" @click="handleAdd">新增地区</el-button>
        </div>
      </div>
      <el-table :data="filteredData" row-key="areaCode" :key="tableKey" :tree-props="{ children: 'children' }" :default-expand-all="isExpand" v-loading="loading" border style="width: 100%; flex: 1" height="100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
        <el-table-column prop="areaCode" label="地区编码" width="150" />
        <el-table-column prop="areaName" label="地区名称" width="200" align="left" />
        <el-table-column prop="areaLv" label="地区级别" width="100" />
        <el-table-column prop="latlnt" label="经纬度" min-width="150" />
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
    </div>

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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import FormDialog from '@/components/FormDialog.vue'
import { areaApi } from '@/api/system'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const treeData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isExpand = ref(true)
const tableKey = ref(0)

const queryParams = reactive({ areaName: '', areaCode: '', stasFlag: '' })
const form = reactive({ areaCode: '', areaName: '', prntAreaCode: null, areaLv: '', latlnt: '', stasFlag: '1' })
const rules = {
  areaCode: [{ required: true, message: '请输入地区编码', trigger: 'blur' }],
  areaName: [{ required: true, message: '请输入地区名称', trigger: 'blur' }]
}

// 递归过滤树形数据
const filterTree = (data) => {
  const { areaName, areaCode, stasFlag } = queryParams
  return data.filter(item => {
    const nameMatch = !areaName || item.areaName.includes(areaName)
    const codeMatch = !areaCode || item.areaCode.includes(areaCode)
    const statusMatch = !stasFlag || item.stasFlag === stasFlag
    if (item.children && item.children.length) {
      item.children = filterTree(item.children)
      return (nameMatch && codeMatch && statusMatch) || item.children.length > 0
    }
    return nameMatch && codeMatch && statusMatch
  }).map(item => ({ ...item, children: item.children ? [...item.children] : [] }))
}

const filteredData = computed(() => {
  if (!queryParams.areaName && !queryParams.areaCode && !queryParams.stasFlag) return tableData.value
  return filterTree(JSON.parse(JSON.stringify(tableData.value)))
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await areaApi.tree()
    tableData.value = res.data
    treeData.value = res.data
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryParams.areaName = ''
  queryParams.areaCode = ''
  queryParams.stasFlag = ''
}

const toggleExpand = () => {
  isExpand.value = !isExpand.value
  tableKey.value++
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
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该地区?', '提示', { type: 'warning' }).then(async () => {
    await areaApi.delete(row.areaCode)
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
.table-card { flex: 1; background: #fff; padding: 10px 12px; border: 1px solid #eee; border-radius: 4px; display: flex; flex-direction: column; overflow: hidden; min-height: 0; }
.table-header { display: flex; align-items: center; justify-content: space-between; }
.table-header .section-title { margin-bottom: 0; }
:deep(.el-table) { margin-top: 8px; flex: 1; --el-table-border-color: #e8e8e8; }
:deep(.el-table__body-wrapper) { overflow: auto; }
:deep(.el-table th.el-table__cell) { background-color: #f5f5f5; color: #003; font-weight: 700; font-size: 14px; padding: 8px 0; text-align: center; border-right: 1px solid #e8e8e8; border-bottom: 1px solid #e8e8e8; }
:deep(.el-table td.el-table__cell) { padding: 8px 0; font-size: 14px; color: #000; border-right: 1px solid #e8e8e8; border-bottom: 1px solid #e8e8e8; }
:deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) { background-color: #f5faff; }
:deep(.el-table--border) { border: 1px solid #e8e8e8; }
</style>
