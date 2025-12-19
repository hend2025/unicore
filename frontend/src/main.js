import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import './styles/index.scss'
import { configApi } from '@/api/system'

// 全局组件
import PageCard from '@/components/PageCard.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataTable from '@/components/DataTable.vue'
import FormDialog from '@/components/FormDialog.vue'
import StatusTag from '@/components/StatusTag.vue'

// 指令
import { setupDirectives } from '@/directives'

const app = createApp(App)

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册全局业务组件
app.component('PageCard', PageCard)
app.component('SearchForm', SearchForm)
app.component('DataTable', DataTable)
app.component('FormDialog', FormDialog)
app.component('StatusTag', StatusTag)

// 注册自定义指令
setupDirectives(app)

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })
app.mount('#app')

// 获取系统标题并更新浏览器标签页
configApi.getByKey('system_title').then(res => {
  document.title = res.data?.configValue || '统一门户管理系统'
}).catch(() => {
  document.title = '统一门户管理系统'
})
