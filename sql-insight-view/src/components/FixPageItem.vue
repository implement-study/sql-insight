<template>
  <div class="page-section" :style="{height:fix?.effect?8*fix.effect+'%':'8%'}"
       @mouseenter="highLight = true" @mouseleave="highLight = false" @click="showDialog">
    <div class="left-section">
      <div class="top-number">{{ fix.topOffset }}</div>
      <div class="top-number">{{ fix.bottomOffset }}</div>
    </div>
    <div :class="{ 'right-section': true, 'card': highLight }">
      <div class="section-title">{{ fix.name }} {{ fix.length ? '(' + fix.length + `字节)` : '' }}</div>
    </div>
  </div>

</template>

<script lang="ts" setup>
import {type FixItem} from '~/types'
import {ref} from 'vue'

let highLight = ref(false);

let props = defineProps<{ fix?: FixItem }>()

const emit = defineEmits(["routeDialog"]);

const showDialog = () => {
  emit("routeDialog", props.fix);
}

</script>

<style scoped>
.page-section {
  display: flex;
  cursor: pointer;
}

.card {
  background: linear-gradient(45deg, #409EFF, #fad0c4)
}

.left-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-end;
  padding-right: 10px;
}

.right-section {
  border: 1px solid #ccc;
  flex: 9;
  padding: 10px;
}

.section-title {
  font-weight: bold;
}

.top-number {
  font-size: 15px;
}
</style>
