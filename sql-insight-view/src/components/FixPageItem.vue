<template>
  <div class="page-section" :style="{height:fix?.effect?8*fix.effect+'%':'8%'}"
       @mouseenter="highLight = true" @mouseleave="highLight = false" @click="showDialog">
    <div class="left-section">
      <div v-if="fix.downArrow" class="arrow-container">
        <div class="line"></div>
        <div class="arrow-down"></div>
      </div>
      <div v-if="fix.upArrow" class="arrow-container">
        <div class="arrow-top"></div>
        <div class="line"></div>
      </div>
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

let props = defineProps<{ fix: FixItem }>()

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

.arrow-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-end;
  padding-right: 10px;
}

.line {
  width: 2px;
  height: 100%;
  background-color: #ccc; /* 箭头的线条颜色 */
}

.arrow-down {
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-top: 10px solid #ccc;
}


.arrow-top {
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-bottom: 10px solid #ccc;
}


.right-section {
  border: 1px solid #ccc;
  flex: 9;
  padding: 10px;
}

.object_desc {
  font-weight: bold;
}
</style>
