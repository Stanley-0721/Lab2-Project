import { createRouter, createWebHistory } from 'vue-router'
import Index from '../views/Index.vue'
import CustomerView from '../views/CustomerView.vue'
import KitchenView from '../views/KitchenView.vue'
import WaiterView from '../views/WaiterView.vue'
import ReceptionView from '../views/ReceptionView.vue'
import BossView from '../views/BossView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', component: Index },
    { path: '/customer', component: CustomerView },
    { path: '/kitchen', component: KitchenView },
    { path: '/waiter', component: WaiterView },
    { path: '/reception', component: ReceptionView },
    { path: '/boss', component: BossView }
  ]
})

export default router