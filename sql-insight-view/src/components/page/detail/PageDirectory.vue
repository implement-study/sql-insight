<template>
<!--  <DetailByteList :group="group" :overview="overview"></DetailByteList>-->
  <div class="container">
    <div class="left-rectangle">
      <div v-for="i in 5" class="square"></div>
    </div>
    <div class="right-rectangle">
      <div class="infimum-group"></div>
      <div class="group"></div>
      <div class="group"></div>
      <div class="group"></div>
      <div class="group"></div>
  </div>
  </div>

</template>

<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'
import DetailByteList from "~/components/page/detail/DetailByteList.vue";


const group: Array<InnodbPageItem> = [
  {name: "指向Supremum", length: 2, detailString: "永远指向Supremum的固定位置"},
  {name: "用户记录slotN", length: 2, detailString: "指向第N组用户记录的最大位置"},
  {name: "用户记录slot ...", length: 2},
  {name: "用户记录slot2", length: 2, detailString: "指向第二组用户记录的最大位置"},
  {name: "用户记录slot1", length: 2, detailString: "指向第一组用户记录的最大位置"},
  {name: "指向Infimum", length: 2, detailString: "永远指向Infimum的固定位置"},
]

const overview = `一个页中的所有记录是逻辑连续的，如果需要从页中找到一个目标记录需要遍历整条链表，
时间复杂度是O(N),所以Innodb把整个页的所有记录分成多个组，每个组的最大记录放在槽中，这样就可以用二分法查找本页内容,将O(N)优化成O(log2N)
                    `
</script>

<style scoped>
/* 设置整体容器为左右布局 */
.container {
  display: flex;
  height: 100vh; /* 占满整个视窗高度 */
}

.left-rectangle {
  width: 10%; /* 宽度为父元素宽度的80% */
  height: 50%; /* 高度为父元素高度的20% */
  border: 1px solid white; /* 边框为1像素实线黑色 */
  border-radius: 10px; /* 圆角半径为10像素，使边框圆角化 */
  margin-left: 10%;
}

.right-rectangle {
  width: 40%; /* 宽度为父元素宽度的80% */
  height: 50%; /* 高度为父元素高度的20% */
  border: 1px solid white; /* 边框为1像素实线黑色 */
  border-radius: 10px; /* 圆角半径为10像素，使边框圆角化 */
  margin-left: 10%;
  display: flex;
  flex-direction: column;
  padding: 5%;
  align-items: center; /* 水平居中 */
}

.square {
  width: 50px; /* 宽度为50像素 */
  height: 50px; /* 高度为50像素 */
  background-color: #cccccc; /* 正方形颜色为黑色 */
  position: relative; /* 相对定位 */
}

/* 用伪元素在正方形中心生成一个圆点 */
.square::before {
  content: '';
  width: 10px; /* 圆点宽度 */
  height: 10px; /* 圆点高度 */
  border-radius: 50%; /* 将元素设为圆形 */
  background-color: white; /* 圆点颜色为红色 */
  position: absolute; /* 绝对定位 */
  top: 50%; /* 纵向居中 */
  left: 50%; /* 横向居中 */
  transform: translate(-50%, -50%); /* 居中定位 */
}

.infimum-group {
  width: 100%; /* 宽度为30像素 */
  height: 10%; /* 高度为10像素 */
  background-color: #fad0c4; /* 矩形颜色为黑色 */
  border-radius: 10px; /* 圆角半径为10像素，使边框圆角化 */
  margin-bottom: 5%; /* 设置矩形之间的间距为5像素 */
}

.group {
  width: 100%; /* 宽度为30像素 */
  height: 20%; /* 高度为10像素 */
  border-radius: 10px; /* 圆角半径为10像素，使边框圆角化 */
  background-color: #fad0c4; /* 矩形颜色为黑色 */
  margin-bottom: 5%; /* 设置矩形之间的间距为5像素 */
}

</style>
