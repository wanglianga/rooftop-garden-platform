<template>
  <div class="harvest-records-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">采收记录</span>
          <div class="actions">
            <el-button type="primary" @click="showAddDialog = true">
              <el-icon><Plus /></el-icon>
              新增采收
            </el-button>
          </div>
        </div>
      </template>

      <div class="summary">
        <el-row :gutter="16">
          <el-col :span="6">
            <div class="summary-card">
              <el-icon class="summary-icon"><Goods /></el-icon>
              <div>
                <div class="summary-number">{{ totalWeight.toFixed(1) }} kg</div>
                <div class="summary-label">总采收量</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-card self">
              <el-icon class="summary-icon"><User /></el-icon>
              <div>
                <div class="summary-number">{{ selfUseWeight.toFixed(1) }} kg</div>
                <div class="summary-label">自用</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-card donate">
              <el-icon class="summary-icon"><Present /></el-icon>
              <div>
                <div class="summary-number">{{ donationWeight.toFixed(1) }} kg</div>
                <div class="summary-label">捐赠</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-card shared">
              <el-icon class="summary-icon"><Share /></el-icon>
              <div>
                <div class="summary-number">{{ sharedWeight.toFixed(1) }} kg</div>
                <div class="summary-label">分享</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <el-table :data="harvests" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="plotId" label="菜畦ID" width="80" />
        <el-table-column prop="cropName" label="作物名称" width="120" />
        <el-table-column label="重量" width="120">
          <template #default="{ row }">
            {{ row.weight }} {{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="quality" label="品质" width="80" />
        <el-table-column label="分配方式" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="getDistributionType(row.distributionType)">
              {{ getDistributionText(row.distributionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="recipient" label="接收方" show-overflow-tooltip />
        <el-table-column prop="harvestTime" label="采收时间" width="180" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-card>

    <el-dialog v-model="showAddDialog" title="新增采收记录" width="500px">
      <el-form :model="harvestForm" label-width="100px">
        <el-form-item label="菜畦ID">
          <el-input-number v-model="harvestForm.plotId" :min="1" />
        </el-form-item>
        <el-form-item label="作物名称">
          <el-input v-model="harvestForm.cropName" />
        </el-form-item>
        <el-form-item label="重量">
          <el-input-number v-model="harvestForm.weight" :min="0.1" :step="0.5" :precision="1" />
          <span style="margin-left: 8px">{{ harvestForm.unit }}</span>
        </el-form-item>
        <el-form-item label="单位">
          <el-select v-model="harvestForm.unit">
            <el-option label="公斤" value="公斤" />
            <el-option label="斤" value="斤" />
            <el-option label="克" value="克" />
          </el-select>
        </el-form-item>
        <el-form-item label="品质">
          <el-select v-model="harvestForm.quality">
            <el-option label="优" value="优" />
            <el-option label="良" value="良" />
            <el-option label="中" value="中" />
            <el-option label="差" value="差" />
          </el-select>
        </el-form-item>
        <el-form-item label="分配方式">
          <el-select v-model="harvestForm.distributionType">
            <el-option label="自用" value="SELF_USE" />
            <el-option label="捐赠" value="DONATION" />
            <el-option label="分享" value="SHARED" />
            <el-option label="出售" value="SOLD" />
          </el-select>
        </el-form-item>
        <el-form-item label="接收方">
          <el-input v-model="harvestForm.recipient" placeholder="捐赠/分享对象" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="harvestForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addHarvest">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getHarvests, createHarvest } from '@/api/harvest'
import { Plus, Goods, User, Present, Share } from '@element-plus/icons-vue'

const userStore = useUserStore()

const harvests = ref([])
const loading = ref(false)
const showAddDialog = ref(false)
const harvestForm = ref({
  plotId: 1,
  cropName: '',
  weight: 1,
  unit: '公斤',
  quality: '良',
  distributionType: 'SELF_USE',
  recipient: '',
  remark: ''
})

const totalWeight = computed(() => 
  harvests.value.reduce((sum, h) => sum + (parseFloat(h.weight) || 0), 0)
)

const selfUseWeight = computed(() =>
  harvests.value.filter(h => h.distributionType === 'SELF_USE').reduce((sum, h) => sum + (parseFloat(h.weight) || 0), 0)
)

const donationWeight = computed(() =>
  harvests.value.filter(h => h.distributionType === 'DONATION').reduce((sum, h) => sum + (parseFloat(h.weight) || 0), 0)
)

const sharedWeight = computed(() =>
  harvests.value.filter(h => h.distributionType === 'SHARED').reduce((sum, h) => sum + (parseFloat(h.weight) || 0), 0)
)

const loadData = async () => {
  loading.value = true
  try {
    let data
    if (userStore.role === 'RESIDENT') {
      const response = await fetch(`/api/harvests/user/${userStore.currentUser.id}`)
      data = await response.json()
    } else {
      data = await getHarvests()
    }
    harvests.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getDistributionType = (type) => {
  const typeMap = {
    SELF_USE: 'info',
    DONATION: 'success',
    SHARED: 'warning',
    SOLD: 'primary'
  }
  return typeMap[type] || 'info'
}

const getDistributionText = (type) => {
  const textMap = {
    SELF_USE: '自用',
    DONATION: '捐赠',
    SHARED: '分享',
    SOLD: '出售'
  }
  return textMap[type] || type
}

const addHarvest = async () => {
  try {
    await createHarvest({
      ...harvestForm.value,
      userId: userStore.currentUser.id,
      plantingId: 0
    })
    ElMessage.success('添加成功')
    showAddDialog.value = false
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

.summary {
  margin-bottom: 20px;
}

.summary-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.summary-card.self { border-left-color: #67c23a; }
.summary-card.donate { border-left-color: #e6a23c; }
.summary-card.shared { border-left-color: #909399; }

.summary-icon {
  font-size: 28px;
  color: #409eff;
}

.summary-card.self .summary-icon { color: #67c23a; }
.summary-card.donate .summary-icon { color: #e6a23c; }
.summary-card.shared .summary-icon { color: #909399; }

.summary-number {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}

.summary-label {
  font-size: 13px;
  color: #909399;
}
</style>
