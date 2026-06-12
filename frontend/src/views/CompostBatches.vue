<template>
  <div class="compost-batches-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">堆肥批次管理</span>
          <div class="actions">
            <el-button type="primary" @click="showCreateDialog = true">
              <el-icon><Plus /></el-icon>
              新建批次
            </el-button>
          </div>
        </div>
      </template>

      <div class="batch-stats">
        <el-row :gutter="16">
          <el-col :span="4">
            <div class="stat-card collecting">
              <div class="stat-number">{{ collectingCount }}</div>
              <div class="stat-label">收集中</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="stat-card fermenting">
              <div class="stat-number">{{ fermentingCount }}</div>
              <div class="stat-label">发酵中</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="stat-card maturing">
              <div class="stat-number">{{ maturingCount }}</div>
              <div class="stat-label">熟成中</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="stat-card mature">
              <div class="stat-number">{{ matureCount }}</div>
              <div class="stat-label">已成熟</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="stat-card used">
              <div class="stat-number">{{ usedCount }}</div>
              <div class="stat-label">已使用</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="stat-card sold">
              <div class="stat-number">{{ soldCount }}</div>
              <div class="stat-label">已出售</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <el-table :data="batches" v-loading="loading" stripe>
        <el-table-column prop="batchCode" label="批次编号" width="140" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="initialWeight" label="初始重量(kg)" width="120" />
        <el-table-column prop="finalWeight" label="最终重量(kg)" width="120" />
        <el-table-column prop="materials" label="原料" show-overflow-tooltip />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="expectedMaturityDate" label="预计成熟" width="120" />
        <el-table-column prop="maturityDate" label="成熟日期" width="120" />
        <el-table-column prop="turnCount" label="翻堆次数" width="90" />
        <el-table-column prop="qualityGrade" label="质量等级" width="100" />
        <el-table-column prop="matureDestination" label="去向" show-overflow-tooltip />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
            <el-button 
              v-if="row.status === 'COLLECTING'" 
              size="small" 
              type="primary"
              @click="startFerment(row)"
            >
              开始发酵
            </el-button>
            <el-button 
              v-if="row.status === 'FERMENTING'" 
              size="small" 
              type="warning"
              @click="turnCompost(row)"
            >
              翻堆
            </el-button>
            <el-button 
              v-if="row.status === 'FERMENTING'" 
              size="small" 
              type="success"
              @click="markMature(row)"
            >
              标记成熟
            </el-button>
            <el-button 
              v-if="row.status === 'MATURE'" 
              size="small" 
              type="primary"
              @click="applyToPlot(row)"
            >
              回填菜畦
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreateDialog" title="新建堆肥批次" width="500px">
      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="批次编号">
          <el-input v-model="batchForm.batchCode" />
        </el-form-item>
        <el-form-item label="初始重量">
          <el-input-number v-model="batchForm.initialWeight" :min="0" :step="10" />
          <span style="margin-left: 8px">公斤</span>
        </el-form-item>
        <el-form-item label="原料">
          <el-input v-model="batchForm.materials" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="预计成熟">
          <el-date-picker v-model="batchForm.expectedMaturityDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="createBatch">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getBatches, 
  createBatch as apiCreateBatch,
  startFermentation,
  turnCompost as apiTurnCompost,
  markMature as apiMarkMature,
  applyToPlot as apiApplyToPlot
} from '@/api/compostBatch'
import { Plus } from '@element-plus/icons-vue'

const batches = ref([])
const loading = ref(false)
const showCreateDialog = ref(false)
const batchForm = ref({
  batchCode: '',
  initialWeight: 100,
  materials: '',
  expectedMaturityDate: ''
})

const collectingCount = computed(() => batches.value.filter(b => b.status === 'COLLECTING').length)
const fermentingCount = computed(() => batches.value.filter(b => b.status === 'FERMENTING').length)
const maturingCount = computed(() => batches.value.filter(b => b.status === 'MATURING').length)
const matureCount = computed(() => batches.value.filter(b => b.status === 'MATURE').length)
const usedCount = computed(() => batches.value.filter(b => b.status === 'USED').length)
const soldCount = computed(() => batches.value.filter(b => b.status === 'SOLD').length)

const loadData = async () => {
  loading.value = true
  try {
    const data = await getBatches()
    batches.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const typeMap = {
    COLLECTING: 'info',
    FERMENTING: 'warning',
    MATURING: 'warning',
    MATURE: 'success',
    USED: 'info',
    SOLD: 'primary'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    COLLECTING: '收集中',
    FERMENTING: '发酵中',
    MATURING: '熟成中',
    MATURE: '已成熟',
    USED: '已使用',
    SOLD: '已出售'
  }
  return textMap[status] || status
}

const viewDetail = (row) => {
  ElMessage.info('查看批次详情：' + row.batchCode)
}

const createBatch = async () => {
  try {
    await apiCreateBatch(batchForm.value)
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const startFerment = async (row) => {
  try {
    await ElMessageBox.confirm(`确定开始批次 ${row.batchCode} 的发酵？`, '确认', {
      type: 'warning'
    })
    await startFermentation(row.id)
    ElMessage.success('已开始发酵')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const turnCompost = async (row) => {
  try {
    await apiTurnCompost(row.id)
    ElMessage.success('翻堆成功')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const markMature = async (row) => {
  try {
    await ElMessageBox.confirm(`确定标记批次 ${row.batchCode} 为成熟？`, '确认', {
      type: 'success'
    })
    await apiMarkMature(row.id)
    ElMessage.success('已标记成熟')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const applyToPlot = async (row) => {
  try {
    const { value: plotId } = await ElMessageBox.prompt(
      '请输入要回填的菜畦ID',
      '回填菜畦',
      { inputType: 'number' }
    )
    await apiApplyToPlot(row.id, plotId, '菜畦施肥')
    ElMessage.success('回填成功')
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

.batch-stats {
  margin-bottom: 20px;
}

.stat-card {
  padding: 20px;
  text-align: center;
  border-radius: 8px;
  color: white;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  opacity: 0.9;
}

.stat-card.collecting { background: linear-gradient(135deg, #909399, #606266); }
.stat-card.fermenting { background: linear-gradient(135deg, #e6a23c, #d48806); }
.stat-card.maturing { background: linear-gradient(135deg, #409eff, #66b1ff); }
.stat-card.mature { background: linear-gradient(135deg, #67c23a, #85ce61); }
.stat-card.used { background: linear-gradient(135deg, #909399, #606266); }
.stat-card.sold { background: linear-gradient(135deg, #8e44ad, #9b59b6); }
</style>
