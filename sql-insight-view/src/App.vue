<template>
  <n-config-provider :theme="darkTheme">
    <el-config-provider namespace="ep">
      <BaseHeader/>
      <div class="flex main-container">
        <BaseSide/>
        <div w="full" py="4">
          <RouterView></RouterView>
        </div>
      </div>
    </el-config-provider>
  </n-config-provider>
</template>


<style>
#app {
  text-align: center;
  color: var(--ep-text-color-primary);
}

.main-container {
  height: calc(100vh - var(--ep-menu-item-height) - 3px);
}
</style>

<script>
import {defineComponent} from 'vue'
import {darkTheme} from 'naive-ui'
import {RouterView} from 'vue-router'

export default defineComponent({
  setup() {
    return {
      darkTheme, RouterView
    }
  }
})
</script>
