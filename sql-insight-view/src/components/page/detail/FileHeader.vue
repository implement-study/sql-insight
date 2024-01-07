<template>
  <DetailByteList :group="group"></DetailByteList>
</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import {ref} from 'vue'
import DetailByteList from "~/components/page/detail/DetailByteList.vue";


const group: Array<InnodbPageItem> = [
  {name: "checkSum", length: 4, detailString: "校验和，用于保证数据一致性"},
  {name: "offset", length: 4, detailString: "页在文件中的偏移量"},
  {name: "pageType", length: 2, detailString: "页类型，包括数据页，索引页，日志页等"},
  {name: "pre", length: 4, detailString: "上一页的偏移量,链表逻辑连续的关键字段"},
  {name: "next", length: 4, detailString: "下一页的偏移量,链表逻辑连续的关键字段"},
  {name: "lsn", length: 8, detailString: "事务序列号"},
  {name: "flushLsn", length: 8, detailString: "已经刷到磁盘上的事务序列号"},
  {name: "spaceId", length: 4, detailString: "所属表空间,高版本MySQl已经没有这个字段了"}
]


</script>

<style>
</style>
