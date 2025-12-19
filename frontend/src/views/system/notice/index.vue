<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <PageCard title="信息查询">
      <SearchForm v-model="queryParams" @search="loadData" @reset="handleReset">
        <el-form-item label="公告标题">
          <el-input v-model="queryParams.noticeTitle" placeholder="请输入公告标题" clearable />
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select v-model="queryParams.noticeType" placeholder="请选择公告类型" clearable>
            <el-option label="通知" value="1" />
            <el-option label="公告" value="2" />
          </el-select>
        </el-form-item>
      </SearchForm>
    </PageCard>

    <!-- 公告列表 -->
    <PageCard title="公告列表" flex>
      <template #extra>
        <el-button type="primary" @click="handleAdd">新增公告</el-button>
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
        <el-table-column prop="noticeTitle" label="公告标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="noticeType" label="公告类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.noticeType === '1'">通知</el-tag>
            <el-tag v-else type="success">公告</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="stasFlag" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.stasFlag === '1' ? 'success' : 'danger'">{{ row.stasFlag === '1' ? '正常' : '关闭' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="crteTime" label="创建时间" width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="128" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </DataTable>
    </PageCard>

    <!-- 对话框 -->
    <FormDialog v-model:show="dialogVisible" :title="dialogTitle" :rules="rules" :modelValue="form" :loading="submitLoading" width="700px" @submit="handleSubmit">
      <el-form-item label="公告标题" prop="noticeTitle">
        <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" />
      </el-form-item>
      <el-form-item label="公告类型" prop="noticeType">
        <el-radio-group v-model="form.noticeType">
          <el-radio label="1">通知</el-radio>
          <el-radio label="2">公告</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="公告内容" prop="noticeCont">
        <el-input v-model="form.noticeCont" type="textarea" :rows="6" placeholder="请输入公告内容" />
      </el-form-item>
      <el-form-item label="状态" prop="stasFlag">
        <el-radio-group v-model="form.stasFlag">
          <el-radio label="1">正常</el-radio>
          <el-radio label="0">关闭</el-radio>
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
import { noticeApi } from '@/api/system'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const queryParams = reactive({ pageNum: 1, pageSize: 10, noticeTitle: '', noticeType: '' })
const form = reactive({ noticeId: null, noticeTitle: '', noticeType: '1', noticeCont: '', stasFlag: '1' })
const rules = {
  noticeTitle: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  noticeType: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  noticeCont: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await noticeApi.page(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  loadData()
}

const handleAdd = () => {
  Object.assign(form, { noticeId: null, noticeTitle: '', noticeType: '1', noticeCont: '', stasFlag: '1' })
  dialogTitle.value = '新增公告'
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  const res = await noticeApi.get(row.noticeId)
  Object.assign(form, res.data)
  dialogTitle.value = '编辑公告'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    if (form.noticeId) {
      await noticeApi.update(form)
    } else {
      await noticeApi.add(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该公告?', '提示', { type: 'warning' }).then(async () => {
    await noticeApi.delete(row.noticeId)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>
