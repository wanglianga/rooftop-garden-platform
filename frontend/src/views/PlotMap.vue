<template>
  <div class="plot-map-page">
    <el-card class="map-card">
      <template #header>
        <div class="card-header">
          <span class="title">菜畦分布图</span>
          <div class="legend">
            <span class="legend-item"><span class="dot available"></span>可认领</span>
            <span class="legend-item"><span class="dot claimed"></span>已认领</span>
            <span class="legend-item"><span class="dot planting"></span>种植中</span>
            <span class="legend-item"><span class="dot maintenance"></span>维护中</span>
            <span class="legend-item"><span class="dot idle"></span>休耕</span>
          </div>
        </div>
      </template>

      <div class="garden-container">
        <div class="garden-info">
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="info-card">
                <el-icon class="info-icon"><Grid /></el-icon>
                <div class="info-content">
                  <div class="info-number">{{ plots.length }}</div>
                  <div class="info-label">菜畦总数</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="info-card">
                <el-icon class="info-icon success"><CircleCheck /></el-icon>
                <div class="info-content">
                  <div class="info-number">{{ availableCount }}</div>
                  <div class="info-label">可认领</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="info-card">
                <el-icon class="info-icon warning"><Clock /></el-icon>
                <div class="info-content">
                  <div class="info-number">{{ plantingCount }}</div>
                  <div class="info-label">种植中</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="info-card">
                <el-icon class="info-icon info"><User /></el-icon>
                <div class="info-content">
                  <div class="info-number">{{ claimedCount }}</div>
                  <div class="info-label">已认领</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="plot-area">
          <div class="plot-grid" ref="plotGrid">
            <div
              v-for="plot in plots"
              :key="plot.id"
              class="plot-item"
              :class="`plot-${plot.status.toLowerCase()}`"
              :style="{
                left: plot.positionX + 'px',
                top: plot.positionY + 'px',
                width: plot.width + 'px',
                height: plot.height + 'px'
              }"
              @click="handlePlotClick(plot)"
            >
              <div class="plot-code">{{ plot.plotCode }}</div>
              <div class="plot-crop" v-if="plot.currentCrop">{{ plot.currentCrop }}</div>
              <div class="plot-sunlight">
                <el-icon><Sunny /></el-icon>
              </div>
            </div>
          </div>

          <div class="sunlight-legend">
            <div class="sun-label">
              <span>日照时间</span>
              <el-icon><Sunny /></el-icon>
            </div>
            <div class="sun-bar">
              <div class="sun-segment morning"></div>
              <div class="sun-segment noon"></div>
              <div class="sun-segment afternoon"></div>
            </div>
            <div class="sun-times">
              <span>6:00</span>
              <span>12:00</span>
              <span>18:00</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="菜畦详情" width="600px">
      <div v-if="selectedPlot" class="plot-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="菜畦编号">{{ selectedPlot.plotCode }}</el-descriptions-item>
          <el-descriptions-item label="菜畦名称">{{ selectedPlot.name }}</el-descriptions-item>
          <el-descriptions-item label="面积">{{ selectedPlot.area }} 平方米</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedPlot.status)">{{ getStatusText(selectedPlot.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="日照时段">{{ selectedPlot.sunlightPeriod }}</el-descriptions-item>
          <el-descriptions-item label="土壤类型">{{ selectedPlot.soilType }}</el-descriptions-item>
          <el-descriptions-item label="土壤PH值">{{ selectedPlot.soilPH }}</el-descriptions-item>
          <el-descriptions-item label="灌溉方式">{{ selectedPlot.irrigationType }}</el-descriptions-item>
          <el-descriptions-item label="当前作物" :span="2">{{ selectedPlot.currentCrop || '无' }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ selectedPlot.description || '暂无描述' }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-actions" v-if="canClaim">
          <el-button type="primary" @click="handleClaim">
            <el-icon><EditPen /></el-icon>
            认领菜畦
          </el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="claimVisible" title="认领菜畦" width="500px">
      <el-form :model="claimForm" label-width="100px" ref="claimFormRef">
        <el-form-item label="菜畦编号">
          <span>{{ selectedPlot?.plotCode }}</span>
        </el-form-item>
        <el-form-item label="种植品类" prop="cropType">
          <el-input v-model="claimForm.cropType" placeholder="请输入计划种植的作物" />
        </el-form-item>
        <el-form-item label="浇水安排" prop="wateringSchedule">
          <el-input v-model="claimForm.wateringSchedule" placeholder="例如：每日早晚各一次" />
        </el-form-item>
        <el-form-item label="厨余投放">
          <el-switch v-model="claimForm.willingToDonateCompost" />
          <span class="form-tip">愿意将家庭厨余投放到堆肥桶</span>
        </el-form-item>
        <el-form-item label="采收偏好">
          <el-select v-model="claimForm.harvestPreference" style="width: 100%">
            <el-option label="全部自用" value="全部自用" />
            <el-option label="部分捐赠" value="部分捐赠给社区食堂" />
            <el-option label="全部捐赠" value="全部捐赠" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="claimForm.remark" type="textarea" :rows="3" placeholder="其他说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="claimVisible = false">取消</el-button>
        <el-button type="primary" @click="submitClaim">提交认领</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getPlots } from '@/api/plot'
import { createClaim } from '@/api/claim'
import {
  Grid,
  CircleCheck,
  Clock,
  User,
  Sunny,
  EditPen
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const plots = ref([])
const selectedPlot = ref(null)
const detailVisible = ref(false)
const claimVisible = ref(false)
const claimFormRef = ref(null)
const claimForm = ref({
  cropType: '',
  wateringSchedule: '',
  willingToDonateCompost: true,
  harvestPreference: '全部自用',
  remark: ''
})

const availableCount = computed(() => 
  plots.value.filter(p => p.status === 'AVAILABLE').length
)

const plantingCount = computed(() => 
  plots.value.filter(p => p.status === 'PLANTING').length
)

const claimedCount = computed(() => 
  plots.value.filter(p => p.status === 'CLAIMED' || p.status === 'PLANTING').length
)

const canClaim = computed(() => 
  userStore.role === 'RESIDENT' && selectedPlot.value?.status === 'AVAILABLE'
)

const loadPlots = async () => {
  try {
    const data = await getPlots()
    plots.value = data
  } catch (e) {
    console.error(e)
  }
}

const getStatusType = (status) => {
  const typeMap = {
    AVAILABLE: 'success',
    CLAIMED: 'info',
    PLANTING: 'warning',
    HARVESTING: 'primary',
    MAINTENANCE: 'danger',
    IDLE: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    AVAILABLE: '可认领',
    CLAIMED: '已认领',
    PLANTING: '种植中',
    HARVESTING: '采收中',
    MAINTENANCE: '维护中',
    IDLE: '休耕'
  }
  return textMap[status] || status
}

const handlePlotClick = (plot) => {
  selectedPlot.value = plot
  detailVisible.value = true
}

const handleClaim = () => {
  detailVisible.value = false
  claimVisible.value = true
  claimForm.value = {
    cropType: '',
    wateringSchedule: '',
    willingToDonateCompost: true,
    harvestPreference: '全部自用',
    remark: ''
  }
}

const submitClaim = async () => {
  try {
    await createClaim({
      plotId: selectedPlot.value.id,
      userId: userStore.currentUser.id,
      ...claimForm.value
    })
    ElMessage.success('认领申请已提交，请等待审核')
    claimVisible.value = false
    loadPlots()
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadPlots()
})
</script>

<style scoped>
.plot-map-page {
  padding: 0;
}

.map-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: 600;
}

.legend {
  display: flex;
  gap: 16px;
  font-size: 13px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}

.dot.available { background: #67c23a; }
.dot.claimed { background: #409eff; }
.dot.planting { background: #e6a23c; }
.dot.maintenance { background: #f56c6c; }
.dot.idle { background: #909399; }

.garden-container {
  padding: 20px 0;
}

.garden-info {
  margin-bottom: 30px;
}

.info-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.info-icon {
  font-size: 32px;
  color: #409eff;
}

.info-icon.success { color: #67c23a; }
.info-icon.warning { color: #e6a23c; }
.info-icon.info { color: #909399; }

.info-number {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.info-label {
  font-size: 13px;
  color: #909399;
}

.plot-area {
  display: flex;
  gap: 40px;
  align-items: flex-start;
}

.plot-grid {
  position: relative;
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #a8e6cf 0%, #88d8b0 100%);
  border-radius: 12px;
  box-shadow: inset 0 2px 10px rgba(0,0,0,0.1);
}

.plot-item {
  position: absolute;
  background: #f5deb3;
  border: 2px solid #8b7355;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.plot-item:hover {
  transform: scale(1.05);
  z-index: 10;
  box-shadow: 0 4px 12px rgba(0,0,0,0.3);
}

.plot-available {
  background: linear-gradient(135deg, #c8e6c9, #a5d6a7);
  border-color: #66bb6a;
}

.plot-claimed {
  background: linear-gradient(135deg, #bbdefb, #90caf9);
  border-color: #42a5f5;
}

.plot-planting {
  background: linear-gradient(135deg, #ffe0b2, #ffcc80);
  border-color: #ffa726;
}

.plot-maintenance {
  background: linear-gradient(135deg, #ffcdd2, #ef9a9a);
  border-color: #ef5350;
}

.plot-idle {
  background: linear-gradient(135deg, #e0e0e0, #bdbdbd);
  border-color: #9e9e9e;
}

.plot-code {
  font-weight: 700;
  font-size: 14px;
  color: #333;
}

.plot-crop {
  font-size: 11px;
  color: #666;
  margin-top: 2px;
}

.plot-sunlight {
  position: absolute;
  top: 4px;
  right: 4px;
  font-size: 12px;
  color: #f59e0b;
}

.sunlight-legend {
  flex: 1;
  padding: 20px;
  background: #fffbeb;
  border-radius: 8px;
  border: 1px solid #fde68a;
}

.sun-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #92400e;
  margin-bottom: 12px;
}

.sun-label .el-icon {
  color: #f59e0b;
  font-size: 20px;
}

.sun-bar {
  display: flex;
  height: 30px;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.sun-segment {
  flex: 1;
}

.sun-segment.morning {
  background: linear-gradient(to right, #fef3c7, #fde68a);
}

.sun-segment.noon {
  background: linear-gradient(to right, #fde68a, #fbbf24);
}

.sun-segment.afternoon {
  background: linear-gradient(to right, #fbbf24, #f59e0b);
}

.sun-times {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #92400e;
}

.plot-detail {
  padding: 10px 0;
}

.detail-actions {
  margin-top: 20px;
  text-align: center;
}

.form-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}
</style>
