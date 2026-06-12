<template>
  <div class="visitor-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">访客管理</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增访客
          </el-button>
        </div>
      </template>

      <div class="stats">
        <el-statistic title="今日访客" :value="todayCount" />
        <el-statistic title="在场访客" :value="visitingCount" />
      </div>

      <el-table :data="visitors" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="visitorName" label="访客姓名" width="100" />
        <el-table-column prop="visitorPhone" label="电话" width="120" />
        <el-table-column prop="visitPurpose" label="来访目的" show-overflow-tooltip />
        <el-table-column prop="invitedById" label="邀请人ID" width="100" />
        <el-table-column prop="plotNumber" label="相关菜畦" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enterTime" label="进入时间" width="180" />
        <el-table-column prop="leaveTime" label="离开时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'PENDING'"
              size="small" 
              type="success"
              @click="checkIn(row)"
            >
              签到
            </el-button>
            <el-button 
              v-if="row.status === 'VISITING'"
              size="small" 
              type="warning"
              @click="checkOut(row)"
            >
              签退
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showAddDialog" title="新增访客登记" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="访客姓名">
          <el-input v-model="form.visitorName" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.visitorPhone" />
        </el-form-item>
        <el-form-item label="来访目的">
          <el-input v-model="form.visitPurpose" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="邀请人ID">
          <el-input-number v-model="form.invitedById" :min="1" />
        </el-form-item>
        <el-form-item label="相关菜畦">
          <el-input v-model="form.plotNumber" placeholder="例如：A1" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addVisitor">登记</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getVisitors, createVisitor, checkInVisitor, checkOutVisitor } from '@/api/property'
import { Plus } from '@element-plus/icons-vue'

const visitors = ref([])
const loading = ref(false)
const showAddDialog = ref(false)
const form = ref({
  visitorName: '',
  visitorPhone: '',
  visitPurpose: '',
  invitedById: 1,
  plotNumber: '',
  remark: ''
})

const todayCount = computed(() => visitors.value.length)
const visitingCount = computed(() => 
  visitors.value.filter(v => v.status === 'VISITING').length
)

const loadData = async () => {
  loading.value = true
  try {
    const data = await getVisitors()
    visitors.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const typeMap = {
    PENDING: 'info',
    VISITING: 'success',
    LEFT: 'info',
    CANCELLED: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    PENDING: '待进入',
    VISITING: '访问中',
    LEFT: '已离开',
    CANCELLED: '已取消'
  }
  return textMap[status] || status
}

const checkIn = async (row) => {
  try {
    await checkInVisitor(row.id)
    ElMessage.success('签到成功')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const checkOut = async (row) => {
  try {
    await checkOutVisitor(row.id)
    ElMessage.success('签退成功')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const addVisitor = async () => {
  try {
    await createVisitor(form.value)
    ElMessage.success('登记成功')
    showAddDialog.value = false
    form.value = {
      visitorName: '',
      visitorPhone: '',
      visitPurpose: '',
      invitedById: 1,
      plotNumber: '',
      remark: ''
    }
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

.stats {
  display: flex;
  gap: 40px;
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}
</style>
