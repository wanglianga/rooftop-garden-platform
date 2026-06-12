<template>
  <div class="tool-keys-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">工具柜钥匙管理</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增钥匙
          </el-button>
        </div>
      </template>

      <el-row :gutter="16">
        <el-col :span="8" v-for="key in toolKeys" :key="key.id">
          <div class="key-card" :class="`key-${key.status.toLowerCase()}`">
            <div class="key-header">
              <div class="key-icon">
                <el-icon :size="32"><Key /></el-icon>
              </div>
              <div class="key-info">
                <div class="key-code">{{ key.keyCode }}</div>
                <div class="key-cabinet">{{ key.cabinetNumber }}</div>
              </div>
              <el-tag size="small" :type="getStatusType(key.status)">
                {{ getStatusText(key.status) }}
              </el-tag>
            </div>
            <div class="key-desc">{{ key.description }}</div>
            <div class="key-borrow" v-if="key.status === 'BORROWED'">
              <div class="borrow-row">
                <span class="borrow-label">借用人ID:</span>
                <span class="borrow-value">{{ key.currentBorrowerId }}</span>
              </div>
              <div class="borrow-row">
                <span class="borrow-label">借出时间:</span>
                <span class="borrow-value">{{ key.borrowTime }}</span>
              </div>
              <div class="borrow-row">
                <span class="borrow-label">预计归还:</span>
                <span class="borrow-value">{{ key.expectedReturnTime }}</span>
              </div>
            </div>
            <div class="key-actions">
              <el-button 
                v-if="key.status === 'AVAILABLE'"
                type="primary" 
                size="small" 
                @click="borrowKey(key)"
              >
                借出
              </el-button>
              <el-button 
                v-if="key.status === 'BORROWED'"
                type="success" 
                size="small" 
                @click="returnKey(key)"
              >
                归还
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog v-model="showBorrowDialog" title="借出钥匙" width="400px">
      <el-form :model="borrowForm" label-width="80px">
        <el-form-item label="借用人ID">
          <el-input-number v-model="borrowForm.borrowerId" :min="1" />
        </el-form-item>
        <el-form-item label="借用时长">
          <el-input-number v-model="borrowForm.hours" :min="1" :max="168" />
          <span style="margin-left: 8px">小时</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBorrowDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmBorrow">确认借出</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToolKeys, borrowKey as apiBorrow, returnKey as apiReturn } from '@/api/property'
import { Plus, Key } from '@element-plus/icons-vue'

const toolKeys = ref([])
const showAddDialog = ref(false)
const showBorrowDialog = ref(false)
const currentKey = ref(null)
const borrowForm = ref({
  borrowerId: 1,
  hours: 24
})

const loadData = async () => {
  try {
    const data = await getToolKeys()
    toolKeys.value = data
  } catch (e) {
    console.error(e)
  }
}

const getStatusType = (status) => {
  const typeMap = {
    AVAILABLE: 'success',
    BORROWED: 'warning',
    LOST: 'danger',
    DAMAGED: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    AVAILABLE: '可借',
    BORROWED: '已借出',
    LOST: '遗失',
    DAMAGED: '损坏'
  }
  return textMap[status] || status
}

const borrowKey = (key) => {
  currentKey.value = key
  showBorrowDialog.value = true
}

const confirmBorrow = async () => {
  try {
    await apiBorrow(currentKey.value.id, borrowForm.value.borrowerId, borrowForm.value.hours)
    ElMessage.success('借出成功')
    showBorrowDialog.value = false
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const returnKey = async (key) => {
  try {
    await ElMessageBox.confirm(`确认归还钥匙 ${key.keyCode}？`, '确认', {
      type: 'success'
    })
    await apiReturn(key.id)
    ElMessage.success('归还成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
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

.key-card {
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  background: white;
  transition: all 0.3s;
}

.key-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.key-available { border-color: #67c23a; }
.key-borrowed { border-color: #e6a23c; }
.key-lost { border-color: #f56c6c; }
.key-damaged { border-color: #909399; }

.key-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.key-icon {
  color: #409eff;
}

.key-available .key-icon { color: #67c23a; }
.key-borrowed .key-icon { color: #e6a23c; }

.key-info {
  flex: 1;
}

.key-code {
  font-weight: 700;
  font-size: 16px;
  color: #303133;
}

.key-cabinet {
  font-size: 13px;
  color: #909399;
}

.key-desc {
  color: #606266;
  font-size: 13px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #e4e7ed;
}

.key-borrow {
  padding: 10px;
  background: #fdf6ec;
  border-radius: 6px;
  margin-bottom: 12px;
}

.borrow-row {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  padding: 2px 0;
}

.borrow-label {
  color: #909399;
}

.borrow-value {
  color: #606266;
}

.key-actions {
  text-align: right;
}
</style>
