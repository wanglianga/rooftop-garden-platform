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
        <el-table-column prop="binId" label="桶号" width="70" />
        <el-table-column prop="userId" label="用户ID" width="70" />
        <el-table-column prop="weight" label="重量(kg)" width="90" />
        <el-table-column prop="foodType" label="厨余类型" min-width="140" show-overflow-tooltip />
        <el-table-column prop="contaminationLevel" label="污染" width="70">
          <template #default="{ row }">
            <el-tag size="small" :type="getContaminationType(row.contaminationLevel)">
              {{ row.contaminationLevel || '无' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="塑料" width="60" align="center">
          <template #default="{ row }">
            <el-tag size="small" type="danger" v-if="row.hasPlastic">有</el-tag>
            <el-tag size="small" type="success" v-else>无</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="液体" width="60" align="center">
          <template #default="{ row }">
            <el-tag size="small" type="warning" v-if="row.hasLiquid">有</el-tag>
            <el-tag size="small" type="success" v-else>无</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="row.collected ? 'success' : 'warning'">
              {{ row.collected ? '已收集' : '待收集' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.remark">{{ row.remark }}</span>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="deliveryTime" label="投放时间" width="170" />
        <el-table-column prop="collectTime" label="收集时间" width="170">
          <template #default="{ row }">
            <span v-if="row.collectTime">{{ row.collectTime }}</span>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getDeliveries, getUncollectedDeliveries, collectDelivery as apiCollect } from '@/api/compost'

const userStore = useUserStore()

const deliveries = ref([])
const loading = ref(false)
const statusFilter = ref('')
const collecting = ref(false)

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
      data = await getUncollectedDeliveries()
    } else {
      data = await getDeliveries()
    }
    deliveries.value = data
  } catch (e) {
    console.error(e)
    const msg = e?.response?.data?.message || e?.message || '加载投放台账失败'
    ElMessage.error(msg)
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
    await ElMessageBox.confirm(
      `确认已收集桶 ${row.binId} 的 ${row.weight} kg 厨余？`,
      '收集确认',
      { type: 'info', confirmButtonText: '确认收集', cancelButtonText: '取消' }
    )
  } catch (_e) {
    return
  }

  collecting.value = true
  try {
    await apiCollect(row.id, userStore.currentUser.id)
    ElMessage({ type: 'success', message: `已收集 ${row.weight} kg 厨余`, duration: 2000 })
    loadData()
  } catch (e) {
    console.error(e)
    const msg = e?.response?.data?.message || e?.message || '收集失败，请稍后重试'
    ElMessageBox.alert(msg, '收集失败', { type: 'error' })
  } finally {
    collecting.value = false
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
