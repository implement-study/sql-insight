<template>
  <DetailByteList :group="group" unit="位" :overview="overview"></DetailByteList>

</template>

<script lang="ts" setup>
import DetailByteList from "~/components/page/detail/DetailByteList.vue";
import type {InnodbPageItem} from "~/types";

const group: Array<InnodbPageItem> = [
  {name: "unuseful", length: 2, detailString: "没用到"},
  {name: "delete_mask", length: 1, detailString: "删除标记"},
  {name: "min_rec_mask", length: 1, detailString: "是否是非叶子节点上的最小记录，只有索引页这里才可能是1"},
  {name: "n_owned", length: 4, detailString: "槽中记录数，只有同组内的最大记录才会有这个字段"},
  {name: "heap_no", length: 13, detailString: "记录在当前页中的序号，Infimum是0,supremum是1，用户记录从2开始"},
  {name: "record_type", length: 3, detailString: "节点类型，普通节点是0，非叶子节点是1,Infimum 是2，Supremum是3"},
  {name: "next_record", length: 16, detailString: "下一条记录的偏移量，可以通过这个字段找到页内下一条用户记录"},
]

const overview = "记录用户记录的元信息"

</script>

<style scoped>

</style>
