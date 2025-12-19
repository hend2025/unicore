<template>
  <!-- 用户下拉菜单 -->
  <el-dropdown @command="handleCommand">
    <span class="user-info">
      <el-avatar :size="28" icon="UserFilled" />
      <span class="username">{{ userInfo.realName || userInfo.userName || '未登录' }}</span>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="changePwd">修改密码</el-dropdown-item>
        <el-dropdown-item command="logout">退出系统</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
defineProps({
  userInfo: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['change-password', 'logout'])

const handleCommand = (command) => {
  if (command === 'logout') {
    emit('logout')
  } else if (command === 'changePwd') {
    emit('change-password')
  }
}
</script>

<style scoped lang="scss">
:deep(.el-dropdown) {
  outline: none;
  
  &:focus-visible {
    outline: none;
  }
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #fff;
  outline: none;
  
  .username {
    margin-left: 8px;
    font-size: 14px;
  }
  
  &:hover {
    opacity: 0.8;
  }
}
</style>
