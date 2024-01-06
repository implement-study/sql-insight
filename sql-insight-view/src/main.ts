import {createApp} from "vue";
import App from "./App.vue";
import router from "./router";

// import "~/styles/element/index.scss";

import ElementPlus from "element-plus";
import "element-plus/dist/index.css";

import "~/styles/index.scss";
import '~/styles/magic/magic.css'
import "uno.css";

import {
    create,
    NButton,
    NList, NListItem, NConfigProvider, NTag, NSpace, NThing, NPopover
} from 'naive-ui'

const naive = create({
    components: [NButton, NList, NListItem, NConfigProvider, NTag, NSpace, NThing, NPopover]
})
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App);
app.use(ElementPlus);
app.use(router)
app.use(naive)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
app.mount("#app");
