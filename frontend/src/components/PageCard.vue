<template>
  <div class="page-card" :class="{ 'page-card--flex': flex }">
    <div class="section-title" v-if="title">
      <i></i>{{ title }}
      <div class="title-extra" v-if="$slots.extra">
        <slot name="extra"></slot>
      </div>
    </div>
    <slot></slot>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: '' },
  flex: { type: Boolean, default: false }
})
</script>

<style scoped lang="scss">
@use "@/styles/variables.scss" as *;

.page-card { 
  background: $bg-white; 
  padding: 8px 12px; 
  border: 1px solid $border-color; 
  border-radius: $border-radius-sm; 
  margin-bottom: 6px; 
}
.page-card:last-child { margin-bottom: 0; }
.page-card--flex { flex: 1; display: flex; flex-direction: column; overflow: hidden; min-height: 0; }
.section-title { 
  display: flex; 
  align-items: center; 
  font-size: 16px; 
  color: $text-primary; 
  font-weight: 700; 
  margin-bottom: 8px; 
}
.section-title i { 
  width: 4px; 
  height: 18px; 
  background: $primary-color; 
  margin-right: 8px; 
  border-radius: $border-radius-sm; 
}
.title-extra { margin-left: auto; }
.page-card--flex .section-title { margin-bottom: 8px; flex-shrink: 0; }
</style>
