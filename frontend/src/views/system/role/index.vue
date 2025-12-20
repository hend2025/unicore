<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <PageCard title="信息查询">
      <SearchForm v-model="queryParams" @search="handleSearch" @reset="handleReset">
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="权限字符">
          <el-input v-model="queryParams.roleKey" placeholder="请输入权限字符" clearable />
        </el-form-item>
      </SearchForm>
    </PageCard>

    <!-- 角色列表 -->
    <PageCard title="角色列表" flex>
      <template #extra>
        <el-button type="primary" @click="openAddDialog">新增角色</el-button>
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
        <el-table-column prop="roleName" label="角色名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="roleKey" label="权限字符" min-width="120" show-overflow-tooltip />
        <el-table-column prop="stasFlag" label="状态" width="80">
          <template #default="{ row }">
            <StatusTag :status="row.stasFlag" />
          </template>
        </el-table-column>
        <el-table-column prop="crteTime" label="创建时间" width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="128" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </DataTable>
    </PageCard>

    <!-- 对话框 -->
    <FormDialog v-model:visible="dialogVisible" :title="dialogTitle" :rules="rules" :modelValue="form" :loading="submitLoading" width="600px" @submit="doSubmit" @close="handleDialogClose">
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="form.roleName" placeholder="请输入角色名称" />
      </el-form-item>
      <el-form-item label="权限字符" prop="roleKey">
        <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
      </el-form-item>
      <el-form-item label="菜单权限" prop="menuIds">
        <div class="menu-tree-wrapper">
          <el-tree
            v-if="dialogVisible"
            ref="menuTreeRef"
            :data="menuTree"
            show-checkbox
            node-key="menuId"
            :props="{ label: 'menuName', children: 'children' }"
            :default-checked-keys="checkedMenuIds"
          />
        </div>
      </el-form-item>
      <el-form-item label="状态" prop="stasFlag">
        <el-radio-group v-model="form.stasFlag">
          <el-radio label="1">正常</el-radio>
          <el-radio label="0">停用</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="remarks">
        <el-input v-model="form.remarks" type="textarea" placeholder="请输入备注" />
      </el-form-item>
    </FormDialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { roleApi, menuApi } from '@/api/system'
import { useCrud } from '@/hooks/useCrud'

// 菜单树相关
const menuTree = ref([])
const menuTreeRef = ref()
const checkedMenuIds = ref([])

// 表单数据
const form = reactive({ roleId: null, roleName: '', roleKey: '', menuIds: [], stasFlag: '1', remarks: '' })

// 表单校验规则
const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入权限字符', trigger: 'blur' }]
}

// 使用 CRUD Hook
const {
  loading,
  submitLoading,
  tableData,
  total,
  dialogVisible,
  dialogTitle,
  queryParams,
  loadData,
  handleSearch,
  handleReset,
  handleAdd,
  handleEdit,
  handleSubmit,
  handleDelete
} = useCrud(roleApi, {
  defaultQuery: { roleName: '', roleKey: '' },
  defaultForm: { roleName: '', roleKey: '', menuIds: [], stasFlag: '1', remarks: '' },
  rowKey: 'roleId',
  title: '角色',
  beforeSubmit: async (formData) => {
    // 提交前从树组件获取选中的菜单ID
    formData.menuIds = menuTreeRef.value.getCheckedKeys().concat(menuTreeRef.value.getHalfCheckedKeys())
    return formData
  }
})

// 加载菜单树
const loadMenuTree = async () => {
  const res = await menuApi.tree()
  menuTree.value = res.data
}

// 过滤出叶子节点ID（排除父节点）
const filterLeafMenuIds = (menuIds) => {
  const allParentIds = new Set()
  const collectParentIds = (nodes) => {
    for (const node of nodes) {
      if (node.children && node.children.length > 0) {
        allParentIds.add(node.menuId)
        collectParentIds(node.children)
      }
    }
  }
  collectParentIds(menuTree.value)
  return menuIds.filter(id => !allParentIds.has(id))
}

// 打开新增弹窗
const openAddDialog = () => {
  checkedMenuIds.value = []
  handleAdd(form)
}

// 打开编辑弹窗
const openEditDialog = async (row) => {
  await handleEdit(row, form)
  // 只设置叶子节点的选中状态
  const allMenuIds = form.menuIds || []
  checkedMenuIds.value = filterLeafMenuIds(allMenuIds)
}

// 弹窗关闭时清理
const handleDialogClose = () => {
  checkedMenuIds.value = []
}

// 表单提交
const doSubmit = () => {
  handleSubmit(form)
}

onMounted(() => {
  loadMenuTree()
})
</script>

<style scoped>
.menu-tree-wrapper {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 8px;
  width: 100%;
}
</style>
