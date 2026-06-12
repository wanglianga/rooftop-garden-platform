<template>
  <div class="compost-bins-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">厨余桶管理</span>
          <div class="actions">
            <el-button type="primary" @click="showAddDialog = true">
              <el-icon><Plus /></el-icon>
              新增桶
            </el-button>
          </div>
        </div>
      </template>

      <el-row :gutter="16">
        <el-col :span="8" v-for="bin in bins" :key="bin.id">
          <div class="bin-card" :class="`bin-${bin.status.toLowerCase()}`">
            <div class="bin-header">
              <div>
                <span class="bin-code">{{ bin.binCode }}</span>
                <el-tag size="small" :type="getStatusType(bin.status)">
                  {{ getStatusText(bin.status) }}
                </el-tag>
              </div>
              <el-dropdown @command="(cmd) => handleCommand(cmd, bin)">
                <el-icon class="more-icon"><MoreFilled /></el-icon>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">编辑</el-dropdown-item>
                    <el-dropdown-item command="empty" v-if="bin.status !== 'ACTIVE'">清空</el-dropdown-item>
                    <el-dropdown-item command="maintenance">维护</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            <div class="bin-body">
              <div class="bin-location">
                <el-icon><Location /></el-icon>
                {{ bin.location }}
              </div>
              <div class="bin-type">
                <el-icon><Box /></el-icon>
                {{ bin.binType }}
              </div>
              <div class="bin-visual">
                <div class="bin-container">
                  <div 
                    class="bin-fill" 
                    :style="{ height: getFillPercentage(bin) + '%' }"
                  ></div>
                </div>
                <div class="bin-stats">
                  <div class="stat-row">
                    <span class="stat-label">当前</span>
                    <span class="stat-value">{{ bin.currentWeight }} kg</span>
                  </div>
                  <div class="stat-row">
                    <span class="stat-label">容量</span>
                    <span class="stat-value">{{ bin.capacity }} kg</span>
                  </div>
                  <div class="stat-row">
                    <span class="stat-label">占比</span>
                    <span class="stat-value">{{ getFillPercentage(bin) }}%</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="bin-footer">
              <span class="maintain-tip">上次维护：{{ bin.lastMaintenanceDate || '无记录' }}</span>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBins, emptyBin as apiEmptyBin } from '@/api/compost'
import { Plus, Location, Box, MoreFilled } from '@element-plus/icons-vue'

const bins = ref([])
const showAddDialog = ref(false)

const loadData = async () => {
  try {
    const data = await getBins()
    bins.value = data
  } catch (e) {
    console.error(e)
  }
}

const getStatusType = (status) => {
  const typeMap = {
    ACTIVE: 'success',
    FILLING: 'warning',
    FULL: 'danger',
    FERMENTING: 'info',
    MAINTENANCE: 'info',
    INACTIVE: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    ACTIVE: '可用',
    FILLING: '投放中',
    FULL: '已满',
    FERMENTING: '发酵中',
    MAINTENANCE: '维护中',
    INACTIVE: '停用'
  }
  return textMap[status] || status
}

const getFillPercentage = (bin) => {
  if (!bin.capacity || bin.capacity === 0) return 0
  return Math.round((bin.currentWeight / bin.capacity) * 100)
}

const handleCommand = (cmd, bin) => {
  if (cmd === 'edit') {
    ElMessage.info('编辑：' + bin.binCode)
  } else if (cmd === 'empty') {
    handleEmpty(bin)
  } else if (cmd === 'maintenance') {
    ElMessage.info('维护：' + bin.binCode)
  }
}

const handleEmpty = async (bin) => {
  try {
    await ElMessageBox.confirm(`确定清空桶 ${bin.binCode}？`, '确认', {
      type: 'warning'
    })
    await apiEmptyBin(bin.id)
    ElMessage.success('已清空')
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

.bin-card {
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 16px;
  transition: all 0.3s;
}

.bin-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.bin-active { border-color: #67c23a; }
.bin-filling { border-color: #e6a23c; }
.bin-full { border-color: #f56c6c; }

.bin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.bin-active .bin-header { background: #f0f9eb; }
.bin-filling .bin-header { background: #fdf6ec; }
.bin-full .bin-header { background: #fef0f0; }

.bin-code {
  font-weight: 700;
  font-size: 16px;
  margin-right: 8px;
}

.more-icon {
  cursor: pointer;
  font-size: 18px;
  color: #909399;
}

.bin-body {
  padding: 16px;
}

.bin-location, .bin-type {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 13px;
  margin-bottom: 8px;
}

.bin-visual {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-top: 12px;
}

.bin-container {
  width: 60px;
  height: 100px;
  border: 2px solid #dcdfe6;
  border-radius: 6px 6px 10px 10px;
  position: relative;
  background: #fafafa;
  overflow: hidden;
}

.bin-fill {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, #85ce61, #67c23a);
  transition: height 0.5s ease;
}

.bin-stats {
  flex: 1;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  font-size: 13px;
}

.stat-label {
  color: #909399;
}

.stat-value {
  font-weight: 600;
  color: #303133;
}

.bin-footer {
  padding: 8px 16px;
  background: #fafafa;
  border-top: 1px solid #e4e7ed;
}

.maintain-tip {
  font-size: 12px;
  color: #909399;
}
</style>
