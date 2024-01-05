<template>
  <el-row>
    <el-col :span="10">
      <div class="toolbar">
        <el-tooltip v-for="tool in tools" :key="tool.name"
                    :show-after=1000
                    :content="tool.tooltip" placement="top">
          <el-icon :name="tool.icon" class="icon-with-border">
            <component :is="tool.type"></component>
          </el-icon>
        </el-tooltip>
      </div>
      <el-input
          v-model="textarea"
          :rows="25"
          type="textarea"
          placeholder="Please input"
      />
    </el-col>
    <el-col :span="12" >
      <InnodbPage/>
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import {ref} from 'vue'

const textarea = ref('')

const tools = ref([
  {name: 'tool1', icon: 'el-icon-search', tooltip: 'Tool 1', type: 'Right'},
  {name: 'tool2', icon: 'el-icon-edit', tooltip: 'Tool 2', type: 'Right'},
  {name: 'tool3', icon: 'el-icon-setting', tooltip: 'Tool 3', type: 'Right'},
  // Add more tools as needed...
]);

</script>

<style scoped>
.icon-with-border {
  border: solid #ccc;
  padding: 10px;
}

.toolbar {
  display: flex;
  padding: 0;
  margin: 0;
}
</style>
