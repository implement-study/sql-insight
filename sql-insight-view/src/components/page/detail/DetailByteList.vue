<template>
  <el-row>
    <el-col :span="12">
      <div v-for="(item,itemIndex) in group"
           :key="item.name">
        <detail-byte-item :item="item" :start="calcStart(itemIndex)"
                          @desc-click="showDetail"
                          :group-index="itemIndex"
                          :color="color[itemIndex]"></detail-byte-item>
      </div>
    </el-col>
    <el-col :span="12">
      <div v-if="detailIndex>=0 && group[detailIndex].detailComponent ">
        <h2>{{ group[detailIndex].desc }}</h2>
        <component :is="group[detailIndex].detailComponent"></component>
      </div>
      <p v-if="detailIndex>=0 && group[detailIndex].detailString ">
        {{group[detailIndex].detailString }}
      </p>
    </el-col>
  </el-row>

</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import DetailByteItem from "~/components/page/detail/DetailByteItem.vue";
import {ref} from "vue";

let props = defineProps<{ group: Array<InnodbPageItem> }>()

const group = props.group

const detailIndex = ref(-1)

const calcStart = (index: number) => {
  let total = 0;
  for (let i = 0; i < index; i++) {
    total += group[i].length;
  }
  return total
}

const showDetail = (hitIndex: number) => {
  detailIndex.value = hitIndex
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
