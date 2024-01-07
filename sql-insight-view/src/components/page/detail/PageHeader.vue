<template>
  <DetailByteList :group="group"></DetailByteList>
</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import {ref} from 'vue'
import DetailByteList from "~/components/page/detail/DetailByteList.vue";


const group: Array<InnodbPageItem> = [
  {name: "slotCount", length: 2,detailString:"当前页中有多少个槽，对应PageDirectory"},
  {name: "heapTop", length: 2, detailString: "空闲空间顶部偏移量"},
  {name: "absoluteRecordCount", length: 2, detailString: "",},
  {name: "recordCount", length: 2, detailString: ""},
  {name: "free", length: 2, detailString: ""},
  {name: "garbage", length: 2, detailString: ""},
  {name: "lastInsertOffset", length: 2, detailString: ""},
  {name: "direction", length: 2, detailString: ""},
  {name: "directionCount", length: 2, detailString: ""},
  {name: "maxTransactionId", length: 8, detailString: ""},
  {name: "level", length: 2, detailString: ""},
  {name: "indexId", length: 8, detailString: ""},
  {name: "segLeafPre", length: 2, detailString: ""},
  {name: "segLeaf", length: 8, detailString: ""},
  {name: "segTopPre", length: 2, detailString: ""},
  {name: "segTop", length: 8, detailString: ""},
]


</script>

<style>
</style>
