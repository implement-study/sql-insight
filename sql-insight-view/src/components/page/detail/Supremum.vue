<template>
  <DetailByteList :group="group" :overview="overview"></DetailByteList>
</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import DetailByteList from "~/components/page/detail/DetailByteList.vue";
import RecordHeader from "~/components/page/detail/RecordHeader.vue";


const group: Array<InnodbPageItem> = [
  {name: "recordHeader", length: 5, detailComponent: RecordHeader},
  {name: "body", length: 8, detailString: "内容,Supremum字符串的二进制数组，占8字节"}
]


const overview = "页中最大的数据，属于系统记录，格式和用户记录类似"

</script>

<style>
</style>
