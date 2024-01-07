<template>
  <el-row>
    <el-col :span="16">
      <div v-for="(item,itemIndex) in group"
           :key="item.name">
        <detail-byte-item :item="item" :start="calcStart(itemIndex)"
                          @desc-click="showDetail"
                          :group-index="itemIndex"
                          :unit="unit?unit:'字节'"
                          :color="color[itemIndex]"></detail-byte-item>
      </div>
    </el-col>
    <el-col :span="8">
      <p v-if="descStringOpen">
        {{ group[detailIndex].detailString }}
      </p>
    </el-col>
  </el-row>

  <el-drawer
      v-model="drawerOpen"
      v-if="detailIndex>=0 && group[detailIndex].detailComponent "
      size="80%">
    <template #header>
      <h2 style="color: #cccccc">{{ group[detailIndex].name }}</h2>
    </template>
    <template #default>
      <component :is="group[detailIndex].detailComponent"></component>
    </template>
  </el-drawer>


</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import DetailByteItem from "~/components/page/detail/DetailByteItem.vue";
import {ref} from "vue";

let props = defineProps<{ group: Array<InnodbPageItem>, unit?: string }>()

const group = props.group

const detailIndex = ref(-1)

const calcStart = (index: number) => {
  let total = 0;
  for (let i = 0; i < index; i++) {
    total += group[i].length;
  }
  return total
}

const drawerOpen = ref(false)

const descStringOpen = ref(false)

const showDetail = (hitIndex: number) => {
  detailIndex.value = hitIndex
  if (group[hitIndex].detailComponent) {
    drawerOpen.value = true
  } else if (group[hitIndex].detailString) {
    descStringOpen.value = true
  }
}

const color = [
  "#FFFFFF",
  "#FF0000",
  "#00FF00",
  "#0000FF",
  "#FFFF00",
  "#800080",
  "#FFA500",
  "#00FFFF",
  "#FFC0CB",
  "#FFD700",
  "#008000",
  "#ADD8E6",
  "#8B0000",
  "#FFFFE0",
  "#E6E6FA",
  "#A52A2A",

]
</script>

<style>
</style>
