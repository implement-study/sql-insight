<template>
  <DetailByteList :group="group" :overview="overview"></DetailByteList>
</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import {ref} from 'vue'
import DetailByteList from "~/components/page/detail/DetailByteList.vue";


const group: Array<InnodbPageItem> = [
  {name: "第一条用户记录", length: 7},
  {name: "第二条用户记录", length: 14},
  {name: "第三条用户记录", length: 13},
  {name: "第四条用户记录", length: 20},
  {name: "第N条用户记录", length: 10},
]

const overview = "用户记录，按照一定行格式进行编码，行格式几乎都是以Compact行格式为基础,以下是举个例子"

</script>

<style>
</style>
