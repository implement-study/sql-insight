import {createRouter,createWebHistory} from 'vue-router'
import SqlDashboard from "~/components/SqlDashboard.vue";
import PageOverview from "~/views/PageOverview.vue";


const router = createRouter({
    history:createWebHistory(),
    routes:[
        {
            path: '/',
            component: SqlDashboard
        },
        {
            path:'/innodbPage',
            component: PageOverview
        }
    ]
})

export default router
