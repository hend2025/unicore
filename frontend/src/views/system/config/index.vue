<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <PageCard title="信息查询">
      <SearchForm v-model="queryParams" @search="loadData" @reset="handleReset">
        <el-form-item label="参数名称">
          <el-input v-model="queryParams.configName" placeholder="请输入参数名称" clearable />
        </el-form-item>
        <el-form-item label="参数键名">
          <el-input v-model="queryParams.configKey" placeholder="请输入参数键名" clearable />
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
        <el-table-column prop="configName" label="参数名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="configKey" label="参数键名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="configValue" label="参数键值" min-width="150" show-overflow-tooltip />
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
    <FormDialog v-model:visible="dialogVisible" :title="dialogTitle" :rules="rules" :modelValue="form" :loading="submitLoading" @submit="handleSubmit(form)">
      <el-form-item label="参数名称" prop="configName">
        <el-input v-model="form.configName" placeholder="请输入参数名称" />
      </el-form-item>
      <el-form-item label="参数键名" prop="configKey">
        <el-input v-model="form.configKey" placeholder="请输入参数键名" />
      </el-form-item>
      <el-form-item label="参数键值" prop="configValue">
        <el-input v-model="form.configValue" type="textarea" placeholder="请输入参数键值" />
      </el-form-item>
      <el-form-item label="备注" prop="remarks">
        <el-input v-model="form.remarks" type="textarea" placeholder="请输入备注" />
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
import { configApi } from '@/api/system'
import { useCrud } from '@/hooks/useCrud'

const form = reactive({ configId: null, configName: '', configKey: '', configValue: '', remarks: '', stasFlag: '1' })

const rules = {
  configName: [{ required: true, message: '请输入参数名称', trigger: 'blur' }],
  configKey: [{ required: true, message: '请输入参数键名', trigger: 'blur' }],
  configValue: [{ required: true, message: '请输入参数键值', trigger: 'blur' }]
}

const {
  loading, submitLoading, tableData, total, dialogVisible, dialogTitle, queryParams,
  loadData, handleReset, handleAdd, handleEdit, handleSubmit, handleDelete
} = useCrud(configApi, {
  defaultQuery: { configName: '', configKey: '' },
  defaultForm: { configName: '', configKey: '', configValue: '', remarks: '', stasFlag: '1' },
  rowKey: 'configId',
  title: '参数'
})
</script>
