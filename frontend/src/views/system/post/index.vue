<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <PageCard title="信息查询">
      <SearchForm v-model="queryParams" @search="loadData" @reset="handleReset">
        <el-form-item label="岗位名称">
          <el-input v-model="queryParams.postName" placeholder="请输入岗位名称" clearable />
        </el-form-item>
      </SearchForm>
    </PageCard>

    <!-- 岗位列表 -->
    <PageCard title="岗位列表" flex>
      <template #extra>
        <el-button type="primary" @click="handleAdd(form)">新增岗位</el-button>
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
        <el-table-column prop="postCode" label="岗位编码" min-width="120" show-overflow-tooltip />
        <el-table-column prop="postName" label="岗位名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="orderNum" label="排序" width="80" show-overflow-tooltip />
        <el-table-column prop="remarks" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="stasFlag" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.stasFlag === '1' ? 'success' : 'danger'">{{ row.stasFlag === '1' ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="crteTime" label="创建时间" width="180" show-overflow-tooltip />
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
      <el-form-item label="岗位编码" prop="postCode">
        <el-input v-model="form.postCode" placeholder="请输入岗位编码" />
      </el-form-item>
      <el-form-item label="岗位名称" prop="postName">
        <el-input v-model="form.postName" placeholder="请输入岗位名称" />
      </el-form-item>
      <el-form-item label="排序" prop="orderNum">
        <el-input-number v-model="form.orderNum" :min="0" />
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
import { postApi } from '@/api/system'
import { useCrud } from '@/hooks/useCrud'

// 表单数据（需要在页面定义，因为模板需要绑定）
const form = reactive({ postId: null, postCode: '', postName: '', orderNum: 0, remarks: '', stasFlag: '1' })

// 表单校验规则
const rules = {
  postCode: [{ required: true, message: '请输入岗位编码', trigger: 'blur' }],
  postName: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }]
}

// 使用 CRUD Hook
const {
  loading, submitLoading, tableData, total, dialogVisible, dialogTitle, queryParams,
  loadData, handleReset, handleAdd, handleEdit, handleSubmit, handleDelete
} = useCrud(postApi, {
  defaultQuery: { postName: '' },
  defaultForm: { postCode: '', postName: '', orderNum: 0, remarks: '', stasFlag: '1' },
  rowKey: 'postId',
  title: '岗位'
})

</script>
