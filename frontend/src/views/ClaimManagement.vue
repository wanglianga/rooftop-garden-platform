<template>
  <div class="claim-management-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">认领管理</span>
          <div class="filter-bar">
            <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 140px" @change="loadData">
              <el-option label="全部" value="" />
              <el-option label="待审核" value="PENDING" />
              <el-option label="进行中" value="ACTIVE" />
              <el-option label="已到期" value="EXPIRED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table :data="claims" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="plotId" label="菜畦ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cropType" label="种植品类" width="120" />
        <el-table-column prop="claimDate" label="申请日期" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="wateringSchedule" label="浇水安排" show-overflow-tooltip />
        <el-table-column prop="harvestPreference" label="采收偏好" show-overflow-tooltip />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
            <el-button 
              v-if="row.status === 'PENDING'" 
              size="small" 
              type="success" 
              @click="approve(row)"
            >
              通过
            </el-button>
            <el-button 
              v-if="row.status === 'PENDING'" 
              size="small" 
              type="danger" 
              @click="reject(row)"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getClaims, approveClaim, rejectClaim } from '@/api/claim'

const claims = ref([])
const loading = ref(false)
const statusFilter = ref('')

const loadData = async () => {
  loading.value = true
  try {
    let data
    if (statusFilter.value) {
      const response = await fetch(`/api/claims/status/${statusFilter.value}`)
      data = await response.json()
    } else {
      data = await getClaims()
    }
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

const viewDetail = (row) => {
  ElMessage.info('查看详情：' + row.id)
}

const approve = async (row) => {
  try {
    await ElMessageBox.confirm('确定通过此认领申请？', '确认', {
      type: 'warning'
    })
    await approveClaim(row.id)
    ElMessage.success('已通过')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const reject = async (row) => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝申请', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'textarea'
    })
    await rejectClaim(row.id, reason)
    ElMessage.success('已拒绝')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
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

.filter-bar {
  display: flex;
  gap: 10px;
}
</style>
