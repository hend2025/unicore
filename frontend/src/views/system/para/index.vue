<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <PageCard title="信息查询">
      <SearchForm v-model="queryParams" @search="loadData" @reset="handleReset">
        <el-form-item label="参数名称">
          <el-input v-model="queryParams.paraName" placeholder="请输入参数名称" clearable />
        </el-form-item>
        <el-form-item label="参数编码">
          <el-input v-model="queryParams.paraCode" placeholder="请输入参数编码" clearable />
        </el-form-item>
      </SearchForm>
    </PageCard>

    <!-- 参数列表 -->
    <PageCard title="参数列表" flex>
      <template #extra>
        <el-button type="primary" @click="handleAdd(form)">新增参数</el-button>
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
        <el-table-column prop="paraCode" label="参数编码" min-width="120" show-overflow-tooltip />
        <el-table-column prop="paraName" label="参数名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="paraValue" label="参数值" min-width="150" show-overflow-tooltip />
        <el-table-column prop="paraType" label="参数类型" width="100" show-overflow-tooltip />
        <el-table-column prop="paraDesc" label="参数说明" min-width="150" show-overflow-tooltip />
        <el-table-column prop="stasFlag" label="状态" width="80">
          <template #default="{ row }">
            <StatusTag :status="row.stasFlag" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="128" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row, form)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </DataTable>
    </PageCard>

    <!-- 对话框 -->
    <FormDialog v-model:show="dialogVisible" :title="dialogTitle" :rules="rules" :modelValue="form" :loading="submitLoading" @submit="handleSubmit(form)">
      <el-form-item label="参数编码" prop="paraCode">
        <el-input v-model="form.paraCode" placeholder="请输入参数编码" />
      </el-form-item>
      <el-form-item label="参数名称" prop="paraName">
        <el-input v-model="form.paraName" placeholder="请输入参数名称" />
      </el-form-item>
      <el-form-item label="参数值" prop="paraValue">
        <el-input v-model="form.paraValue" placeholder="请输入参数值" />
      </el-form-item>
      <el-form-item label="参数类型" prop="paraType">
        <el-input v-model="form.paraType" placeholder="请输入参数类型" />
      </el-form-item>
      <el-form-item label="参数说明" prop="paraDesc">
        <el-input v-model="form.paraDesc" type="textarea" placeholder="请输入参数说明" />
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
import { reactive } from 'vue'
import { paraApi } from '@/api/system'
import { useCrud } from '@/hooks/useCrud'

const form = reactive({ paraId: null, paraCode: '', paraName: '', paraValue: '', paraType: '', paraDesc: '', stasFlag: '1' })

const rules = {
  paraCode: [{ required: true, message: '请输入参数编码', trigger: 'blur' }],
  paraName: [{ required: true, message: '请输入参数名称', trigger: 'blur' }],
  paraValue: [{ required: true, message: '请输入参数值', trigger: 'blur' }]
}

const {
  loading, submitLoading, tableData, total, dialogVisible, dialogTitle, queryParams,
  loadData, handleReset, handleAdd, handleEdit, handleSubmit, handleDelete
} = useCrud(paraApi, {
  defaultQuery: { paraName: '', paraCode: '' },
  defaultForm: { paraCode: '', paraName: '', paraValue: '', paraType: '', paraDesc: '', stasFlag: '1' },
  rowKey: 'paraId',
  title: '参数'
})
</script>
