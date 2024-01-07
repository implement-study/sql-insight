<template>
  <div class="container">
    <div  v-for="(index,innerIndex) in item.length" :key="innerIndex"
         class="rectangle"
         :class="{ active: active }"
         :style="{ width: `5%`,background:color}"
         @mouseover="active = true"
         @mouseleave="active = false">
      {{ start + innerIndex }}
    </div>
    <n-tag type="success">
      <template #default>
            <span class="tag-font">
              从第{{ start + 1 }} {{ unit }} 到 第 {{ start + item.length }}{{ unit }}是
              <n-button type="warning" size="tiny"
                        @click="buttonShowDetail">
                {{ item.name }}
              </n-button>
            </span>
      </template>
      <template #icon>
        <el-icon>
          <Right/>
        </el-icon>
      </template>
    </n-tag>
  </div>

</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import {ref} from 'vue'

let props = defineProps<{ item: InnodbPageItem, start: number, color: string, groupIndex: number, unit: string }>()

const emit = defineEmits(["descClick"]);

const {item, start, color, groupIndex} = props

const active = ref(false)

const buttonShowDetail = () => {
  emit("descClick", groupIndex);
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
