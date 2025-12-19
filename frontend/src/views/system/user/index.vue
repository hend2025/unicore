<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <PageCard title="信息查询">
      <SearchForm v-model="queryParams" @search="loadData" @reset="handleReset">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.userName" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="用户姓名">
          <el-input v-model="queryParams.realName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="医保区划">
          <el-tree-select v-model="queryParams.admdvsCode" :data="admdvsTree" :props="{ label: 'admdvsName', value: 'admdvsCode' }" check-strictly clearable placeholder="请选择医保区划" />
        </el-form-item>
        <el-form-item label="用户状态">
          <el-select v-model="queryParams.stasFlag" placeholder="请选择" clearable>
            <el-option label="正常" value="1" />
            <el-option label="停用" value="0" />
          </el-select>
        </el-form-item>
      </SearchForm>
    </PageCard>

    <!-- 用户列表 -->
    <PageCard title="用户列表" flex>
      <template #extra>
        <el-button type="primary" @click="handleAdd">新增用户</el-button>
      </template>
      <DataTable
        :data="tableData"
        :loading="loading"
        :total="total"
        v-model:pageNum="queryParams.pageNum"
        v-model:pageSize="queryParams.pageSize"
        @page-change="loadData"
        @sort-change="handleSortChange"
      >
        <el-table-column type="index" label="序号" width="60" align="center" fixed="left" />
        <el-table-column prop="userName" label="用户名" min-width="120" show-overflow-tooltip sortable/>
        <el-table-column prop="realName" label="姓名" min-width="120" show-overflow-tooltip sortable />
        <el-table-column prop="admdvsName" label="医保区划" min-width="120" show-overflow-tooltip />
        <el-table-column prop="orgName" label="所属机构" min-width="120" show-overflow-tooltip />
        <el-table-column prop="crteTime" label="创建时间" width="180" show-overflow-tooltip sortable />
        <el-table-column label="操作" width="128" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)" :disabled="row.userName === 'admin'">删除</el-button>
          </template>
        </el-table-column>
      </DataTable>
    </PageCard>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户名" :disabled="!!form.userId" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.userId">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="医保区划" prop="admdvsCode">
          <el-tree-select v-model="form.admdvsCode" :data="admdvsTree" :props="{ label: 'admdvsName', value: 'admdvsCode' }" check-strictly clearable placeholder="请选择医保区划" style="width: 100%" />
        </el-form-item>
        <el-form-item label="所属机构" prop="orgId">
          <el-tree-select v-model="form.orgId" :data="orgTree" :props="{ label: 'orgName', value: 'orgId', children: 'children' }" check-strictly clearable placeholder="请选择所属机构" style="width: 100%" />
        </el-form-item>
        <el-form-item label="地区" prop="areaCode">
          <el-tree-select v-model="form.areaCode" :data="areaTree" :props="{ label: 'areaName', value: 'areaCode' }" check-strictly clearable placeholder="请选择地区" style="width: 100%" />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds" v-if="form.userName !== 'admin'">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option v-for="role in roleList" :key="role.roleId" :label="role.roleName" :value="role.roleId" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="stasFlag">
          <el-radio-group v-model="form.stasFlag" :disabled="form.userName === 'admin'">
            <el-radio label="1">正常</el-radio>
            <el-radio label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="warning" plain v-if="form.userId && form.userName !== 'admin'" @click="handleResetPwd">密码重置</el-button>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageCard from '@/components/PageCard.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataTable from '@/components/DataTable.vue'
import { userApi, roleApi, admdvsApi, orgApi, areaApi } from '@/api/system'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const roleList = ref([])
const admdvsTree = ref([])
const orgTree = ref([])
const areaTree = ref([])
const formRef = ref(null)

const queryParams = reactive({ pageNum: 1, pageSize: 10, userName: '', realName: '', admdvsCode: '', stasFlag: '' })
const form = reactive({ userId: null, userName: '', realName: '', password: '', admdvsCode: '', orgId: null, areaCode: '', roleIds: [], stasFlag: '1' })

const validatePassword = (rule, value, callback) => {
  if (!value) { callback(new Error('请输入密码')); return }
  if (value.length < 8) { callback(new Error('密码长度至少8个字符')); return }
  let complexity = 0
  if (/[0-9]/.test(value)) complexity++
  if (/[a-zA-Z]/.test(value)) complexity++
  if (/[^0-9a-zA-Z]/.test(value)) complexity++
  if (complexity < 3) { callback(new Error('密码必须包含数字、字母、特殊符号')); return }
  callback()
}

const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
  admdvsCode: [{ required: true, message: '请选择医保区划', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await userApi.page(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  loadData()
}

const handleSortChange = ({ prop, order }) => {
  // order: ascending 升序, descending 降序, null 取消排序
  queryParams.orderBy = prop
  queryParams.orderType = order === 'ascending' ? 'asc' : order === 'descending' ? 'desc' : ''
  loadData()
}

const loadRoles = async () => {
  try { const res = await roleApi.list(); if (res?.data) roleList.value = res.data } catch (e) {}
}
const loadAdmdvsTree = async () => {
  try { const res = await admdvsApi.tree(); if (res?.data) admdvsTree.value = res.data } catch (e) {}
}
const loadOrgTree = async () => {
  try { const res = await orgApi.tree(); if (res?.data) orgTree.value = res.data } catch (e) {}
}
const loadAreaTree = async () => {
  try { const res = await areaApi.tree(); if (res?.data) areaTree.value = res.data } catch (e) {}
}

const handleAdd = () => {
  Object.assign(form, { userId: null, userName: '', realName: '', password: '', admdvsCode: '', orgId: null, areaCode: '', roleIds: [], stasFlag: '1' })
  dialogTitle.value = '新增用户'
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  const res = await userApi.get(row.userId)
  Object.assign(form, res.data, { password: '' })
  dialogTitle.value = '编辑用户'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (form.userId) {
      await userApi.update(form)
    } else {
      await userApi.add(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleResetPwd = () => {
  ElMessageBox.confirm('确认将密码重置为 asdfg@1234 ?', '提示', { type: 'warning' }).then(async () => {
    await userApi.resetPwd({ userId: form.userId })
    ElMessage.success('密码重置成功')
  })
}

const handleDelete = (row) => {
  if (row.userName === 'admin') { ElMessage.warning('admin用户不能删除'); return }
  ElMessageBox.confirm('确认删除该用户?', '提示', { type: 'warning' }).then(async () => {
    await userApi.delete(row.userId)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => {
  loadData()
  loadRoles()
  loadAdmdvsTree()
  loadOrgTree()
  loadAreaTree()
})
</script>
