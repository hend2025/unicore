<template>
  <div class="login-container">
    <div class="login-content">
      <div class="left-section">
        <h1 class="system-title">医保基金综合监管平台</h1>
        <div class="illustration">
          <img src="@/assets/images/bg-icon.png" alt="系统插画" />
        </div>
      </div>
      <div class="right-section">
        <div class="login-box">
          <h2 class="title">欢迎使用</h2>
          <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password @keyup.enter="handleLogin" />
            </el-form-item>
            <el-form-item>
              <div class="form-options">
                <el-checkbox v-model="form.rememberMe">记住密码</el-checkbox>
                <el-checkbox v-model="form.agreed">
                  我已阅读并同意 <a href="#" class="link">《平台用户协议》</a>
                </el-checkbox>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" :loading="loading" style="width: 100%" @click="handleLogin">登 录</el-button>
            </el-form-item>
          </el-form>
          <div class="copyright">
            Copyright © AltinerEyes 智慧眼科技股份有限公司
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  rememberMe: false,
  agreed: true
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 从 localStorage 读取记住的账号密码
onMounted(() => {
  const remembered = localStorage.getItem('rememberedUser')
  if (remembered) {
    try {
      const { username, password } = JSON.parse(remembered)
      form.username = username || ''
      form.password = password || ''
      form.rememberMe = true
    } catch (e) {
      localStorage.removeItem('rememberedUser')
    }
  }
})

const handleLogin = async () => {
  if (!form.agreed) {
    ElMessage.warning('请先阅读并同意平台用户协议')
    return
  }
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.doLogin({
      username: form.username,
      password: form.password,
      captchaKey: '',
      captchaCode: ''
    })
    // 记住密码
    if (form.rememberMe) {
      localStorage.setItem('rememberedUser', JSON.stringify({
        username: form.username,
        password: form.password
      }))
    } else {
      localStorage.removeItem('rememberedUser')
    }
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  width: 100%;
  height: 100vh;
  background: linear-gradient(180deg, #d4e4f7 0%, #e8f1fa 50%, #f0f5fa 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  position: relative;
}

.login-content {
  width: 100%;
  max-width: 1300px;
  height: 100%;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 0 100px;
  padding-top: 15vh;
  gap: 80px;
}

.left-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .system-title {
    font-size: 36px;
    font-weight: 600;
    color: #333;
    margin-bottom: 40px;
    letter-spacing: 3px;
  }
  
  .illustration {
    width: 100%;
    max-width: 660px;
    
    img {
      width: 100%;
      height: auto;
      display: block;
    }
  }
}

.right-section {
  width: 440px;
  flex-shrink: 0;
  display: flex;
  justify-content: center;
}

.login-box {
  width: 100%;
  padding: 45px 50px 35px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.title {
  text-align: center;
  font-size: 26px;
  font-weight: 500;
  margin-bottom: 45px;
  color: #333;
}

.login-form {
  .el-form-item {
    margin-bottom: 24px;
    
    &:last-of-type {
      margin-bottom: 0;
    }
  }
  
  :deep(.el-input__wrapper) {
    padding: 8px 15px;
    background-color: #fafafa;
    box-shadow: none;
    border: 1px solid #e8e8e8;
    
    &:hover {
      border-color: #d9d9d9;
    }
    
    &.is-focus {
      border-color: #1890ff;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
    }
  }
  
  :deep(.el-input__inner) {
    font-size: 15px;
  }
  
  .form-options {
    display: flex;
    justify-content: space-between;
    width: 100%;
  }
  
  :deep(.el-checkbox) {
    font-size: 14px;
    color: #666;
  }
  
  :deep(.el-button--primary) {
    background-color: #1890ff;
    border-color: #1890ff;
    height: 44px;
    font-size: 16px;
    border-radius: 4px;
    
    &:hover {
      background-color: #40a9ff;
      border-color: #40a9ff;
    }
    
    &:active {
      background-color: #096dd9;
      border-color: #096dd9;
    }
  }
  
  .link {
    color: #1890ff;
    text-decoration: none;
    
    &:hover {
      color: #40a9ff;
    }
  }
}

.copyright {
  text-align: center;
  margin-top: 60px;
  font-size: 12px;
  color: #999;
  line-height: 1.5;
}
</style>
