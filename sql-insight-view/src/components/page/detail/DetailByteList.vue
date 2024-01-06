<template>
  <div v-for="(item,itemIndex) in group"
       :key="item.name">
    <detail-byte-item  :item="item" :start="calcStart(itemIndex)"
                      :color="color[itemIndex]"></detail-byte-item>
  </div>
</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import {ref} from 'vue'
import DetailByteItem from "~/components/page/detail/DetailByteItem.vue";

let props = defineProps<{ group: Array<InnodbPageItem> }>()

const group = props.group

const calcStart = (index: number) => {
  let total = 0;
  for (let i = 0; i < index; i++) {
    total += group[i].length;
  }
  return total

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
