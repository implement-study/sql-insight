<template>
    <n-list hoverable clickable>
      <n-list-item>
        <n-button color="#8a2be2">
          <template #icon>
            <el-icon><BellFilled /></el-icon>
          </template>
          #8a2be2
        </n-button>
      </n-list-item>
      <n-list-item>
        <n-thing title="他在时间门外" content-style="margin-top: 10px;">
          <template #description>
            <n-space size="small" style="margin-top: 4px">
              <n-tag :bordered="false" type="info" size="small">
                环形公路
              </n-tag>
              <n-tag :bordered="false" type="info" size="small">
                潜水艇司机
              </n-tag>
            </n-space>
          </template>
          最新的打印机<br>
          复制着彩色傀儡<br>
          早上好我的罐头先生<br>
          让他带你去被工厂敲击
        </n-thing>
      </n-list-item>
    </n-list>

</template>


<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}

.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}

.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}

.logo.element-plus:hover {
  filter: drop-shadow(0 0 2em #409effaa);
}
</style>
