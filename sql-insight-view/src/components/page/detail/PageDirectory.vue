<template>
  <el-row>
    <el-col :span="2" :offset="4">
      <div class="slot-rectangle">
        <div class="slot">
          <div class="slot-circle"></div>
        </div>
      </div>
    </el-col>
    <el-col :span="12" :offset="2">
      <div class="record-rectangle">
        <div class="infimum_group group_item" style="flex-grow: 1;">

        </div>
        <div class="user_record_group group_item" v-for="index in 3" :key="index">

        </div>
        <div class="supremum_group group_item">

        </div>
      </div>
    </el-col>
  </el-row>



</template>
<script lang="ts" setup>
import {type InnodbPageItem} from '~/types'



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
.slot-rectangle {
  width: 100px;
  height: 600px;
  position: relative;
  border-radius: 5px;
  border: 2px solid white;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  display: flex; /* 使用 Flexbox 布局 */
}

.record-rectangle {
  width: 600px;
  height: 600px;
  position: relative;
  display: grid;
  grid-template-rows: auto;
}

.infimum_group{
  flex-grow: 1
}
.user_record_group{
  flex-grow: 2
}

.group_item{
  border-radius: 5px;
  border: 2px solid white;
  margin: 10px;
}


.slot {
  width: 50px; /* 设置正方形的宽度 */
  height: 50px; /* 设置正方形的高度 */
  background-color: teal; /* 设置正方形的背景颜色 */
  position: relative; /* 设置为相对定位，以便内部元素可以使用绝对定位 */
}

.slot-circle {
  width: 5px; /* 设置圆形的直径 */
  height: 5px; /* 设置圆形的直径 */
  background-color: yellow; /* 设置圆形的背景颜色 */
  border-radius: 50%; /* 将正方形的边框半径设置为50%，使其变成圆形 */
  position: absolute; /* 设置为绝对定位，相对于父元素进行定位 */
  top: 50%; /* 将圆形定位到父元素的中间位置 */
  left: 50%; /* 将圆形定位到父元素的中间位置 */
  transform: translate(-50%, -50%); /* 通过平移变换将圆形的中心放置在正方形的中心 */
}
</style>
