<template>
  <div class="page-container">
    <el-tabs v-model="activeTab" class="dict-tabs">
      <!-- 数据字典 -->
      <el-tab-pane label="数据字典" name="data">
        <div class="query-card">
          <div class="section-title"><i></i>信息查询</div>
          <div class="query-section">
            <el-form :model="dataQuery" inline class="query-form" label-width="80px" @submit.prevent>
              <el-form-item label="字典类型">
                <el-select v-model="dataQuery.dicTypeCode" placeholder="请选择字典类型" clearable>
                  <el-option v-for="item in dictTypeList" :key="item.dicTypeCode" :label="item.dicTypeName" :value="item.dicTypeCode" />
                </el-select>
              </el-form-item>
              <el-form-item label="字典标签">
                <el-input v-model="dataQuery.dicName" placeholder="请输入字典标签" clearable />
              </el-form-item>
              <el-form-item label="字典值">
                <el-input v-model="dataQuery.dicValue" placeholder="请输入字典值" clearable />
              </el-form-item>
              <el-form-item>
                <el-button @click="handleDataReset">重置</el-button>
                <el-button type="primary" @click="loadDataList">查询</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
        <div class="table-card">
          <div class="table-header">
            <div class="section-title"><i></i>数据字典列表</div>
            <el-button type="primary" @click="handleAddData">新增数据</el-button>
          </div>
          <el-table :data="dataList" v-loading="dataLoading" border style="width: 100%; flex: 1" height="100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }" :show-overflow-tooltip="true">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="dicTypeCode" label="字典类型" min-width="120" show-overflow-tooltip />
            <el-table-column prop="dicName" label="字典标签" min-width="120" show-overflow-tooltip />
            <el-table-column prop="dicValue" label="字典值" min-width="100" show-overflow-tooltip />
            <el-table-column prop="orderNum" label="排序" width="80" show-overflow-tooltip />
            <el-table-column prop="stasFlag" label="状态" width="80">
              <template #default="{ row }">
                <StatusTag :status="row.stasFlag" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="128" align="center">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditData(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteData(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="table-footer">
            <span class="total-info">总共{{ dataTotal }}条 显示{{ dataShowStart }}-{{ dataShowEnd }}条</span>
            <el-pagination v-model:current-page="dataQuery.pageNum" v-model:page-size="dataQuery.pageSize" :page-sizes="[10, 20, 50, 100]" :total="dataTotal" background small layout="sizes, prev, pager, next, jumper" @size-change="loadDataList" @current-change="loadDataList" />
          </div>
        </div>
      </el-tab-pane>

      <!-- 字典类型 -->
      <el-tab-pane label="字典类型" name="type">
        <div class="query-card">
          <div class="section-title"><i></i>信息查询</div>
          <div class="query-section">
            <el-form :model="typeQuery" inline class="query-form" label-width="80px" @submit.prevent>
              <el-form-item label="类型名称">
                <el-input v-model="typeQuery.dicTypeName" placeholder="请输入类型名称" clearable />
              </el-form-item>
              <el-form-item>
                <el-button @click="handleTypeReset">重置</el-button>
                <el-button type="primary" @click="loadTypeList">查询</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
        <div class="table-card">
          <div class="table-header">
            <div class="section-title"><i></i>字典类型列表</div>
            <el-button type="primary" @click="handleAddType">新增类型</el-button>
          </div>
          <el-table :data="typeList" v-loading="typeLoading" border style="width: 100%; flex: 1" height="100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }" :show-overflow-tooltip="true">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="dicTypeCode" label="字典类型" min-width="120" show-overflow-tooltip />
            <el-table-column prop="dicTypeName" label="字典名称" min-width="120" show-overflow-tooltip />
            <el-table-column prop="stasFlag" label="状态" width="80">
              <template #default="{ row }">
                <StatusTag :status="row.stasFlag" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="128" align="center">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditType(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteType(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="table-footer">
            <span class="total-info">总共{{ typeTotal }}条 显示{{ typeShowStart }}-{{ typeShowEnd }}条</span>
            <el-pagination v-model:current-page="typeQuery.pageNum" v-model:page-size="typeQuery.pageSize" :page-sizes="[10, 20, 50, 100]" :total="typeTotal" background small layout="sizes, prev, pager, next, jumper" @size-change="loadTypeList" @current-change="loadTypeList" />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 数据字典对话框 -->
    <FormDialog v-model:show="dataDialogVisible" :title="dataDialogTitle" :rules="dataRules" :modelValue="dataForm" :loading="dataSubmitLoading" @submit="handleDataSubmit">
      <el-form-item label="字典类型" prop="dicTypeCode">
        <el-select v-model="dataForm.dicTypeCode" placeholder="请选择字典类型" style="width: 100%" @change="handleTypeChange">
          <el-option v-for="item in dictTypeList" :key="item.dicTypeCode" :label="item.dicTypeName" :value="item.dicTypeCode" />
        </el-select>
      </el-form-item>
      <el-form-item label="字典标签" prop="dicName">
        <el-input v-model="dataForm.dicName" placeholder="请输入字典标签" />
      </el-form-item>
      <el-form-item label="字典值" prop="dicValue">
        <el-input v-model="dataForm.dicValue" placeholder="请输入字典值" />
      </el-form-item>
      <el-form-item label="排序" prop="orderNum">
        <el-input-number v-model="dataForm.orderNum" :min="0" />
      </el-form-item>
      <el-form-item label="状态" prop="stasFlag">
        <el-radio-group v-model="dataForm.stasFlag">
          <el-radio label="1">正常</el-radio>
          <el-radio label="0">停用</el-radio>
        </el-radio-group>
      </el-form-item>
    </FormDialog>

    <!-- 字典类型对话框 -->
    <FormDialog v-model:show="typeDialogVisible" :title="typeDialogTitle" :rules="typeRules" :modelValue="typeForm" :loading="typeSubmitLoading" @submit="handleTypeSubmit">
      <el-form-item label="字典类型" prop="dicTypeCode">
        <el-input v-model="typeForm.dicTypeCode" placeholder="请输入字典类型" :disabled="typeDialogTitle === '编辑字典类型'" />
      </el-form-item>
      <el-form-item label="字典名称" prop="dicTypeName">
        <el-input v-model="typeForm.dicTypeName" placeholder="请输入字典名称" />
      </el-form-item>
      <el-form-item label="状态" prop="stasFlag">
        <el-radio-group v-model="typeForm.stasFlag">
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
import { dictApi } from '@/api/system'

const activeTab = ref('data')
const dictTypeList = ref([])

// 数据字典相关
const dataLoading = ref(false)
const dataSubmitLoading = ref(false)
const dataList = ref([])
const dataTotal = ref(0)
const dataDialogVisible = ref(false)
const dataDialogTitle = ref('')
const dataQuery = reactive({ pageNum: 1, pageSize: 10, dicTypeCode: '', dicName: '', dicValue: '' })
const dataForm = reactive({ dicId: '', dicTypeCode: '', dicTypeName: '', dicName: '', dicValue: '', orderNum: 0, stasFlag: '1' })
const dataRules = {
  dicTypeCode: [{ required: true, message: '请选择字典类型', trigger: 'change' }],
  dicName: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  dicValue: [{ required: true, message: '请输入字典值', trigger: 'blur' }]
}

// 字典类型相关
const typeLoading = ref(false)
const typeSubmitLoading = ref(false)
const typeList = ref([])
const typeTotal = ref(0)
const typeDialogVisible = ref(false)
const typeDialogTitle = ref('')
const typeQuery = reactive({ pageNum: 1, pageSize: 10, dicTypeName: '' })
const typeForm = reactive({ dicTypeCode: '', dicTypeName: '', stasFlag: '1' })
const typeRules = {
  dicTypeCode: [{ required: true, message: '请输入字典类型', trigger: 'blur' }],
  dicTypeName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }]
}

const dataShowStart = computed(() => dataTotal.value ? (dataQuery.pageNum - 1) * dataQuery.pageSize + 1 : 0)
const dataShowEnd = computed(() => Math.min(dataQuery.pageNum * dataQuery.pageSize, dataTotal.value))
const typeShowStart = computed(() => typeTotal.value ? (typeQuery.pageNum - 1) * typeQuery.pageSize + 1 : 0)
const typeShowEnd = computed(() => Math.min(typeQuery.pageNum * typeQuery.pageSize, typeTotal.value))

// 加载字典类型下拉
const loadDictTypes = async () => {
  const res = await dictApi.typeList()
  dictTypeList.value = res.data
}

// 数据字典方法
const loadDataList = async () => {
  dataLoading.value = true
  try {
    const res = await dictApi.dataPage(dataQuery)
    dataList.value = res.data.records
    dataTotal.value = res.data.total
  } finally {
    dataLoading.value = false
  }
}

const handleDataReset = () => {
  Object.assign(dataQuery, { pageNum: 1, pageSize: 10, dicTypeCode: '', dicName: '', dicValue: '' })
  loadDataList()
}

const handleTypeChange = (val) => {
  const type = dictTypeList.value.find(t => t.dicTypeCode === val)
  dataForm.dicTypeName = type ? type.dicTypeName : ''
}

const handleAddData = () => {
  Object.assign(dataForm, { dicId: '', dicTypeCode: '', dicTypeName: '', dicName: '', dicValue: '', orderNum: 0, stasFlag: '1' })
  dataDialogTitle.value = '新增字典数据'
  dataDialogVisible.value = true
}

const handleEditData = (row) => {
  Object.assign(dataForm, row)
  dataDialogTitle.value = '编辑字典数据'
  dataDialogVisible.value = true
}

const handleDataSubmit = async () => {
  dataSubmitLoading.value = true
  try {
    if (dataDialogTitle.value === '编辑字典数据') {
      await dictApi.updateData(dataForm)
    } else {
      dataForm.dicId = Date.now().toString()
      await dictApi.addData(dataForm)
    }
    ElMessage.success('操作成功')
    dataDialogVisible.value = false
    loadDataList()
  } finally {
    dataSubmitLoading.value = false
  }
}

const handleDeleteData = (row) => {
  ElMessageBox.confirm('确认删除该字典数据?', '提示', { type: 'warning' }).then(async () => {
    await dictApi.deleteData(row.dicId)
    ElMessage.success('删除成功')
    loadDataList()
  })
}

// 字典类型方法
const loadTypeList = async () => {
  typeLoading.value = true
  try {
    const res = await dictApi.typePage(typeQuery)
    typeList.value = res.data.records
    typeTotal.value = res.data.total
  } finally {
    typeLoading.value = false
  }
}

const handleTypeReset = () => {
  Object.assign(typeQuery, { pageNum: 1, pageSize: 10, dicTypeName: '' })
  loadTypeList()
}

const handleAddType = () => {
  Object.assign(typeForm, { dicTypeCode: '', dicTypeName: '', stasFlag: '1' })
  typeDialogTitle.value = '新增字典类型'
  typeDialogVisible.value = true
}

const handleEditType = (row) => {
  Object.assign(typeForm, row)
  typeDialogTitle.value = '编辑字典类型'
  typeDialogVisible.value = true
}

const handleTypeSubmit = async () => {
  typeSubmitLoading.value = true
  try {
    if (typeDialogTitle.value === '编辑字典类型') {
      await dictApi.updateType(typeForm)
    } else {
      await dictApi.addType(typeForm)
    }
    ElMessage.success('操作成功')
    typeDialogVisible.value = false
    loadTypeList()
    loadDictTypes()
  } finally {
    typeSubmitLoading.value = false
  }
}

const handleDeleteType = (row) => {
  ElMessageBox.confirm('确认删除该字典类型?', '提示', { type: 'warning' }).then(async () => {
    await dictApi.deleteType(row.dicTypeCode)
    ElMessage.success('删除成功')
    loadTypeList()
    loadDictTypes()
  })
}

onMounted(() => {
  loadDictTypes()
  loadDataList()
  loadTypeList()
})
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; padding: 10px; background: #f5f5f5; height: 100%; box-sizing: border-box; overflow: hidden; }
.dict-tabs { height: 100%; display: flex; flex-direction: column; }
.dict-tabs :deep(.el-tabs__content) { flex: 1; overflow: hidden; }
.dict-tabs :deep(.el-tab-pane) { height: 100%; display: flex; flex-direction: column; }
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
.table-footer { display: flex; align-items: center; justify-content: space-between; padding: 6px 0; margin-top: 6px; flex-shrink: 0; }
.total-info { font-size: 14px; color: #000; }
:deep(.el-pagination) { padding: 0; }
:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) { background-color: #409eff; }
:deep(.el-pagination .el-select .el-input) { width: 100px; }
</style>
