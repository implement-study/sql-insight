<template>
  <div class="innodb-page">
    <FixPageItem v-for="(item,index) in pageItems" :key="index" :fix=item @route-dialog="routeDialog"></FixPageItem>
  </div>
  <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="70%"
      :before-close="handleClose">
    <component :is="dialogComponent"></component>
  </el-dialog>
</template>

<script lang="ts" setup>
import {type FixItem} from "~/types";
import {ref, markRaw} from 'vue'
import FileHeader from "~/components/page/detail/FileHeader.vue";
import PageHeader from "~/components/page/detail/PageHeader.vue";
import FileTrailer from "~/components/page/detail/FileTrailer.vue";
import Infimum from "~/components/page/detail/Infimum.vue";
import Supremum from "~/components/page/detail/Supremum.vue";
import PageDirectory from "~/components/page/detail/PageDirectory.vue";
import UserRecord from "~/components/page/detail/UserRecord.vue";
import FixPageItem from "~/components/FixPageItem.vue";
import FreeSpace from "~/components/page/detail/FreeSpace.vue";

let dialogVisible = ref(false)
let dialogTitle = ref("")
let dialogComponent = ref({})

const routeDialog = (fixItem: FixItem) => {
  dialogTitle.value = fixItem.name
  dialogComponent.value = fixItem.dialogComponent
  dialogVisible.value = true
}

const handleClose = (done: () => void) => {
  done()
}


const fileHeader: FixItem = {
  name: "文件头 File Header",
  topOffset: 0,
  bottomOffset: 38,
  length: 38,
  dialogComponent: markRaw(FileHeader)
}

const pageHeader: FixItem = {
  name: "页头 Page Header",
  bottomOffset: 38 + 56,
  length: 56,
  dialogComponent: markRaw(PageHeader)
}

const infimum: FixItem = {
  name: "下确界 Infimum",
  bottomOffset: 38 + 56 + 13,
  length: 13,
  dialogComponent: markRaw(Infimum)
}

const supremum: FixItem = {
  name: "上确界 Supremum",
  bottomOffset: 38 + 56 + 13 + 13,
  length: 13,
  dialogComponent: markRaw(Supremum)
}

const userRecord: FixItem = {
  name: "用户记录 User Records",
  effect: 3,
  downArrow: true,
  dialogComponent: markRaw(UserRecord)
}

const pageDirectory: FixItem = {
  name: "页目录 Page Directory",
  effect: 3,
  upArrow: true,
  dialogComponent: markRaw(PageDirectory)
}

const freeSpace: FixItem = {
  name: "空闲空间 free Space",
  topOffset: 'heap top',
  effect: 2,
  dialogComponent: markRaw(FreeSpace)
}

const fileTrailer: FixItem = {
  name: "文件尾 File Trailer",
  topOffset: 1024 * 16 - 8,
  length: 8,
  bottomOffset: 1024 * 16,
  dialogComponent: markRaw(FileTrailer)
}

const pageItems: Array<FixItem> = [
  fileHeader, pageHeader, infimum, supremum, userRecord, freeSpace, pageDirectory, fileTrailer
]
</script>

<style scoped>

html, body {
  height: 100%;
  margin: 0;
}

.innodb-page {
  display: flex;
  flex-direction: column;
  height: 100%;
  border: 2px solid #ccc;
  padding: 10px 5% 0 10px;
  box-shadow: 2px 2px 5px rgba(128, 128, 128, 0.5);
}


</style>
