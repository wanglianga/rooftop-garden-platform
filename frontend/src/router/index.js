import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/plots'
  },
  {
    path: '/plots',
    name: 'Plots',
    component: () => import('@/views/PlotMap.vue')
  },
  {
    path: '/my-claims',
    name: 'MyClaims',
    component: () => import('@/views/MyClaims.vue')
  },
  {
    path: '/claims',
    name: 'Claims',
    component: () => import('@/views/ClaimManagement.vue')
  },
  {
    path: '/compost-delivery',
    name: 'CompostDelivery',
    component: () => import('@/views/CompostDelivery.vue')
  },
  {
    path: '/compost-bins',
    name: 'CompostBins',
    component: () => import('@/views/CompostBins.vue')
  },
  {
    path: '/compost-batches',
    name: 'CompostBatches',
    component: () => import('@/views/CompostBatches.vue')
  },
  {
    path: '/deliveries',
    name: 'Deliveries',
    component: () => import('@/views/DeliveryLedger.vue')
  },
  {
    path: '/harvests',
    name: 'Harvests',
    component: () => import('@/views/HarvestRecords.vue')
  },
  {
    path: '/inspections',
    name: 'Inspections',
    component: () => import('@/views/InspectionRecords.vue')
  },
  {
    path: '/tool-keys',
    name: 'ToolKeys',
    component: () => import('@/views/ToolKeys.vue')
  },
  {
    path: '/visitors',
    name: 'Visitors',
    component: () => import('@/views/VisitorManagement.vue')
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/Settings.vue')
  },
  {
    path: '/soil-reports',
    name: 'SoilReports',
    component: () => import('@/views/SoilReports.vue')
  },
  {
    path: '/pest-records',
    name: 'PestRecords',
    component: () => import('@/views/PestRecords.vue')
  },
  {
    path: '/suggestions',
    name: 'Suggestions',
    component: () => import('@/views/GardenSuggestions.vue')
  },
  {
    path: '/seedling-records',
    name: 'SeedlingRecords',
    component: () => import('@/views/SeedlingRecords.vue')
  },
  {
    path: '/plot-detail/:id',
    name: 'PlotDetail',
    component: () => import('@/views/PlotDetail.vue')
  },
  {
    path: '/contamination',
    name: 'Contamination',
    component: () => import('@/views/ContaminationRecords.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
