<template>
  <div class="data-table-wrapper">
    <div class="table-toolbar" v-if="$slots.toolbar">
      <slot name="toolbar"></slot>
    </div>
    <el-table :data="data" v-loading="loading" border style="width: 100%; flex: 1" height="100%" :cell-style="{ textAlign: 'center' }" :header-cell-style="{ textAlign: 'center' }" :show-overflow-tooltip="true" @sort-change="handleSortChange">
      <slot></slot>
    </el-table>
    <div class="table-footer" v-if="showPagination">
      <span class="total-info">总共{{ total }}条 显示{{ showStart }}-{{ showEnd }}条</span>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        background
        small
        layout="sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  total: { type: Number, default: 0 },
  pageNum: { type: Number, default: 1 },
  pageSize: { type: Number, default: 10 },
  showPagination: { type: Boolean, default: true }
})

const emit = defineEmits(['update:pageNum', 'update:pageSize', 'page-change', 'sort-change'])

const currentPage = computed({
  get: () => props.pageNum,
  set: (val) => emit('update:pageNum', val)
})

const pageSize = computed({
  get: () => props.pageSize,
  set: (val) => emit('update:pageSize', val)
})

const showStart = computed(() => props.total ? (props.pageNum - 1) * props.pageSize + 1 : 0)
const showEnd = computed(() => Math.min(props.pageNum * props.pageSize, props.total))

const handleSizeChange = (val) => {
  emit('update:pageSize', val)
  emit('page-change')
}

const handleCurrentChange = (val) => {
  emit('update:pageNum', val)
  emit('page-change')
}

const handleSortChange = ({ prop, order }) => {
  emit('sort-change', { prop, order })
}
</script>

<style scoped>
.data-table-wrapper { display: flex; flex-direction: column; height: 100%; min-height: 0; overflow: hidden; }
.table-toolbar { margin-bottom: 8px; flex-shrink: 0; }
.table-footer { display: flex; align-items: center; justify-content: space-between; padding: 8px 0 0; flex-shrink: 0; }
.total-info { font-size: 14px; color: #000; }
:deep(.el-table) { flex: 1; min-height: 0; --el-table-border-color: #e8e8e8; }
:deep(.el-table__body-wrapper) { overflow-y: auto; }
:deep(.el-scrollbar__view) { height: auto !important; }
:deep(.el-table th.el-table__cell) { background-color: #f5f5f5; color: #003; font-weight: 700; font-size: 14px; padding: 8px 0; text-align: center; border-right: 1px solid #e8e8e8; border-bottom: 1px solid #e8e8e8; }
:deep(.el-table td.el-table__cell) { padding: 8px 0; font-size: 14px; color: #000; border-right: 1px solid #e8e8e8; border-bottom: 1px solid #e8e8e8; }
:deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) { background-color: #f5faff; }
:deep(.el-table--border) { border: 1px solid #e8e8e8; }
:deep(.el-pagination) { padding: 0; }
:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) { background-color: #409eff; }
:deep(.el-pagination .el-select .el-input) { width: 100px; }
</style>
