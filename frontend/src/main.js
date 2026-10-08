import { createApp } from 'vue'
import {
  ElDialog,
  ElDrawer,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem,
  ElCollapse,
  ElCollapseItem,
  ElRate,
  ElLoading
} from 'element-plus'
import 'element-plus/dist/index.css'
import App from './CampusApp.vue'
import './campus.css'

const app = createApp(App)
// Register only the UI components this project uses.
for (const component of [ElDialog, ElDrawer, ElDropdown, ElDropdownMenu, ElDropdownItem, ElCollapse, ElCollapseItem, ElRate]) {
  app.component(component.name, component)
}
app.use(ElLoading).mount('#app')
