let headerTemplate = `
		<el-menu 
		:default-active="activeIndex" 
		class="el-menu-demo" 
		mode="horizontal" 
		@select="handleSelect"
		background-color="#545c64" 
		text-color="#fff" 
		active-text-color="#ffd04b">
			<el-menu-item index="1">自动部署</el-menu-item>
			<el-menu-item index="2">版本升级工具解析</el-menu-item>
			<el-menu-item index="3">文件解析工具</el-menu-item>
		</el-menu>
	`

Vue.component('common-menu',{
    template:headerTemplate,
    props:{
        activeIndex:{
            type:String
        }
    },
    methods:{
		handleSelect(key, keyPath) {
			location.href = key;
		},
    }
})
