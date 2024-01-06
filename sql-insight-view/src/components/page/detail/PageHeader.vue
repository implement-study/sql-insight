<template>
  <DetailByteList :group="group"></DetailByteList>
</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import {ref} from 'vue'
import DetailByteList from "~/components/page/detail/DetailByteList.vue";


const group: Array<InnodbPageItem> = [
  {name: "slotCount", length: 2, desc: "槽位"},
  {name: "heapTop", length: 2, desc: "空闲空间顶部"},
  {name: "absoluteRecordCount", length: 2, desc: ""},
  {name: "recordCount", length: 2, desc: ""},
  {name: "free", length: 2, desc: ""},
  {name: "garbage", length: 2, desc: ""},
  {name: "lastInsertOffset", length: 2, desc: ""},
  {name: "direction", length: 2, desc: ""},
  {name: "directionCount", length: 2, desc: ""},
  {name: "maxTransactionId", length: 8, desc: ""},
  {name: "level", length: 2, desc: ""},
  {name: "indexId", length: 8, desc: ""},
  {name: "segLeafPre", length: 2, desc: ""},
  {name: "segLeaf", length: 8, desc: ""},
  {name: "segTopPre", length: 2, desc: ""},
  {name: "segTop", length: 8, desc: ""},
]


</script>

<style>
</style>
