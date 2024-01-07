<template>
  <el-row>
    <el-col :span="12">
      <RecordHeader></RecordHeader>
    </el-col>
    <el-col :span="12"></el-col>
  </el-row>

</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import {ref} from 'vue'
import DetailByteList from "~/components/page/detail/DetailByteList.vue";
import RecordHeader from "~/components/page/detail/RecordHeader.vue";


const group: Array<InnodbPageItem> = [
  {name: "checkSum", length: 4, desc: ""},
  {name: "lsn", length: 4, desc: ""}
]


</script>

<style>
</style>
