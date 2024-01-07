<template>
  <DetailByteList :group="group"></DetailByteList>
</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import DetailByteList from "~/components/page/detail/DetailByteList.vue";
import RecordHeader from "~/components/page/detail/RecordHeader.vue";


const group: Array<InnodbPageItem> = [
  {name: "recordHeader", length: 5, detailComponent: RecordHeader},
  {name: "body", length: 8, detailString: "内容,Supremum字符串的二进制数组，占8字节"}
]


</script>

<style>
</style>
