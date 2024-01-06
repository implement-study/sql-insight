<template>
  <DetailByteList :group="group"></DetailByteList>
</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import DetailByteList from "~/components/page/detail/DetailByteList.vue";


const group: Array<InnodbPageItem> = [
  {name: "checkSum", length: 4, desc: "校验和"},
  {name: "offset", length: 4, desc: "偏移量"},
  {name: "pageType", length: 2, desc: "页类型，数据页，索引页，日志页等"},
  {name: "pre", length: 4, desc: "上一页的偏移量"},
  {name: "next", length: 4, desc: "下一页的偏移量"},
  {name: "lsn", length: 8, desc: "事务序列号"},
  {name: "flushLsn", length: 8, desc: "已经刷到磁盘上的事务序列号"},
  {name: "spaceId", length: 4, desc: "所属表空间"}
]




</script>

<style>
</style>
