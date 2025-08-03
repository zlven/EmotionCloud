// #ifndef VUE3
import Vue from 'vue'
import App from './App'

// 1. 导入你的全局宠物组件
import GlobalPet from './GlobalPet.vue'
// 2. 使用 Vue.component 将其注册为全局组件
Vue.component('GlobalPet', GlobalPet)

Vue.config.productionTip = false

App.mpType = 'app'

const app = new Vue({
    ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
import App from './App.vue'
import GlobalPet from './GlobalPet.vue'

export function createApp() {
  const app = createSSRApp(App)
  app.component('GlobalPet', GlobalPet) // Vue 3 的注册方式
  return {
    app
  }
}
// #endif