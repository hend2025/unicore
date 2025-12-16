<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <div class="query-card">
      <div class="section-title"><i></i>信息查询</div>
      <div class="query-section">
        <el-form :model="queryParams" inline class="query-form" label-width="80px" @submit.prevent>
          <el-form-item label="所属系统">
            <el-select v-model="queryParams.sysId" placeholder="请选择系统" clearable>
              <el-option v-for="item in systemList" :key="item.sysId" :label="item.sysName" :value="item.sysId" />
            </el-select>
          </el-form-item>
          <el-form-item label="菜单名称">
            <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable />
          </el-form-item>
          <el-form-item label="路由名称">
            <el-input v-model="queryParams.menuUrl" placeholder="请输入路由名称" clearable />
          </el-form-item>
          <el-form-item>
            <el-button @click="handleReset">重置</el-button>
            <el-button type="primary" @click="handleQuery">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 菜单列表 -->
    <div class="table-card">
      <div class="table-header">
        <div class="section-title"><i></i>菜单列表</div>
        <div>
          <el-button type="primary" @click="handleAdd()">新增菜单</el-button>
        </div>
      </div>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%; flex: 1" height="100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
        <el-table-column prop="menuName" label="菜单名称" min-width="150" />
        <el-table-column prop="menuUrl" label="路由地址" min-width="220" />
        <el-table-column label="所属系统" min-width="120">
          <template #default="{ row }">
            {{ getSystemName(row.sysId) }}
          </template>
        </el-table-column>
        <el-table-column prop="menuType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.menuType === '1'">目录</el-tag>
            <el-tag v-else-if="row.menuType === '2'" type="success">菜单</el-tag>
            <el-tag v-else type="warning">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="stasFlag" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.stasFlag === '1' ? 'success' : 'danger'">{{ row.stasFlag === '1' ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="80" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 10px; justify-content: flex-end;"
      />
    </div>

    <!-- 对话框 -->
    <FormDialog v-model:show="dialogVisible" :title="dialogTitle" :rules="rules" :modelValue="form" :loading="submitLoading" width="600px" @submit="handleSubmit">
      <el-form-item label="所属系统" prop="sysId">
        <el-select v-model="form.sysId" placeholder="请选择系统" style="width: 100%">
          <el-option v-for="item in systemList" :key="item.sysId" :label="item.sysName" :value="item.sysId" />
        </el-select>
      </el-form-item>
      <el-form-item label="上级菜单" prop="parentId">
        <el-tree-select v-model="form.parentId" :data="menuOptions" :props="{ label: 'menuName', value: 'menuId', children: 'children' }" check-strictly placeholder="选择上级菜单" style="width: 100%" />
      </el-form-item>
      <el-form-item label="菜单类型" prop="menuType">
        <el-radio-group v-model="form.menuType">
          <el-radio label="1">目录</el-radio>
          <el-radio label="2">菜单</el-radio>
          <el-radio label="3">按钮</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="菜单名称" prop="menuName">
        <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
      </el-form-item>
      <el-form-item label="菜单图标" prop="menuIcon" v-if="form.menuType !== '3'">
        <el-input v-model="form.menuIcon" placeholder="请输入图标名称" />
      </el-form-item>
      <el-form-item label="路由地址" prop="menuUrl" v-if="form.menuType !== '3'">
        <el-input v-model="form.menuUrl" placeholder="请输入路由地址" />
      </el-form-item>
      <el-form-item label="组件路径" prop="menuComp" v-if="form.menuType === '2'">
        <el-input v-model="form.menuComp" placeholder="请输入组件路径" />
      </el-form-item>
      <el-form-item label="权限标识" prop="menuPerms" v-if="form.menuType !== '1'">
        <el-input v-model="form.menuPerms" placeholder="请输入权限标识" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import FormDialog from '@/components/FormDialog.vue'
import { menuApi, systemApi } from '@/api/system'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const systemList = ref([])
const menuTreeData = ref([])

const queryParams = reactive({ menuName: '', menuUrl: '', sysId: '', pageNum: 1, pageSize: 10 })
const form = reactive({ menuId: null, parentId: '0', menuName: '', menuType: '1', menuIcon: '', menuUrl: '', menuComp: '', menuPerms: '', orderNum: 0, stasFlag: '1', sysId: '' })
const rules = { 
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  sysId: [{ required: true, message: '请选择所属系统', trigger: 'change' }]
}

const menuOptions = computed(() => [{ menuId: '0', menuName: '主类目', children: menuTreeData.value }])

// 获取系统名称
const getSystemName = (sysId) => {
  const system = systemList.value.find(item => item.sysId === sysId)
  return system ? system.sysName : ''
}

// 加载系统列表
const loadSystemList = async () => {
  try {
    const res = await systemApi.list()
    systemList.value = res.data || []
  } catch (e) {
    // 错误已在request拦截器中处理
  }
}

// 加载菜单树（用于上级菜单选择）
const loadMenuTree = async () => {
  try {
    const res = await menuApi.tree()
    menuTreeData.value = res.data || []
  } catch (e) {
    // 错误已在request拦截器中处理
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await menuApi.page(queryParams)
    tableData.value = res.data?.records || res.data?.list || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.menuName = ''
  queryParams.menuUrl = ''
  queryParams.sysId = ''
  queryParams.pageNum = 1
  loadData()
}

const handleAdd = (parentId = '0') => {
  Object.assign(form, { menuId: null, parentId, menuName: '', menuType: '1', menuIcon: '', menuUrl: '', menuComp: '', menuPerms: '', orderNum: 0, stasFlag: '1', sysId: '' })
  dialogTitle.value = '新增菜单'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogTitle.value = '编辑菜单'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    if (form.menuId) {
      await menuApi.update(form)
    } else {
      await menuApi.add(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
    loadMenuTree()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该菜单?', '提示', { type: 'warning' }).then(async () => {
    await menuApi.delete(row.menuId)
    ElMessage.success('删除成功')
    loadData()
    loadMenuTree()
  })
}

onMounted(() => {
  loadSystemList()
  loadMenuTree()
  loadData()
})
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
