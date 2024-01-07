<template>
  <DetailByteList :group="group" :overview="overview"></DetailByteList>
</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import {ref} from 'vue'
import DetailByteList from "~/components/page/detail/DetailByteList.vue";


const group: Array<InnodbPageItem> = [
  {name: "slotCount", length: 2,detailString:"当前页中有多少个槽，对应PageDirectory"},
  {name: "heapTop", length: 2, detailString: "空闲空间顶部偏移量"},
  {name: "absoluteRecordCount", length: 2, detailString: "页中有多少个用户记录，包括infimum,supremum和被删除的记录",},
  {name: "recordCount", length: 2, detailString: "页中有多少真实用户记录，不包括infimum,supremum，被删除的记录"},
  {name: "free", length: 2, detailString: "第一个被删除的记录偏移量，通过这个字段可以找到被删除的链表"},
  {name: "garbage", length: 2, detailString: "被删除的记录有占用了多少空间"},
  {name: "lastInsertOffset", length: 2, detailString: "最后一条插入数据的偏移量"},
  {name: "direction", length: 2, detailString: "最后一条插入的方向，和directionCount一起判断页分裂策略，提高空间利用率"},
  {name: "directionCount", length: 2, detailString: "连续方向的插入数量，和direction一起判断页分裂策略，提高空间利用率"},
  {name: "maxTransactionId", length: 8, detailString: "事务id"},
  {name: "level", length: 2, detailString: "此页在整个B+树的层级，叶子节点是0"},
  {name: "indexId", length: 8, detailString: "所属的索引id"},
  {name: "segLeaf", length: 10, detailString: "所属段信息，只有root节点才有"},
  {name: "segTop", length: 10, detailString: "所属段信息，只有root节点才有"},
]


const overview = "记录着可能会经常改变的页信息"

</script>

<style>
</style>
