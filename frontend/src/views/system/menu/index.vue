<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <PageCard title="信息查询">
      <SearchForm v-model="queryParams" @search="handleSearch" @reset="handleReset">
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
      </SearchForm>
    </PageCard>

    <!-- 菜单列表 -->
    <PageCard title="菜单列表" flex>
      <template #extra>
        <el-button type="primary" @click="handleAdd(form)">新增菜单</el-button>
      </template>
      <DataTable
        :data="tableData"
        :loading="loading"
        :total="total"
        v-model:pageNum="queryParams.pageNum"
        v-model:pageSize="queryParams.pageSize"
        @page-change="loadData"
      >
        <el-table-column prop="menuId" label="菜单ID" width="180" show-overflow-tooltip />
        <el-table-column prop="menuName" label="菜单名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="menuUrl" label="路由地址" min-width="220" show-overflow-tooltip />
        <el-table-column label="所属系统" min-width="120" show-overflow-tooltip>
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
            <StatusTag :status="row.stasFlag" />
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="80" show-overflow-tooltip />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row, form)">编辑</el-button>
            <el-button type="danger" link @click="doDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </DataTable>
    </PageCard>

    <!-- 对话框 -->
    <FormDialog v-model:visible="dialogVisible" :title="dialogTitle" :rules="rules" :modelValue="form" :loading="submitLoading" width="600px" @submit="doSubmit">
      <el-form-item label="菜单ID" prop="menuId" v-if="form.menuId">
        <el-input v-model="form.menuId" disabled />
      </el-form-item>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { menuApi, systemApi } from '@/api/system'
import { useCrud } from '@/hooks/useCrud'

// 下拉数据
const systemList = ref([])
const menuTreeData = ref([])

// 表单数据
const form = reactive({ 
  menuId: null, 
  parentId: '0', 
  menuName: '', 
  menuType: '1', 
  menuIcon: '', 
  menuUrl: '', 
  menuComp: '', 
  menuPerms: '', 
  orderNum: 0, 
  stasFlag: '1', 
  sysId: '' 
})

// 表单校验规则
const rules = { 
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  sysId: [{ required: true, message: '请选择所属系统', trigger: 'change' }]
}

// 菜单树选项
const menuOptions = computed(() => [{ menuId: '0', menuName: '主类目', children: menuTreeData.value }])

// 根据 sysId 获取系统名称
const getSystemName = (sysId) => {
  const system = systemList.value.find(item => item.sysId === sysId)
  return system ? system.sysName : ''
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
} = useCrud(menuApi, {
  defaultQuery: { menuName: '', menuUrl: '', sysId: '' },
  defaultForm: { parentId: '0', menuName: '', menuType: '1', menuIcon: '', menuUrl: '', menuComp: '', menuPerms: '', orderNum: 0, stasFlag: '1', sysId: '' },
  rowKey: 'menuId',
  title: '菜单',
  afterSubmit: () => {
    // 提交后刷新菜单树
    loadMenuTree()
  }
})

// 加载系统列表
const loadSystemList = async () => {
  try {
    const res = await systemApi.list()
    systemList.value = res.data || []
  } catch (e) {}
}

// 加载菜单树
const loadMenuTree = async () => {
  try {
    const res = await menuApi.tree()
    menuTreeData.value = res.data || []
  } catch (e) {}
}

// 删除（删除后也刷新菜单树）
const doDelete = (row) => {
  handleDelete(row, () => {
    loadMenuTree()
  })
}

// 表单提交
const doSubmit = () => {
  handleSubmit(form)
}

onMounted(() => {
  loadSystemList()
  loadMenuTree()
})
</script>
