<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <PageCard title="信息查询">
      <SearchForm v-model="queryParams" @search="loadData" @reset="handleReset">
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
        <el-button type="primary" @click="handleAdd">新增角色</el-button>
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
            <el-tag :type="row.stasFlag === '1' ? 'success' : 'danger'">{{ row.stasFlag === '1' ? '正常' : '停用' }}</el-tag>
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
    <FormDialog v-model:show="dialogVisible" :title="dialogTitle" :rules="rules" :modelValue="form" :loading="submitLoading" width="600px" @submit="handleSubmit" @close="handleDialogClose">
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
import { ElMessage, ElMessageBox } from 'element-plus'
import PageCard from '@/components/PageCard.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataTable from '@/components/DataTable.vue'
import FormDialog from '@/components/FormDialog.vue'
import { roleApi, menuApi } from '@/api/system'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const menuTree = ref([])
const menuTreeRef = ref()
const checkedMenuIds = ref([])

const queryParams = reactive({ pageNum: 1, pageSize: 10, roleName: '', roleKey: '' })
const form = reactive({ roleId: null, roleName: '', roleKey: '', menuIds: [], stasFlag: '1', remarks: '' })
const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入权限字符', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await roleApi.page(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  loadData()
}

const loadMenuTree = async () => {
  const res = await menuApi.tree()
  menuTree.value = res.data
}

const handleAdd = () => {
  Object.assign(form, { roleId: null, roleName: '', roleKey: '', menuIds: [], stasFlag: '1', remarks: '' })
  checkedMenuIds.value = []
  dialogTitle.value = '新增角色'
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  const res = await roleApi.get(row.roleId)
  Object.assign(form, res.data)
  // 只设置叶子节点的选中状态，避免父节点被重复选中
  const allMenuIds = res.data.menuIds || []
  checkedMenuIds.value = filterLeafMenuIds(allMenuIds)
  dialogTitle.value = '编辑角色'
  dialogVisible.value = true
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

const handleDialogClose = () => {
  checkedMenuIds.value = []
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    form.menuIds = menuTreeRef.value.getCheckedKeys().concat(menuTreeRef.value.getHalfCheckedKeys())
    if (form.roleId) {
      await roleApi.update(form)
    } else {
      await roleApi.add(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该角色?', '提示', { type: 'warning' }).then(async () => {
    await roleApi.delete(row.roleId)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => {
  loadData()
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
