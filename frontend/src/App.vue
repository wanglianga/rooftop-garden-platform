<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="header-left">
        <el-icon class="logo-icon"><Sunny /></el-icon>
        <h1 class="app-title">屋顶菜园认领平台</h1>
      </div>
      <div class="header-right">
        <el-select v-model="currentRole" @change="handleRoleChange" class="role-select">
          <el-option label="居民" value="RESIDENT" />
          <el-option label="物业" value="PROPERTY" />
          <el-option label="园艺师" value="GARDENER" />
          <el-option label="回收员" value="COLLECTOR" />
        </el-select>
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="32" class="user-avatar">{{ currentUser?.name?.charAt(0) || 'U' }}</el-avatar>
            <span class="user-name">{{ currentUser?.name || '未登录' }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container>
      <el-aside width="220px" class="app-aside">
        <el-menu
          :default-active="activeMenu"
          class="side-menu"
          @select="handleMenuSelect"
          background-color="#2c3e50"
          text-color="#b4bcc2"
          active-text-color="#3498db"
        >
          <template v-for="item in menuItems" :key="item.path">
            <el-menu-item :index="item.path">
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.name }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>

      <el-main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import {
  Sunny,
  Grid,
  Document,
  Food,
  DataLine,
  Goods,
  Tools,
  UserFilled,
  Setting,
  Warning,
  ChatDotRound,
  SemiSelect,
  TrendCharts,
  Box
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const currentRole = ref('RESIDENT')
const activeMenu = computed(() => route.path)
const currentUser = computed(() => userStore.currentUser)

const menuItems = computed(() => {
  const role = currentRole.value
  const menus = []

  menus.push({ path: '/plots', name: '菜畦分布', icon: 'Grid' })

  if (role === 'RESIDENT') {
    menus.push({ path: '/my-claims', name: '我的认领', icon: 'Document' })
    menus.push({ path: '/compost-delivery', name: '厨余投放', icon: 'Food' })
    menus.push({ path: '/harvests', name: '采收记录', icon: 'Goods' })
  } else if (role === 'PROPERTY') {
    menus.push({ path: '/claims', name: '认领管理', icon: 'Document' })
    menus.push({ path: '/inspections', name: '巡查记录', icon: 'DataLine' })
    menus.push({ path: '/tool-keys', name: '工具钥匙', icon: 'Tools' })
    menus.push({ path: '/visitors', name: '访客管理', icon: 'UserFilled' })
    menus.push({ path: '/settings', name: '系统设置', icon: 'Setting' })
  } else if (role === 'GARDENER') {
    menus.push({ path: '/soil-reports', name: '土壤检测', icon: 'DataLine' })
    menus.push({ path: '/pest-records', name: '虫害记录', icon: 'Warning' })
    menus.push({ path: '/suggestions', name: '巡园建议', icon: 'ChatDotRound' })
    menus.push({ path: '/seedling-records', name: '补苗记录', icon: 'SemiSelect' })
    menus.push({ path: '/compost-batches', name: '堆肥批次', icon: 'TrendCharts' })
  } else if (role === 'COLLECTOR') {
    menus.push({ path: '/compost-bins', name: '厨余桶', icon: 'Box' })
    menus.push({ path: '/compost-batches', name: '堆肥批次', icon: 'TrendCharts' })
    menus.push({ path: '/deliveries', name: '投放台账', icon: 'Document' })
  }

  return menus
})

const handleRoleChange = (role) => {
  userStore.setRole(role)
  loadUserByRole(role)
  router.push(menuItems.value[0]?.path || '/plots')
}

const loadUserByRole = (role) => {
  const roleMap = {
    RESIDENT: { id: 1, username: 'resident1', name: '张小明', role: 'RESIDENT' },
    PROPERTY: { id: 4, username: 'property', name: '物业管理员', role: 'PROPERTY' },
    GARDENER: { id: 5, username: 'gardener', name: '园艺师老陈', role: 'GARDENER' },
    COLLECTOR: { id: 6, username: 'collector', name: '回收员老赵', role: 'COLLECTOR' }
  }
  userStore.setUser(roleMap[role])
}

const handleMenuSelect = (index) => {
  router.push(index)
}

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    userStore.clearUser()
  }
}

onMounted(() => {
  loadUserByRole(currentRole.value)
})
</script>

<style scoped>
.app-container {
  height: 100vh;
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(90deg, #27ae60, #2ecc71);
  color: white;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 28px;
}

.app-title {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.role-select {
  width: 130px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-name {
  font-size: 14px;
}

.app-aside {
  background-color: #2c3e50;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.side-menu {
  border-right: none;
  height: 100%;
}

.app-main {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
  height: calc(100vh - 60px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
