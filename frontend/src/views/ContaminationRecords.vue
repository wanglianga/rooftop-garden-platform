<template>
  <div class="contamination-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">厨余污染处理中心</span>
          <div class="header-actions">
            <el-tag v-if="isResident && userPaused" type="danger" effect="dark">
              <el-icon><WarningFilled /></el-icon>
              您的绿色积分当前已暂停增长
            </el-tag>
            <el-tag v-if="isResident" type="success">
              绿色积分：{{ myPoints }}
            </el-tag>
            <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 160px; margin-left: 12px" @change="loadData">
              <el-option label="全部状态" value="" />
              <el-option label="已报告待处理" value="ISOLATED" />
              <el-option label="待园艺师判定" value="PENDING_DISPOSAL" />
              <el-option label="已处理待积分" value="DISPOSED" />
              <el-option label="积分已暂停" value="POINTS_PAUSED" />
              <el-option label="已恢复" value="RECOVERED" />
            </el-select>
          </div>
        </div>
      </template>

      <div class="summary-bar">
        <el-statistic title="待分配园艺师" :value="counts.ISOLATED" value-style="color: #e6a23c">
          <template #prefix>
            <el-icon><Document /></el-icon>
          </template>
        </el-statistic>
        <el-statistic title="待园艺师处理" :value="counts.PENDING_DISPOSAL" value-style="color: #f56c6c">
          <template #prefix>
            <el-icon><Tools /></el-icon>
          </template>
        </el-statistic>
        <el-statistic title="待物业积分处理" :value="counts.DISPOSED" value-style="color: #409eff">
          <template #prefix>
            <el-icon><Coin /></el-icon>
          </template>
        </el-statistic>
        <el-statistic title="累计污染记录" :value="records.length" value-style="color: #909399">
          <template #prefix>
            <el-icon><DataLine /></el-icon>
          </template>
        </el-statistic>
      </div>

      <el-table :data="records" v-loading="loading" stripe @row-click="openDetail" style="cursor: pointer">
        <el-table-column prop="id" label="编号" width="70">
          <template #default="{ row }">
            <span class="record-id">#{{ row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column label="污染来源" width="80">
          <template #default="{ row }">
            <span>用户{{ row.userId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="关联桶" width="80">
          <template #default="{ row }">
            <el-tag size="small" type="info">桶{{ row.binId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="污染类型" min-width="150">
          <template #default="{ row }">
            <div class="tag-list">
              <el-tag
                v-for="(t, i) in (row.contaminationTypes || '').split('、')"
                :key="i"
                size="small"
                type="danger"
                effect="light"
                style="margin-right: 4px; margin-bottom: 2px"
              >
                {{ t }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="严重程度" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="getSeverityType(row.severityLevel)">
              {{ getSeverityText(row.severityLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理状态" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="getStatusType(row.status)" effect="dark">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理方式" width="110">
          <template #default="{ row }">
            <span v-if="row.disposalDecision">{{ getDecisionText(row.disposalDecision) }}</span>
            <span v-else style="color: #c0c4cc">待判定</span>
          </template>
        </el-table-column>
        <el-table-column label="扣分" width="70" align="center">
          <template #default="{ row }">
            <span v-if="row.pointsDeducted && row.pointsDeducted > 0" class="deducted-points">
              -{{ row.pointsDeducted }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="堆肥结果" width="110">
          <template #default="{ row }">
            <el-tag size="small" :type="getResultType(row.compostFinalResult)">
              {{ getResultText(row.compostFinalResult) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reportTime" label="报告时间" width="170" />
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="污染记录详情" width="850px" top="5vh">
      <div v-if="selectedRecord" class="detail-content">
        <el-steps :active="getStepIndex(selectedRecord.status)" align-center style="margin-bottom: 24px">
          <el-step title="报告污染" description="回收员标记" />
          <el-step title="园艺师判定" description="处理方式" />
          <el-step title="物业处理" description="积分暂停" />
          <el-step title="恢复" description="积分恢复" />
        </el-steps>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-descriptions title="污染基本信息" :column="1" border size="small">
              <el-descriptions-item label="记录编号">#{{ selectedRecord.id }}</el-descriptions-item>
              <el-descriptions-item label="污染住户">用户 {{ selectedRecord.userId }}</el-descriptions-item>
              <el-descriptions-item label="报告回收员">用户 {{ selectedRecord.reporterId }}</el-descriptions-item>
              <el-descriptions-item label="关联桶号">桶 {{ selectedRecord.binId }}</el-descriptions-item>
              <el-descriptions-item label="关联批次">
                <span v-if="selectedRecord.batchId">批次 {{ selectedRecord.batchId }}</span>
                <span v-else style="color: #c0c4cc">未关联</span>
              </el-descriptions-item>
              <el-descriptions-item label="严重程度">
                <el-tag size="small" :type="getSeverityType(selectedRecord.severityLevel)">
                  {{ getSeverityText(selectedRecord.severityLevel) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="当前状态">
                <el-tag size="small" :type="getStatusType(selectedRecord.status)" effect="dark">
                  {{ getStatusText(selectedRecord.status) }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
          <el-col :span="12">
            <el-descriptions title="污染详情" :column="1" border size="small">
              <el-descriptions-item label="污染类型">
                <div class="tag-list">
                  <el-tag
                    v-for="(t, i) in (selectedRecord.contaminationTypes || '').split('、')"
                    :key="i"
                    size="small"
                    type="danger"
                    style="margin-right: 4px; margin-bottom: 2px"
                  >
                    {{ t }}
                  </el-tag>
                </div>
              </el-descriptions-item>
              <el-descriptions-item label="现场照片">
                <div v-if="selectedRecord.photoUrl" class="photo-placeholder">
                  <el-icon size="32" color="#409eff"><Picture /></el-icon>
                  <span style="margin-left: 8px; font-size: 12px">已上传现场照片</span>
                </div>
                <span v-else style="color: #c0c4cc">未上传</span>
              </el-descriptions-item>
              <el-descriptions-item label="报告时间">{{ selectedRecord.reportTime }}</el-descriptions-item>
              <el-descriptions-item label="复核园艺师">
                <span v-if="selectedRecord.gardenerId">用户 {{ selectedRecord.gardenerId }}</span>
                <span v-else style="color: #c0c4cc">未分配</span>
              </el-descriptions-item>
              <el-descriptions-item label="处理时间">
                <span v-if="selectedRecord.disposalTime">{{ selectedRecord.disposalTime }}</span>
                <span v-else style="color: #c0c4cc">未处理</span>
              </el-descriptions-item>
              <el-descriptions-item label="物业复核人">
                <span v-if="selectedRecord.propertyApproverId">用户 {{ selectedRecord.propertyApproverId }}</span>
                <span v-else style="color: #c0c4cc">未处理</span>
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
        </el-row>

        <el-card v-if="selectedRecord.disposalDecision" class="section-card" shadow="never">
          <template #header>
            <div class="section-title">
              <el-icon type="success"><CircleCheck /></el-icon>
              <span>园艺师判定结果</span>
            </div>
          </template>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="处理方式">
              <el-tag type="primary" effect="dark">{{ getDecisionText(selectedRecord.disposalDecision) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="判定人">用户 {{ selectedRecord.gardenerId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="延长发酵" span="1">
              <span v-if="selectedRecord.extendedDays">{{ selectedRecord.extendedDays }} 天</span>
              <span v-else>不适用</span>
            </el-descriptions-item>
            <el-descriptions-item label="堆肥最终结果">
              <el-tag :type="getResultType(selectedRecord.compostFinalResult)">
                {{ getResultText(selectedRecord.compostFinalResult) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="处理说明" :span="2">
              {{ selectedRecord.disposalRemark || '无' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card v-if="selectedRecord.status === 'POINTS_PAUSED' || selectedRecord.status === 'RECOVERED'" class="section-card warning-card" shadow="never">
          <template #header>
            <div class="section-title warning">
              <el-icon><Warning /></el-icon>
              <span>绿色积分处理</span>
            </div>
          </template>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="扣分处理">
              <span v-if="selectedRecord.pointsDeducted && selectedRecord.pointsDeducted > 0" class="deducted-points big">
                -{{ selectedRecord.pointsDeducted }} 分
              </span>
              <span v-else style="color: #67c23a">未扣分</span>
            </el-descriptions-item>
            <el-descriptions-item label="处理状态">
              <el-tag :type="selectedRecord.status === 'RECOVERED' ? 'success' : 'danger'" effect="dark">
                {{ selectedRecord.status === 'RECOVERED' ? '已恢复' : '积分暂停中' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="暂停原因" :span="2">
              {{ selectedRecord.propertyRemark || '因厨余污染被暂停积分增长' }}
            </el-descriptions-item>
            <el-descriptions-item label="恢复条件" :span="2">
              <span v-if="selectedRecord.recoveryConditions">{{ selectedRecord.recoveryConditions }}</span>
              <span v-else style="color: #c0c4cc">未指定</span>
            </el-descriptions-item>
            <el-descriptions-item label="恢复时间" v-if="selectedRecord.recoveryTime">
              {{ selectedRecord.recoveryTime }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <div v-if="(isCollector || isProperty) && selectedRecord.status === 'ISOLATED'" class="action-section">
          <el-divider />
          <h4 class="action-title">分配园艺师处理</h4>
          <el-form :model="assignForm" label-width="100px">
            <el-form-item label="选择园艺师">
              <el-select v-model="assignForm.gardenerId" placeholder="请选择负责的园艺师" style="width: 300px">
                <el-option label="园艺师老陈 (ID:5)" :value="5" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="assigning" @click="handleAssign">
                <el-icon><User /></el-icon>
                分配并开始处理
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <div v-if="isGardener && selectedRecord.status === 'PENDING_DISPOSAL'" class="action-section">
          <el-divider />
          <h4 class="action-title">园艺师处理判定</h4>
          <el-alert
            title="请根据污染程度选择合适的处理方式，该决定将影响整批堆肥的后续使用"
            type="warning"
            show-icon
            style="margin-bottom: 16px"
          />
          <el-form :model="disposeForm" :rules="disposeRules" label-width="120px" ref="disposeFormRef">
            <el-form-item label="处理方式" prop="decision">
              <el-radio-group v-model="disposeForm.decision">
                <el-radio value="SCRAP_WHOLE">
                  <b>整桶报废</b> - 污染严重无法使用，全部销毁
                </el-radio><br/>
                <el-radio value="PARTIAL_PICK">
                  <b>局部挑拣</b> - 挑出污染部分，其余继续发酵
                </el-radio><br/>
                <el-radio value="EXTEND_FERMENTATION">
                  <b>延长发酵</b> - 通过延长时间降解污染物质
                </el-radio><br/>
                <el-radio value="NORMAL_USE">
                  <b>正常使用</b> - 确认无污染或不影响发酵
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="延长天数" v-if="disposeForm.decision === 'EXTEND_FERMENTATION'">
              <el-input-number v-model="disposeForm.extendedDays" :min="1" :max="60" />
              <span class="unit">天</span>
            </el-form-item>
            <el-form-item label="堆肥最终结果" prop="finalResult">
              <el-radio-group v-model="disposeForm.finalResult">
                <el-radio value="BACKFILL_AVAILABLE">可回填菜畦</el-radio>
                <el-radio value="PARTIALLY_BACKFILL">部分可回填</el-radio>
                <el-radio value="BACKFILL_UNAVAILABLE">不可回填</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="处理说明" prop="disposalRemark">
              <el-input
                v-model="disposeForm.disposalRemark"
                type="textarea"
                :rows="3"
                placeholder="请详细说明处理依据和后续建议"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="disposing" @click="handleGardenerDispose">
                <el-icon><CircleCheck /></el-icon>
                提交判定结果
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <div v-if="isProperty && selectedRecord.status === 'DISPOSED'" class="action-section">
          <el-divider />
          <h4 class="action-title">物业积分处理</h4>
          <el-alert
            title="园艺师处理完成后，请执行积分暂停操作并告知住户恢复条件"
            type="info"
            show-icon
            style="margin-bottom: 16px"
          />
          <el-form :model="pointsForm" :rules="pointsRules" label-width="120px" ref="pointsFormRef">
            <el-form-item label="扣除积分">
              <el-input-number v-model="pointsForm.pointsDeducted" :min="0" :max="500" />
              <span class="unit">分（0表示不扣分只暂停）</span>
            </el-form-item>
            <el-form-item label="暂停说明" prop="propertyRemark">
              <el-input
                v-model="pointsForm.propertyRemark"
                type="textarea"
                :rows="2"
                placeholder="说明积分暂停的原因，住户会看到此内容"
              />
            </el-form-item>
            <el-form-item label="恢复条件" prop="recoveryConditions">
              <el-input
                v-model="pointsForm.recoveryConditions"
                type="textarea"
                :rows="3"
                placeholder="例如：连续3次投放无污染厨余；参加1次社区环保培训；提交书面保证书等"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="danger" :loading="pausingPoints" @click="handlePausePoints">
                <el-icon><Coin /></el-icon>
                执行积分暂停
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <div v-if="isProperty && selectedRecord.status === 'POINTS_PAUSED'" class="action-section">
          <el-divider />
          <h4 class="action-title">恢复绿色积分</h4>
          <el-alert
            title="确认住户已满足恢复条件后，恢复其绿色积分增长权限"
            type="success"
            show-icon
            style="margin-bottom: 16px"
          />
          <el-form-item>
            <el-button type="success" :loading="recovering" @click="handleRecoverPoints">
              <el-icon><CircleCheck /></el-icon>
              确认恢复积分
            </el-button>
          </el-form-item>
        </div>

        <div v-if="isResident" class="action-section resident-tip">
          <el-divider />
          <el-alert
            :title="getResidentTipTitle(selectedRecord)"
            :type="selectedRecord.status === 'RECOVERED' ? 'success' : 'warning'"
            show-icon
          >
            <template #default>
              <div v-if="selectedRecord.status === 'POINTS_PAUSED' || selectedRecord.status === 'RECOVERED'">
                <p><b>扣分原因：</b>{{ selectedRecord.propertyRemark || '因厨余污染被暂停积分增长' }}</p>
                <p><b>复核人：</b>物业用户 {{ selectedRecord.propertyApproverId || '暂无' }}</p>
                <p><b>恢复条件：</b>{{ selectedRecord.recoveryConditions || '请联系物业咨询' }}</p>
                <p>
                  <b>本批堆肥回填情况：</b>
                  <el-tag size="small" :type="getResultType(selectedRecord.compostFinalResult)">
                    {{ getResultText(selectedRecord.compostFinalResult) }}
                  </el-tag>
                </p>
              </div>
              <div v-else>
                <p>当前处理阶段：{{ getStatusText(selectedRecord.status) }}</p>
                <p>处理完成后此处将显示详细扣分说明、复核人信息、恢复条件及堆肥最终结果。</p>
              </div>
            </template>
          </el-alert>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  WarningFilled,
  Document,
  Tools,
  Coin,
  DataLine,
  Picture,
  CircleCheck,
  Warning,
  User
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import {
  getAllContaminationRecords,
  getPendingContaminationRecords,
  getContaminationRecordsByUserId,
  assignGardenerToContamination,
  gardenerDisposeContamination,
  pauseUserPoints,
  recoverUserPoints
} from '@/api/contamination'

const userStore = useUserStore()
const currentRole = computed(() => userStore.currentUser?.role || 'RESIDENT')
const isCollector = computed(() => currentRole.value === 'COLLECTOR')
const isGardener = computed(() => currentRole.value === 'GARDENER')
const isProperty = computed(() => currentRole.value === 'PROPERTY')
const isResident = computed(() => currentRole.value === 'RESIDENT')

const records = ref([])
const loading = ref(false)
const statusFilter = ref('')
const counts = reactive({
  ISOLATED: 0,
  PENDING_DISPOSAL: 0,
  DISPOSED: 0
})
const userPaused = ref(false)
const myPoints = ref(0)

const detailVisible = ref(false)
const selectedRecord = ref(null)

const assigning = ref(false)
const assignForm = reactive({ gardenerId: null })

const disposing = ref(false)
const disposeFormRef = ref(null)
const disposeForm = reactive({
  decision: '',
  extendedDays: 7,
  disposalRemark: '',
  finalResult: ''
})
const disposeRules = {
  decision: [{ required: true, message: '请选择处理方式', trigger: 'change' }],
  finalResult: [{ required: true, message: '请选择堆肥最终结果', trigger: 'change' }],
  disposalRemark: [{ required: true, message: '请填写处理说明', trigger: 'blur' }]
}

const pausingPoints = ref(false)
const pointsFormRef = ref(null)
const pointsForm = reactive({
  pointsDeducted: 20,
  propertyRemark: '',
  recoveryConditions: ''
})
const pointsRules = {
  recoveryConditions: [{ required: true, message: '请明确告知住户恢复条件', trigger: 'blur' }]
}

const recovering = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    let data
    if (isResident.value) {
      data = await getContaminationRecordsByUserId(userStore.currentUser.id)
    } else {
      data = await getAllContaminationRecords()
    }
    if (statusFilter.value) {
      data = data.filter(r => r.status === statusFilter.value)
    }
    records.value = data

    counts.ISOLATED = data.filter(r => r.status === 'ISOLATED').length
    counts.PENDING_DISPOSAL = data.filter(r => r.status === 'PENDING_DISPOSAL').length
    counts.DISPOSED = data.filter(r => r.status === 'DISPOSED').length
  } catch (e) {
    console.error(e)
    ElMessage.error('加载污染记录失败')
  } finally {
    loading.value = false
  }
}

const openDetail = (row) => {
  selectedRecord.value = { ...row }
  detailVisible.value = true
  assignForm.gardenerId = null
  disposeForm.decision = ''
  disposeForm.extendedDays = 7
  disposeForm.disposalRemark = ''
  disposeForm.finalResult = ''
  pointsForm.pointsDeducted = row.severityLevel === 'SEVERE' ? 50 : (row.severityLevel === 'MODERATE' ? 20 : 10)
  pointsForm.propertyRemark = ''
  pointsForm.recoveryConditions = ''
}

const getStepIndex = (status) => {
  const map = {
    REPORTED: 0,
    ISOLATED: 0,
    PENDING_DISPOSAL: 1,
    DISPOSED: 2,
    POINTS_PAUSED: 3,
    RECOVERED: 4,
    CANCELLED: 0
  }
  return map[status] ?? 0
}

const getSeverityType = (lvl) => ({
  MILD: 'warning',
  MODERATE: 'warning',
  SEVERE: 'danger'
}[lvl] || 'info')

const getSeverityText = (lvl) => ({
  MILD: '轻微',
  MODERATE: '中等',
  SEVERE: '严重'
}[lvl] || lvl)

const getStatusType = (s) => ({
  REPORTED: 'warning',
  ISOLATED: 'warning',
  PENDING_DISPOSAL: 'danger',
  DISPOSED: 'primary',
  POINTS_PAUSED: 'danger',
  RECOVERED: 'success',
  CANCELLED: 'info'
}[s] || 'info')

const getStatusText = (s) => ({
  REPORTED: '已报告',
  ISOLATED: '已隔离待分配',
  PENDING_DISPOSAL: '待园艺师处理',
  DISPOSED: '已处理待积分',
  POINTS_PAUSED: '积分暂停中',
  RECOVERED: '已恢复',
  CANCELLED: '已取消'
}[s] || s)

const getDecisionText = (d) => ({
  SCRAP_WHOLE: '整桶报废',
  PARTIAL_PICK: '局部挑拣',
  EXTEND_FERMENTATION: '延长发酵',
  NORMAL_USE: '正常使用'
}[d] || d)

const getResultType = (r) => ({
  BACKFILL_AVAILABLE: 'success',
  PARTIALLY_BACKFILL: 'warning',
  BACKFILL_UNAVAILABLE: 'danger',
  PENDING: 'info'
}[r] || 'info')

const getResultText = (r) => ({
  BACKFILL_AVAILABLE: '可回填菜畦',
  PARTIALLY_BACKFILL: '部分可回填',
  BACKFILL_UNAVAILABLE: '不可回填',
  PENDING: '待判定'
}[r] || '待判定')

const getResidentTipTitle = (record) => {
  if (record.status === 'RECOVERED') return '积分已恢复，感谢您对社区环保工作的支持！'
  if (record.status === 'POINTS_PAUSED') return '您的绿色积分因本次污染已被暂停，详细信息如下：'
  return '处理进度通知'
}

const handleAssign = async () => {
  if (!assignForm.gardenerId) {
    ElMessage.warning('请选择园艺师')
    return
  }
  assigning.value = true
  try {
    await assignGardenerToContamination(selectedRecord.value.id, assignForm.gardenerId)
    ElMessage.success('已分配园艺师处理')
    detailVisible.value = false
    loadData()
  } catch (e) {
    const msg = e?.response?.data?.message || e?.message || '分配失败'
    ElMessageBox.alert(msg, '操作失败', { type: 'error' })
  } finally {
    assigning.value = false
  }
}

const handleGardenerDispose = async () => {
  if (!disposeFormRef.value) return
  try {
    await disposeFormRef.value.validate()
  } catch (_e) {
    ElMessage.warning('请完善判定信息')
    return
  }
  disposing.value = true
  try {
    await gardenerDisposeContamination(selectedRecord.value.id, {
      gardenerId: userStore.currentUser.id,
      decision: disposeForm.decision,
      disposalRemark: disposeForm.disposalRemark,
      extendedDays: disposeForm.decision === 'EXTEND_FERMENTATION' ? disposeForm.extendedDays : null,
      finalResult: disposeForm.finalResult
    })
    ElMessage.success('判定结果已提交')
    detailVisible.value = false
    loadData()
  } catch (e) {
    const msg = e?.response?.data?.message || e?.message || '提交失败'
    ElMessageBox.alert(msg, '操作失败', { type: 'error' })
  } finally {
    disposing.value = false
  }
}

const handlePausePoints = async () => {
  if (!pointsFormRef.value) return
  try {
    await pointsFormRef.value.validate()
  } catch (_e) {
    ElMessage.warning('请完善恢复条件')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确认扣除 ${pointsForm.pointsDeducted} 分并暂停该住户积分增长？此操作住户可见。`,
      '积分处理确认',
      { type: 'warning' }
    )
  } catch (_e) {
    return
  }
  pausingPoints.value = true
  try {
    await pauseUserPoints(selectedRecord.value.id, {
      propertyApproverId: userStore.currentUser.id,
      pointsDeducted: pointsForm.pointsDeducted,
      propertyRemark: pointsForm.propertyRemark || '因厨余污染被暂停积分增长',
      recoveryConditions: pointsForm.recoveryConditions
    })
    ElMessage.success('积分处理已执行，住户端可查看详情')
    detailVisible.value = false
    loadData()
  } catch (e) {
    const msg = e?.response?.data?.message || e?.message || '执行失败'
    ElMessageBox.alert(msg, '操作失败', { type: 'error' })
  } finally {
    pausingPoints.value = false
  }
}

const handleRecoverPoints = async () => {
  try {
    await ElMessageBox.confirm(
      '确认恢复该住户的绿色积分增长权限？',
      '恢复积分确认',
      { type: 'success' }
    )
  } catch (_e) {
    return
  }
  recovering.value = true
  try {
    await recoverUserPoints(selectedRecord.value.id, userStore.currentUser.id)
    ElMessage.success('积分已恢复')
    detailVisible.value = false
    loadData()
  } catch (e) {
    const msg = e?.response?.data?.message || e?.message || '恢复失败'
    ElMessageBox.alert(msg, '操作失败', { type: 'error' })
  } finally {
    recovering.value = false
  }
}

onMounted(() => {
  loadData()
  if (isResident.value) {
    myPoints.value = 100 + Math.floor(Math.random() * 200)
    userPaused.value = Math.random() > 0.7
  }
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.header-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
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
  background: linear-gradient(135deg, #fff7e6, #ffe7ba);
  border-radius: 8px;
}

.record-id {
  font-weight: 600;
  color: #409eff;
}

.deducted-points {
  color: #f56c6c;
  font-weight: 600;
}
.deducted-points.big {
  font-size: 18px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
}

.photo-placeholder {
  display: flex;
  align-items: center;
  padding: 6px;
  background: #ecf5ff;
  border-radius: 4px;
  border: 1px dashed #409eff;
  width: fit-content;
}

.section-card {
  margin-top: 16px;
  border: 1px solid #e1f3d8;
}
.section-card.warning-card {
  border: 1px solid #fde2e2;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  color: #67c23a;
}
.section-title.warning {
  color: #e6a23c;
}

.action-section {
  margin-top: 4px;
}
.action-title {
  margin: 12px 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.unit {
  margin-left: 8px;
  color: #909399;
}

.resident-tip {
  padding: 4px 0;
}
</style>
