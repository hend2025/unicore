<template>
  <div class="home-container">
    <div class="welcome-image">
      <img src="@/assets/images/welcome.png" alt="欢迎使用" />
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped lang="scss">
.home-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  
  .welcome-image {
    max-width: 100%;
    max-height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    
    img {
      max-width: 100%;
      max-height: 100%;
      object-fit: contain;
    }
  }
}
</style>
