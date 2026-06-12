<template>
  <div class="plot-detail-page">
    <el-page-header @back="goBack" content="菜畦详情">
      <template #content>
        <span class="page-title">{{ plot?.name || '菜畦详情' }}</span>
      </template>
    </el-page-header>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span class="title">基本信息</span>
          </template>
          <el-descriptions :column="2" border v-if="plot">
            <el-descriptions-item label="菜畦编号">{{ plot.plotCode }}</el-descriptions-item>
            <el-descriptions-item label="菜畦名称">{{ plot.name }}</el-descriptions-item>
            <el-descriptions-item label="面积">{{ plot.area }} 平方米</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(plot.status)">{{ getStatusText(plot.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="日照时段">{{ plot.sunlightPeriod }}</el-descriptions-item>
            <el-descriptions-item label="土壤类型">{{ plot.soilType }}</el-descriptions-item>
            <el-descriptions-item label="土壤PH">{{ plot.soilPH }}</el-descriptions-item>
            <el-descriptions-item label="灌溉方式">{{ plot.irrigationType }}</el-descriptions-item>
            <el-descriptions-item label="当前作物">{{ plot.currentCrop || '无' }}</el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ plot.description || '暂无描述' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>
            <span class="title">种植记录</span>
          </template>
          <el-table :data="plantings" size="small">
            <el-table-column prop="cropName" label="作物名称" />
            <el-table-column prop="cropVariety" label="品种" />
            <el-table-column prop="plantCount" label="数量" width="80" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag size="small">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="sowingDate" label="播种日期" width="120" />
            <el-table-column prop="expectedHarvestDate" label="预计采收" width="120" />
          </el-table>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>
            <span class="title">采收记录</span>
          </template>
          <el-table :data="harvests" size="small">
            <el-table-column prop="cropName" label="作物" />
            <el-table-column prop="weight" label="重量" />
            <el-table-column prop="unit" label="单位" width="80" />
            <el-table-column prop="quality" label="品质" width="80" />
            <el-table-column label="分配方式" width="100">
              <template #default="{ row }">
                <el-tag size="small">{{ row.distributionType }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="harvestTime" label="时间" width="160" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <template #header>
            <span class="title">操作面板</span>
          </template>
          <div class="action-panel">
            <el-button type="primary" style="width: 100%; margin-bottom: 10px">
              申请认领
            </el-button>
            <el-button style="width: 100%; margin-bottom: 10px">
              记录浇水
            </el-button>
            <el-button style="width: 100%; margin-bottom: 10px">
              记录施肥
            </el-button>
            <el-button style="width: 100%; margin-bottom: 10px">
              添加采收
            </el-button>
            <el-button style="width: 100%">
              查看土壤报告
            </el-button>
          </div>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>
            <span class="title">照料日志</span>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="log in careLogs"
              :key="log.id"
              :timestamp="log.careTime"
              placement="top"
            >
              <el-card shadow="never">
                <h4>{{ log.careType }}</h4>
                <p>{{ log.description }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPlotById } from '@/api/plot'
import { getPlantingsByPlotId } from '@/api/planting'
import { getHarvestsByPlotId } from '@/api/harvest'
import { getCareLogsByPlotId } from '@/api/planting'

const route = useRoute()
const router = useRouter()

const plot = ref(null)
const plantings = ref([])
const harvests = ref([])
const careLogs = ref([])

const loadData = async () => {
  const id = route.params.id
  try {
    const plotData = await getPlotById(id)
    plot.value = plotData

    try {
      plantings.value = await getPlantingsByPlotId(id)
    } catch (e) {
      plantings.value = []
    }

    try {
      harvests.value = await getHarvestsByPlotId(id)
    } catch (e) {
      harvests.value = []
    }

    try {
      careLogs.value = await getCareLogsByPlotId(id)
    } catch (e) {
      careLogs.value = []
    }
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

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-title {
  font-size: 18px;
  font-weight: 600;
}

.title {
  font-size: 16px;
  font-weight: 600;
}

.action-panel {
  display: flex;
  flex-direction: column;
}
</style>
