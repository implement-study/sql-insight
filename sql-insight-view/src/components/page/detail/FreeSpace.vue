<template>
  <DetailByteList :group="group" :overview="overview"></DetailByteList>
</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import DetailByteList from "~/components/page/detail/DetailByteList.vue";


const group: Array<InnodbPageItem> = []

const overview = "空闲空间和用户记录相辅相成，当添加一条记录时，会在用户记录内容后增加一段数据，同时将空闲空间向后移动一段"

</script>

<style>
</style>
