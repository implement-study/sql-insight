<template>
  <div class="container">
    <template v-for="(item,itemIndex) in group"
              :key="item.name">
          <div v-for="(index,innerIndex) in item.length" :key="innerIndex"
               class="rectangle"
               :class="{ active: itemIndex === activeGroup }"
               :style="{ width: `${100 / 50}%`,background:color[itemIndex]}"
               @mouseover="onMouseOver(itemIndex)"
               @mouseleave="onMouseLeave">
            {{ calculateIndex(itemIndex, innerIndex) }}
          </div>

    </template>
  </div>
</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import {ref} from 'vue'

const activeGroup = ref(-1);

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

const calculateIndex = (itemIndex: number, index: number) => {
  let total = 0;
  for (let i = 0; i < itemIndex; i++) {
    total += group[i].length;
  }
  return total + index;
};

const onMouseOver = (itemIndex: number) => {
  activeGroup.value = itemIndex;
};

const onMouseLeave = () => {
  activeGroup.value = -1;
};


const color = [
  "#FFFFFF",
  "#FF0000",
  "#00FF00",
  "#0000FF",
  "#FFFF00",
  "#800080",
  "#FFA500",
  "#00FFFF",
  "#FFC0CB",
  "#FFD700",
  "#008000",
  "#ADD8E6",
  "#8B0000",
  "#FFFFE0",
  "#E6E6FA",
  "#A52A2A",

]
</script>

<style>
.container {
  width: 100%;
  height: 100px;
  display: flex;
  flex-wrap: nowrap;
  overflow: hidden;
}

.rectangle {
  height: 100%;
  border: 1px solid #cccccc;
  box-sizing: border-box;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: transform 0.3s ease;
  color: black;
}

.active {
  transform: scale(1.5);
}
</style>
