<script setup>
import {reactive, ref} from 'vue';

let name = ref("张三")
let job = reactive({
  name: "张三",
  age: 15
})
let hobby = ['抽烟', '喝酒', '烫头']

function changeInfo() {
  name.value = "嘎嘎暴力"

  job.name = "张三2"
  job.age = 176
  hobby[0] = '嘎嘎暴力'
}
</script>

<template>
  <div class="greetings">
    <h1 class="green">{{ msg }}</h1>
    <h1 class="green">{{ job.name }}</h1>
    <h1 class="green">{{ job.age }}</h1>
    <h1 v-for="i in hobby" class="green">{{ i }}</h1>
    <h3>
      You’ve successfully created a project with
      <a href="https://vitejs.dev/" target="_blank" rel="noopener">Vite</a> +
      <a href="https://vuejs.org/" target="_blank" rel="noopener">Vue 3</a>.
    </h3>
  </div>
  {{ name }}
  <button @click="changeInfo">修改了</button>
</template>

<style scoped>
h1 {
  font-weight: 500;
  font-size: 2.6rem;
  position: relative;
  top: -10px;
}

h3 {
  font-size: 1.2rem;
}

.greetings h1,
.greetings h3 {
  text-align: center;
}

@media (min-width: 1024px) {
  .greetings h1,
  .greetings h3 {
    text-align: left;
  }
}
</style>
