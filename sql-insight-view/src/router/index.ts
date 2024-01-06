import {createRouter,createWebHistory} from 'vue-router'
import HelloWorld from "~/components/HelloWorld.vue";
import gaGa from "~/components/GaGa.vue";
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
            path:'/hello',
            component: HelloWorld,
        },
        {
            path:'/gaga',
            component: gaGa,
        },
        {
            path:'/innodbPage',
            component: PageOverview
        }
    ]
})

export default router
