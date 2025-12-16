<template>
  <!-- 有子菜单时渲染为 sub-menu -->
  <el-sub-menu v-if="menu.children && menu.children.length" :index="String(menu.menuId)">
    <template #title>
      <el-icon><component :is="menu.menuIcon || 'Folder'" /></el-icon>
      <span>{{ menu.menuName }}</span>
    </template>
    <!-- 递归渲染子菜单 -->
    <MenuItem v-for="child in menu.children" :key="child.menuId" :menu="child" />
  </el-sub-menu>
  <!-- 没有子菜单时渲染为 menu-item -->
  <el-menu-item v-else :index="menu.menuPath">
    <el-icon><component :is="menu.menuIcon || 'Document'" /></el-icon>
    <span>{{ menu.menuName }}</span>
  </el-menu-item>
</template>

<script setup>
defineProps({
  menu: {
    type: Object,
    required: true
  }
})
</script>
