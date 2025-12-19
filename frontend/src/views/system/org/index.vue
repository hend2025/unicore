<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <PageCard title="信息查询">
      <SearchForm v-model="queryParams" @search="handleQuery" @reset="handleReset">
        <el-form-item label="机构名称">
          <el-input v-model="queryParams.orgName" placeholder="请输入机构名称" clearable />
        </el-form-item>
        <el-form-item label="机构编码">
          <el-input v-model="queryParams.orgCode" placeholder="请输入机构编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.stasFlag" placeholder="请选择状态" clearable>
            <el-option label="正常" value="1" />
            <el-option label="停用" value="0" />
          </el-select>
        </el-form-item>
      </SearchForm>
    </PageCard>

    <!-- 机构列表 -->
    <PageCard title="机构列表" flex>
      <template #extra>
        <el-button type="primary" @click="handleAdd(0)">新增机构</el-button>
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
        <el-table-column prop="orgName" label="机构名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="orgCode" label="机构编码" width="150" show-overflow-tooltip />
        <el-table-column prop="prntOrgId" label="上级机构ID" width="120" show-overflow-tooltip />
        <el-table-column prop="orgType" label="机构类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.orgType === '1'">单位</el-tag>
            <el-tag v-else type="success">部门</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contName" label="联系人" width="100" show-overflow-tooltip />
        <el-table-column prop="contPhone" label="联系电话" width="140" show-overflow-tooltip />
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
    <FormDialog v-model:show="dialogVisible" :title="dialogTitle" :rules="rules" :modelValue="form" :loading="submitLoading" width="600px" @submit="handleSubmit">
      <el-form-item label="上级机构" prop="prntOrgId">
        <el-tree-select v-model="form.prntOrgId" :data="orgOptions" :props="{ label: 'orgName', value: 'orgId', children: 'children' }" check-strictly placeholder="选择上级机构" style="width: 100%" />
      </el-form-item>
      <el-form-item label="机构名称" prop="orgName">
        <el-input v-model="form.orgName" placeholder="请输入机构名称" />
      </el-form-item>
      <el-form-item label="机构编码" prop="orgCode">
        <el-input v-model="form.orgCode" placeholder="请输入机构编码" />
      </el-form-item>
      <el-form-item label="机构类型" prop="orgType">
        <el-radio-group v-model="form.orgType">
          <el-radio label="1">单位</el-radio>
          <el-radio label="2">部门</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="联系人" prop="contName">
        <el-input v-model="form.contName" placeholder="请输入联系人" />
      </el-form-item>
      <el-form-item label="联系电话" prop="contPhone">
        <el-input v-model="form.contPhone" placeholder="请输入联系电话" />
      </el-form-item>
      <el-form-item label="机构地址" prop="orgAddr">
        <el-input v-model="form.orgAddr" placeholder="请输入机构地址" />
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
import { orgApi } from '@/api/system'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const treeData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const queryParams = reactive({ pageNum: 1, pageSize: 10, orgName: '', orgCode: '', stasFlag: '' })
const form = reactive({ orgId: null, prntOrgId: 0, orgName: '', orgCode: '', orgType: '1', contName: '', contPhone: '', orgAddr: '', orderNum: 0, stasFlag: '1' })
const rules = {
  orgName: [{ required: true, message: '请输入机构名称', trigger: 'blur' }],
  orgCode: [{ required: true, message: '请输入机构编码', trigger: 'blur' }]
}

const orgOptions = computed(() => [{ orgId: 0, orgName: '顶级机构', children: treeData.value }])

const loadData = async () => {
  loading.value = true
  try {
    const res = await orgApi.page(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const loadTreeData = async () => {
  const res = await orgApi.tree()
  treeData.value = res.data
}

const handleQuery = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  loadData()
}

const handleAdd = (prntOrgId) => {
  Object.assign(form, { orgId: null, prntOrgId, orgName: '', orgCode: '', orgType: '1', contName: '', contPhone: '', orgAddr: '', orderNum: 0, stasFlag: '1' })
  dialogTitle.value = '新增机构'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogTitle.value = '编辑机构'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    if (form.orgId) {
      await orgApi.update(form)
    } else {
      await orgApi.add(form)
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
  ElMessageBox.confirm('确认删除该机构?', '提示', { type: 'warning' }).then(async () => {
    await orgApi.delete(row.orgId)
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
