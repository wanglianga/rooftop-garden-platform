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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="!row.collected"
              size="small"
              type="primary"
              @click="collectDelivery(row)"
            >
              收集
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="openReportDialog(row)"
            >
              标记污染
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="reportDialogVisible" title="标记厨余污染" width="650px" @close="resetReportForm">
      <el-alert
        v-if="currentDelivery"
        :title="`桶号：${currentDelivery.binId} | 用户ID：${currentDelivery.userId} | 重量：${currentDelivery.weight}kg`"
        type="warning"
        show-icon
        style="margin-bottom: 16px"
      />
      <el-form :model="reportForm" :rules="reportRules" label-width="100px" ref="reportFormRef">
        <el-form-item label="污染类型" prop="contaminationTypes">
          <el-checkbox-group v-model="reportForm.contaminationTypeList">
            <el-checkbox value="塑料袋">塑料袋</el-checkbox>
            <el-checkbox value="油汤">油汤</el-checkbox>
            <el-checkbox value="骨头">骨头</el-checkbox>
            <el-checkbox value="外卖盒">外卖盒</el-checkbox>
            <el-checkbox value="其他">其他</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="污染程度" prop="severityLevel">
          <el-radio-group v-model="reportForm.severityLevel">
            <el-radio value="MILD">轻微</el-radio>
            <el-radio value="MODERATE">中等</el-radio>
            <el-radio value="SEVERE">严重</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="现场照片" prop="photoUrl">
          <el-upload
            class="upload-demo"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :on-change="handlePhotoChange"
            :limit="3"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="tip-text">最多上传3张现场照片（演示环境暂不实际上传，用占位符模拟）</div>
        </el-form-item>
        <el-form-item label="备注说明">
          <el-input
            v-model="reportForm.remark"
            type="textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
            placeholder="请详细描述污染情况，便于园艺师判定处理方式"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="reporting" @click="submitReport">
          <el-icon><Warning /></el-icon>
          确认标记并隔离
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Warning } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getDeliveries, getUncollectedDeliveries, collectDelivery as apiCollect } from '@/api/compost'
import { reportContamination } from '@/api/contamination'

const userStore = useUserStore()

const deliveries = ref([])
const loading = ref(false)
const statusFilter = ref('')
const collecting = ref(false)

const reportDialogVisible = ref(false)
const currentDelivery = ref(null)
const reportFormRef = ref(null)
const reporting = ref(false)
const reportForm = ref({
  contaminationTypeList: [],
  severityLevel: '',
  photoUrl: '',
  remark: ''
})

const reportRules = {
  contaminationTypes: [
    {
      validator: (_r, _v, cb) => {
        if (!reportForm.value.contaminationTypeList || reportForm.value.contaminationTypeList.length === 0) {
          cb(new Error('请至少选择一种污染类型'))
        } else {
          cb()
        }
      },
      trigger: 'change'
    }
  ],
  severityLevel: [{ required: true, message: '请评估污染程度', trigger: 'change' }]
}

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

const openReportDialog = (row) => {
  currentDelivery.value = row
  reportForm.value = {
    contaminationTypeList: [],
    severityLevel: '',
    photoUrl: '',
    remark: ''
  }
  const existingTypes = []
  if (row.hasPlastic) existingTypes.push('塑料袋')
  if (row.hasLiquid) existingTypes.push('油汤')
  if (row.contaminationLevel === '严重') {
    reportForm.value.severityLevel = 'SEVERE'
  } else if (row.contaminationLevel === '轻微') {
    reportForm.value.severityLevel = 'MILD'
  }
  if (existingTypes.length > 0) {
    reportForm.value.contaminationTypeList = existingTypes
  }
  reportDialogVisible.value = true
}

const handlePhotoChange = (file) => {
  reportForm.value.photoUrl = `simulated-photo-${Date.now()}-${file.name}`
}

const resetReportForm = () => {
  currentDelivery.value = null
  reportForm.value = {
    contaminationTypeList: [],
    severityLevel: '',
    photoUrl: '',
    remark: ''
  }
  if (reportFormRef.value) {
    reportFormRef.value.resetFields()
  }
}

const submitReport = async () => {
  if (!reportFormRef.value) return
  try {
    await reportFormRef.value.validate()
  } catch (_e) {
    ElMessage.warning('请完善污染标记必填项')
    return
  }

  try {
    await ElMessageBox.confirm(
      '确认后该桶将被隔离并从正常发酵批次中移除，是否继续？',
      '标记污染确认',
      { type: 'warning', confirmButtonText: '确认标记', cancelButtonText: '取消' }
    )
  } catch (_e) {
    return
  }

  reporting.value = true
  try {
    const payload = {
      binId: currentDelivery.value.binId,
      deliveryId: currentDelivery.value.id,
      userId: currentDelivery.value.userId,
      reporterId: userStore.currentUser.id,
      contaminationTypes: reportForm.value.contaminationTypeList.join('、'),
      severityLevel: reportForm.value.severityLevel,
      photoUrl: reportForm.value.photoUrl || undefined
    }
    await reportContamination(payload)
    ElMessage({
      type: 'success',
      message: '已标记污染并隔离该桶，已通知园艺师进行判定',
      duration: 3000
    })
    reportDialogVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
    const msg = e?.response?.data?.message || e?.message || '标记污染失败'
    ElMessageBox.alert(msg, '操作失败', { type: 'error' })
  } finally {
    reporting.value = false
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

.tip-text {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
}
</style>
