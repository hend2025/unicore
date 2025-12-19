<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <PageCard title="信息查询">
      <SearchForm v-model="queryParams" @search="loadData" @reset="handleReset">
        <el-form-item label="系统名称">
          <el-input v-model="queryParams.sysName" placeholder="请输入系统名称" clearable />
        </el-form-item>
      </SearchForm>
    </PageCard>

    <!-- 应用列表 -->
    <PageCard title="应用列表" flex>
      <template #extra>
        <el-button type="primary" @click="handleAdd(form)">新增应用</el-button>
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
        <el-table-column prop="sysName" label="系统名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="sysAbbr" label="系统简称" min-width="100" show-overflow-tooltip />
        <el-table-column prop="sysUrl" label="系统URL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="orderNum" label="排序" width="80" show-overflow-tooltip />
        <el-table-column prop="stasFlag" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.stasFlag === '1' ? 'success' : 'danger'">{{ row.stasFlag === '1' ? '正常' : '停用' }}</el-tag>
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
      <el-form-item label="系统名称" prop="sysName">
        <el-input v-model="form.sysName" placeholder="请输入系统名称" />
      </el-form-item>
      <el-form-item label="系统简称" prop="sysAbbr">
        <el-input v-model="form.sysAbbr" placeholder="请输入系统简称" />
      </el-form-item>
      <el-form-item label="系统URL" prop="sysUrl">
        <el-input v-model="form.sysUrl" placeholder="请输入系统URL" />
      </el-form-item>
      <el-form-item label="首页URL" prop="homeUrl">
        <el-input v-model="form.homeUrl" placeholder="请输入首页URL" />
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
import { reactive } from 'vue'
import { systemApi } from '@/api/system'
import { useCrud } from '@/hooks/useCrud'

const form = reactive({ sysId: null, sysName: '', sysAbbr: '', sysUrl: '', homeUrl: '', orderNum: 0, stasFlag: '1', prntId: 0 })

const rules = {
  sysName: [{ required: true, message: '请输入系统名称', trigger: 'blur' }],
  sysAbbr: [{ required: true, message: '请输入系统简称', trigger: 'blur' }]
}

const {
  loading, submitLoading, tableData, total, dialogVisible, dialogTitle, queryParams,
  loadData, handleReset, handleAdd, handleEdit, handleSubmit, handleDelete
} = useCrud(systemApi, {
  defaultQuery: { sysName: '' },
  defaultForm: { sysName: '', sysAbbr: '', sysUrl: '', homeUrl: '', orderNum: 0, stasFlag: '1', prntId: 0 },
  rowKey: 'sysId',
  title: '应用'
})
</script>
