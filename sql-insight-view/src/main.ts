import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";

// import "~/styles/element/index.scss";

import ElementPlus from "element-plus";
import "element-plus/dist/index.css";


import "~/styles/index.scss";
import "uno.css";


const app = createApp(App);
app.use(ElementPlus);
app.use(router)
app.mount("#app");
