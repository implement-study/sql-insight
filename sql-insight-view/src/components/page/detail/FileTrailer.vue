<template>
  <DetailByteList :group="group"></DetailByteList>
</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import {ref} from 'vue'
import DetailByteList from "~/components/page/detail/DetailByteList.vue";


const group: Array<InnodbPageItem> = [
  {name: "checkSum", length: 4, detailString: "校验和"},
  {name: "lsn", length: 4, detailString: "校验和"}
]


</script>

<style>
</style>
