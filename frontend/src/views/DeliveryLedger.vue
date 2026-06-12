<template>
  <div class="delivery-ledger-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">投放台账</span>
          <div class="filters">
            <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 140px" @change="loadData">
              <el-option label="全部" value="" />
              <el-option label="待收集" value="uncollected" />
              <el-option label="已收集" value="collected" />
            </el-select>
          </div>
        </div>
      </template>

      <div class="summary-bar">
        <el-statistic title="总投放次数" :value="deliveries.length" />
        <el-statistic title="总重量(kg)" :value="totalWeight.toFixed(1)" />
        <el-statistic title="待收集" :value="uncollectedCount" />
        <el-statistic title="污染率" :value="contaminationRate + '%'" />
      </div>

      <el-table :data="deliveries" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="binId" label="桶号" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="weight" label="重量(kg)" width="100" />
        <el-table-column prop="foodType" label="厨余类型" show-overflow-tooltip />
        <el-table-column prop="contaminationLevel" label="污染" width="80">
          <template #default="{ row }">
            <el-tag size="small" :type="getContaminationType(row.contaminationLevel)">
              {{ row.contaminationLevel || '无' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="塑料" width="60">
          <template #default="{ row }">
            <el-tag size="small" type="danger" v-if="row.hasPlastic">有</el-tag>
            <el-tag size="small" type="success" v-else>无</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.collected ? 'success' : 'warning'">
              {{ row.collected ? '已收集' : '待收集' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deliveryTime" label="投放时间" width="180" />
        <el-table-column prop="collectTime" label="收集时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="!row.collected"
              size="small" 
              type="primary"
              @click="collectDelivery(row)"
            >
              收集
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getDeliveries, collectDelivery as apiCollect } from '@/api/compost'

const userStore = useUserStore()

const deliveries = ref([])
const loading = ref(false)
const statusFilter = ref('')

const totalWeight = computed(() => 
  deliveries.value.reduce((sum, d) => sum + (parseFloat(d.weight) || 0), 0)
)

const uncollectedCount = computed(() => 
  deliveries.value.filter(d => !d.collected).length
)

const contaminationRate = computed(() => {
  if (deliveries.value.length === 0) return 0
  const contaminated = deliveries.value.filter(d => 
    d.contaminationLevel && d.contaminationLevel !== '无'
  ).length
  return Math.round((contaminated / deliveries.value.length) * 100)
})

const loadData = async () => {
  loading.value = true
  try {
    let data
    if (statusFilter.value === 'uncollected') {
      const response = await fetch('/api/compost/deliveries/uncollected')
      data = await response.json()
    } else {
      data = await getDeliveries()
    }
    deliveries.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getContaminationType = (level) => {
  const typeMap = {
    '无': 'success',
    '轻微': 'warning',
    '严重': 'danger'
  }
  return typeMap[level] || 'info'
}

const collectDelivery = async (row) => {
  try {
    await apiCollect(row.id, userStore.currentUser.id)
    ElMessage.success('已收集')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: 600;
}

.summary-bar {
  display: flex;
  gap: 40px;
  padding: 16px 20px;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #f5f7fa, #e8ecf1);
  border-radius: 8px;
}
</style>
