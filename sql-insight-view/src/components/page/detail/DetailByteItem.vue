<template>
  <div ref="buttonRef" v-click-outside="onClickOutside" class="container">
    <div v-for="(index,innerIndex) in item.length" :key="innerIndex"
         class="rectangle"
         :class="{ active: active }"
         :style="{ width: `${100 / 50}%`,background:color}"
         @mouseover="onMouseOver"
         @mouseleave="onMouseLeave">
      {{ start + innerIndex }}
    </div>
    <n-tag type="success">
      <template #default>
        <p class="tag-font">
          从第{{ start + 1 }}个字节 到 第 {{ start + item.length }}个字节 是{{ item.desc }}
        </p>
      </template>
      <template #icon>
        <el-icon><Right /></el-icon>
      </template>
    </n-tag>
  </div>
</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import {ref, unref} from 'vue'
import {ClickOutside as vClickOutside} from 'element-plus'


let props = defineProps<{ item: InnodbPageItem, start: number, color: string }>()

const {item, start, color} = props


const active = ref(false)

const onMouseOver = () => {
  active.value = true
};

const onMouseLeave = () => {
  active.value = false
};

const buttonRef = ref()
const onClickOutside = () => {
}

</script>

<style scoped>
.container {
  width: 100%;
  height: 40px;
  display: flex;
  flex-wrap: nowrap;
  overflow: hidden;
  padding: 10px;
}

.rectangle {
  height: 100%;
  border: 1px solid #cccccc;
  box-sizing: border-box;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: transform 0.3s ease;
  color: black;
}

.active {
  transform: scale(1.5);
}

.tag-font {
  font-weight: bold;
}
</style>
