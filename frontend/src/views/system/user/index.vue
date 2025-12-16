<template>
  <div class="page-container">
    <!-- 信息查询 -->
    <div class="query-card">
      <div class="section-title"><i></i>信息查询</div>
      <div class="query-section">
        <el-form :model="queryParams" inline class="query-form" label-width="70px" @submit.prevent>
          <!-- 第一行 - 始终显示 -->
          <el-form-item label="用户名">
            <el-input v-model="queryParams.userName" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="queryParams.realName" placeholder="请输入姓名" clearable />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="queryParams.mobile" placeholder="请输入手机号" clearable />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="queryParams.email" placeholder="请输入邮箱" clearable />
          </el-form-item>
          <!-- 展开时显示更多 -->
          <template v-if="queryExpanded">
            <el-form-item label="证件号码">
              <el-input v-model="queryParams.certNo" placeholder="请输入证件号码" clearable />
            </el-form-item>
            <el-form-item label="证件类型">
              <el-select v-model="queryParams.certType" placeholder="请选择" clearable>
                <el-option label="身份证" value="01" />
                <el-option label="护照" value="02" />
                <el-option label="军官证" value="03" />
                <el-option label="其他" value="99" />
              </el-select>
            </el-form-item>
            <el-form-item label="用户类型">
              <el-select v-model="queryParams.userType" placeholder="请选择" clearable>
                <el-option label="系统用户" value="00" />
                <el-option label="普通用户" value="01" />
              </el-select>
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="queryParams.gend" placeholder="请选择" clearable>
                <el-option label="男" value="1" />
                <el-option label="女" value="2" />
              </el-select>
            </el-form-item>
            <el-form-item label="区划代码">
              <el-input v-model="queryParams.areaCode" placeholder="请输入区划代码" clearable />
            </el-form-item>
            <el-form-item label="医保区划">
              <el-input v-model="queryParams.admdvsCode" placeholder="请输入医保区划" clearable />
            </el-form-item>
            <el-form-item label="地址">
              <el-input v-model="queryParams.addr" placeholder="请输入地址" clearable />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="queryParams.stasFlag" placeholder="请选择" clearable>
                <el-option label="正常" value="1" />
                <el-option label="停用" value="0" />
              </el-select>
            </el-form-item>
          </template>
          <!-- 按钮组 -->
          <el-form-item class="query-btns">
            <el-button link type="primary" @click="queryExpanded = !queryExpanded">
              {{ queryExpanded ? '收起' : '展开' }}<el-icon><component :is="queryExpanded ? 'ArrowUp' : 'ArrowDown'" /></el-icon>
            </el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button type="primary" @click="loadData">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="table-card">
      <div class="table-header">
        <div class="section-title"><i></i>用户列表</div>
        <el-button type="primary" @click="handleAdd">新增用户</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%; flex: 1" height="100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="userName" label="用户名" min-width="100" />
        <el-table-column prop="realName" label="姓名" min-width="80" />
        <el-table-column prop="mobile" label="手机号" min-width="110" />
        <el-table-column prop="orgName" label="所属机构" min-width="150" />
        <el-table-column prop="crteTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="128" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)" :disabled="row.userName === 'admin'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-footer">
        <span class="total-info">总共{{ total }}条 显示{{ showStart }}-{{ showEnd }}条</span>
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          background
          small
          layout="sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

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
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="form.mobile" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { userApi, roleApi } from '@/api/system'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const roleList = ref([])
const queryExpanded = ref(false)
const formRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: '',
  realName: '',
  mobile: '',
  email: '',
  certNo: '',
  certType: '',
  userType: '',
  gend: '',
  areaCode: '',
  admdvsCode: '',
  addr: '',
  stasFlag: ''
})
const form = reactive({ userId: null, userName: '', realName: '', password: '', mobile: '', email: '', roleIds: [], stasFlag: '1' })

// 密码复杂度验证：至少8个字符，包含数字、字母、特殊符号中的至少3种
const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
    return
  }
  if (value.length < 8) {
    callback(new Error('密码长度至少8个字符'))
    return
  }
  let complexity = 0
  if (/[0-9]/.test(value)) complexity++ // 数字
  if (/[a-zA-Z]/.test(value)) complexity++ // 字母
  if (/[^0-9a-zA-Z]/.test(value)) complexity++ // 特殊符号
  if (complexity < 3) {
    callback(new Error('密码必须包含数字、字母、特殊符号'))
    return
  }
  callback()
}

const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, validator: validatePassword, trigger: 'blur' }]
}

const showStart = computed(() => total.value ? (queryParams.pageNum - 1) * queryParams.pageSize + 1 : 0)
const showEnd = computed(() => Math.min(queryParams.pageNum * queryParams.pageSize, total.value))

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
  Object.keys(queryParams).forEach(key => {
    if (key === 'pageNum') queryParams[key] = 1
    else if (key === 'pageSize') queryParams[key] = 10
    else queryParams[key] = ''
  })
  loadData()
}

const loadRoles = async () => {
  const res = await roleApi.list()
  roleList.value = res.data
}

const handleAdd = () => {
  Object.assign(form, { userId: null, userName: '', realName: '', password: '', mobile: '', email: '', roleIds: [], stasFlag: '1' })
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
  if (row.userName === 'admin') {
    ElMessage.warning('admin用户不能删除')
    return
  }
  ElMessageBox.confirm('确认删除该用户?', '提示', { type: 'warning' }).then(async () => {
    await userApi.delete(row.userId)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => {
  loadData()
  loadRoles()
})
</script>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  padding: 10px;
  background: #f5f5f5;
  height: 100%;
  box-sizing: border-box;
  overflow: hidden;
}

.section-title {
  display: flex;
  align-items: center;
  font-size: 16x;
  color: #333;
  font-weight: 700;
  margin-bottom: 8px;
}

.section-title i {
  width: 4px;
  height: 18px;
  background: #0b7ef0ff;
  margin-right: 8px;
  border-radius: 2px;
}

/* 查询卡片 */
.query-card {
  background: #fff;
  padding: 10px 12px;
  margin-bottom: 8px;
  border: 1px solid #eee;
  border-radius: 4px;
}

.query-card .section-title {
  margin-bottom: 8px;
}

/* 查询区域 */
.query-section {
  padding-bottom: 0;
}

.query-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.query-form :deep(.el-form-item) {
  margin-bottom: 8px;
  margin-right: 20px;
  flex: 0 0 calc(25% - 20px);
}

.query-form :deep(.el-form-item__label) {
  padding-right: 8px;
  color: #000;
  font-size: 14px;
  flex-shrink: 0;
}

.query-form :deep(.el-form-item__content) {
  flex: 1;
}

.query-form :deep(.el-input),
.query-form :deep(.el-select) {
  width: 100%;
}

.query-btns {
  flex: 1 1 100% !important;
  display: flex;
  justify-content: flex-end;
  margin-right: 0 !important;
  padding-right: 0;
}

.query-btns :deep(.el-form-item__content) {
  justify-content: flex-end;
}

.query-btns :deep(.el-icon) {
  margin-left: 2px;
}

/* 表格卡片 */
.table-card {
  flex: 1;
  background: #fff;
  padding: 10px 12px;
  border: 1px solid #eee;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.table-header .section-title {
  margin-bottom: 0;
}

:deep(.el-table) {
  margin-top: 8px;
  flex: 1;
  --el-table-border-color: #e8e8e8;
}

:deep(.el-table__body-wrapper) {
  overflow: auto;
}

:deep(.el-table th.el-table__cell) {
  background-color: #f5f5f5;
  color: #003;
  font-weight: 700;
  font-size: 14px;
  padding: 8px 0;
  text-align: center;
  border-right: 1px solid #e8e8e8;
  border-bottom: 1px solid #e8e8e8;
}

:deep(.el-table td.el-table__cell) {
  padding: 8px 0;
  font-size: 14px;
  color: #000;
  border-right: 1px solid #e8e8e8;
  border-bottom: 1px solid #e8e8e8;
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) {
  background-color: #f5faff;
}

:deep(.el-table--border) {
  border: 1px solid #e8e8e8;
}

/* 分页区域 */
.table-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 0;
  margin-top: 6px;
  flex-shrink: 0;
}

.total-info {
  font-size: 14px;
  color: #000;
}

:deep(.el-pagination) {
  padding: 0;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #409eff;
}

:deep(.el-pagination .el-select .el-input) {
  width: 100px;
}
</style>
