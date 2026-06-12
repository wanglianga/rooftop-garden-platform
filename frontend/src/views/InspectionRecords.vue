<template>
  <div class="inspection-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">巡查记录</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增巡查
          </el-button>
        </div>
      </template>

      <div class="stats">
        <el-row :gutter="16">
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-number">{{ inspections.length }}</div>
              <div class="stat-label">总巡查次数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card normal">
              <div class="stat-number">{{ normalCount }}</div>
              <div class="stat-label">正常</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card abnormal">
              <div class="stat-number">{{ abnormalCount }}</div>
              <div class="stat-label">异常</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card unhandled">
              <div class="stat-number">{{ unhandledCount }}</div>
              <div class="stat-label">待处理</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <el-table :data="inspections" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ getTypeText(row.inspectionType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" width="120" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="结果" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="getResultType(row.result)">
              {{ getResultText(row.result) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.handled ? 'success' : 'warning'">
              {{ row.handled ? '已处理' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="inspectionTime" label="巡查时间" width="160" />
        <el-table-column prop="handledBy" label="处理人" width="100" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
            <el-button 
              v-if="!row.handled"
              size="small" 
              type="primary" 
              @click="handleInspection(row)"
            >
              处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showAddDialog" title="新增巡查记录" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="巡查类型">
          <el-select v-model="form.inspectionType" style="width: 100%">
            <el-option label="防水巡查" value="WATERPROOF" />
            <el-option label="安全巡查" value="SAFETY" />
            <el-option label="工具柜" value="TOOL_CABINET" />
            <el-option label="常规巡查" value="GENERAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.location" placeholder="例如：东侧防水墙" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="检查结果">
          <el-radio-group v-model="form.result">
            <el-radio value="NORMAL">正常</el-radio>
            <el-radio value="ABNORMAL">异常</el-radio>
            <el-radio value="NEED_REPAIR">需维修</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="问题描述" v-if="form.result !== 'NORMAL'">
          <el-input v-model="form.issueDescription" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="处理建议">
          <el-input v-model="form.handlingSuggestion" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="submitInspection">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getInspections, createInspection, handleInspection as apiHandle } from '@/api/property'
import { Plus } from '@element-plus/icons-vue'

const userStore = useUserStore()

const inspections = ref([])
const loading = ref(false)
const showAddDialog = ref(false)
const form = ref({
  inspectionType: 'GENERAL',
  location: '',
  description: '',
  result: 'NORMAL',
  issueDescription: '',
  handlingSuggestion: ''
})

const normalCount = computed(() => 
  inspections.value.filter(i => i.result === 'NORMAL').length
)

const abnormalCount = computed(() => 
  inspections.value.filter(i => i.result !== 'NORMAL').length
)

const unhandledCount = computed(() => 
  inspections.value.filter(i => !i.handled).length
)

const loadData = async () => {
  loading.value = true
  try {
    const data = await getInspections()
    inspections.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getTypeText = (type) => {
  const textMap = {
    WATERPROOF: '防水巡查',
    SAFETY: '安全巡查',
    TOOL_CABINET: '工具柜',
    GENERAL: '常规巡查'
  }
  return textMap[type] || type
}

const getResultType = (result) => {
  const typeMap = {
    NORMAL: 'success',
    ABNORMAL: 'warning',
    NEED_REPAIR: 'danger'
  }
  return typeMap[result] || 'info'
}

const getResultText = (result) => {
  const textMap = {
    NORMAL: '正常',
    ABNORMAL: '异常',
    NEED_REPAIR: '需维修'
  }
  return textMap[result] || result
}

const viewDetail = (row) => {
  ElMessage.info('查看详情：' + row.id)
}

const handleInspection = async (row) => {
  try {
    const { value: remark } = await ElMessageBox.prompt('请输入处理说明', '处理巡查', {
      inputType: 'textarea'
    })
    await apiHandle(row.id, userStore.currentUser.name, remark)
    ElMessage.success('处理成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const submitInspection = async () => {
  try {
    await createInspection({
      ...form.value,
      inspectorId: userStore.currentUser.id
    })
    ElMessage.success('提交成功')
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

.stats {
  margin-bottom: 20px;
}

.stat-card {
  padding: 16px;
  text-align: center;
  background: #f5f7fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.stat-card.normal { border-left-color: #67c23a; }
.stat-card.abnormal { border-left-color: #e6a23c; }
.stat-card.unhandled { border-left-color: #f56c6c; }

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
</style>
