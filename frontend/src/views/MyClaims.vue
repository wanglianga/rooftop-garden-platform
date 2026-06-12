<template>
  <div class="my-claims-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">我的认领</span>
          <el-button type="primary" @click="loadData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="claims" v-loading="loading" stripe>
        <el-table-column prop="plotId" label="菜畦ID" width="80" />
        <el-table-column label="认领状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cropType" label="种植品类" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="wateringSchedule" label="浇水安排" show-overflow-tooltip />
        <el-table-column label="厨余投放" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.willingToDonateCompost" type="success">愿意</el-tag>
            <el-tag v-else type="info">暂不</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="harvestPreference" label="采收偏好" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">
              详情
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              v-if="row.status === 'PENDING'"
              @click="cancelClaim(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="认领详情" width="600px">
      <div v-if="currentClaim" class="claim-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="菜畦ID">{{ currentClaim.plotId }}</el-descriptions-item>
          <el-descriptions-item label="认领状态">
            <el-tag :type="getStatusType(currentClaim.status)">
              {{ getStatusText(currentClaim.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="种植品类">{{ currentClaim.cropType }}</el-descriptions-item>
          <el-descriptions-item label="申请日期">{{ currentClaim.claimDate }}</el-descriptions-item>
          <el-descriptions-item label="开始日期">{{ currentClaim.startDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="结束日期">{{ currentClaim.endDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="浇水安排" :span="2">
            {{ currentClaim.wateringSchedule }}
          </el-descriptions-item>
          <el-descriptions-item label="厨余投放意愿">
            {{ currentClaim.willingToDonateCompost ? '愿意' : '暂不' }}
          </el-descriptions-item>
          <el-descriptions-item label="采收偏好">{{ currentClaim.harvestPreference }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ currentClaim.remark || '无' }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="planting-section" v-if="plantings.length > 0">
          <h4>种植记录</h4>
          <el-table :data="plantings" size="small">
            <el-table-column prop="cropName" label="作物名称" />
            <el-table-column prop="cropVariety" label="品种" />
            <el-table-column prop="plantCount" label="种植数量" width="100" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="getPlantingStatusType(row.status)">
                  {{ getPlantingStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="expectedHarvestDate" label="预计采收" width="120" />
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getUserActiveClaims, getClaimsByUserId } from '@/api/claim'
import { getPlantingsByPlotId } from '@/api/planting'
import { Refresh } from '@element-plus/icons-vue'

const userStore = useUserStore()

const claims = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const currentClaim = ref(null)
const plantings = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const data = await getClaimsByUserId(userStore.currentUser.id)
    claims.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    APPROVED: 'success',
    ACTIVE: 'success',
    EXPIRED: 'info',
    CANCELLED: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    PENDING: '待审核',
    APPROVED: '已通过',
    ACTIVE: '进行中',
    EXPIRED: '已到期',
    CANCELLED: '已取消'
  }
  return textMap[status] || status
}

const getPlantingStatusType = (status) => {
  const typeMap = {
    PLANNED: 'info',
    SOWN: 'primary',
    GERMINATED: 'primary',
    GROWING: 'warning',
    FLOWERING: 'warning',
    FRUITING: 'success',
    HARVESTING: 'success',
    COMPLETED: 'info',
    FAILED: 'danger'
  }
  return typeMap[status] || 'info'
}

const getPlantingStatusText = (status) => {
  const textMap = {
    PLANNED: '计划中',
    SOWN: '已播种',
    GERMINATED: '已发芽',
    GROWING: '生长中',
    FLOWERING: '开花中',
    FRUITING: '结果中',
    HARVESTING: '采收中',
    COMPLETED: '已完成',
    FAILED: '失败'
  }
  return textMap[status] || status
}

const viewDetail = async (claim) => {
  currentClaim.value = claim
  detailVisible.value = true
  try {
    const data = await getPlantingsByPlotId(claim.plotId)
    plantings.value = data
  } catch (e) {
    console.error(e)
  }
}

const cancelClaim = async (claim) => {
  try {
    await ElMessageBox.confirm('确定要取消认领申请吗？', '确认', {
      type: 'warning'
    })
    ElMessage.success('已取消认领')
    loadData()
  } catch {
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

.claim-detail {
  padding: 10px 0;
}

.planting-section {
  margin-top: 20px;
}

.planting-section h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #303133;
}
</style>
