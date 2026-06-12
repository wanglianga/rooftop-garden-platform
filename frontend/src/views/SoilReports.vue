<template>
  <div class="soil-reports-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">土壤检测报告</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增检测
          </el-button>
        </div>
      </template>

      <el-table :data="reports" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="plotId" label="菜畦ID" width="80" />
        <el-table-column prop="gardenerId" label="园艺师ID" width="100" />
        <el-table-column prop="phValue" label="PH值" width="80">
          <template #default="{ row }">
            <el-tag size="small" :type="getPHType(row.phValue)">
              {{ row.phValue }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="moisture" label="湿度(%)" width="100" />
        <el-table-column prop="nitrogen" label="氮" width="80" />
        <el-table-column prop="phosphorus" label="磷" width="80" />
        <el-table-column prop="potassium" label="钾" width="80" />
        <el-table-column prop="soilTexture" label="土质" width="100" />
        <el-table-column prop="testTime" label="检测时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDetailDialog" title="土壤检测详情" width="500px">
      <div v-if="currentReport">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="菜畦ID">{{ currentReport.plotId }}</el-descriptions-item>
          <el-descriptions-item label="园艺师ID">{{ currentReport.gardenerId }}</el-descriptions-item>
          <el-descriptions-item label="PH值">
            <el-tag :type="getPHType(currentReport.phValue)">{{ currentReport.phValue }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="湿度">{{ currentReport.moisture }}%</el-descriptions-item>
          <el-descriptions-item label="氮含量">{{ currentReport.nitrogen }} mg/kg</el-descriptions-item>
          <el-descriptions-item label="磷含量">{{ currentReport.phosphorus }} mg/kg</el-descriptions-item>
          <el-descriptions-item label="钾含量">{{ currentReport.potassium }} mg/kg</el-descriptions-item>
          <el-descriptions-item label="土质">{{ currentReport.soilTexture }}</el-descriptions-item>
          <el-descriptions-item label="检测时间" :span="2">{{ currentReport.testTime }}</el-descriptions-item>
          <el-descriptions-item label="建议" :span="2">{{ currentReport.recommendation }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentReport.remark || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <el-dialog v-model="showAddDialog" title="新增土壤检测" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="菜畦ID">
          <el-input-number v-model="form.plotId" :min="1" />
        </el-form-item>
        <el-form-item label="PH值">
          <el-input-number v-model="form.phValue" :min="0" :max="14" :step="0.1" :precision="1" />
        </el-form-item>
        <el-form-item label="湿度">
          <el-input-number v-model="form.moisture" :min="0" :max="100" :step="1" />
          <span style="margin-left: 8px">%</span>
        </el-form-item>
        <el-form-item label="氮含量">
          <el-input-number v-model="form.nitrogen" :min="0" :step="1" />
          <span style="margin-left: 8px">mg/kg</span>
        </el-form-item>
        <el-form-item label="磷含量">
          <el-input-number v-model="form.phosphorus" :min="0" :step="1" />
          <span style="margin-left: 8px">mg/kg</span>
        </el-form-item>
        <el-form-item label="钾含量">
          <el-input-number v-model="form.potassium" :min="0" :step="1" />
          <span style="margin-left: 8px">mg/kg</span>
        </el-form-item>
        <el-form-item label="土质">
          <el-select v-model="form.soilTexture" style="width: 100%">
            <el-option label="砂质土" value="砂质土" />
            <el-option label="壤土" value="壤土" />
            <el-option label="粘质土" value="粘质土" />
            <el-option label="腐殖土" value="腐殖土" />
          </el-select>
        </el-form-item>
        <el-form-item label="建议">
          <el-input v-model="form.recommendation" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addReport">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getSoilReports, createSoilReport } from '@/api/gardener'
import { Plus } from '@element-plus/icons-vue'

const userStore = useUserStore()

const reports = ref([])
const loading = ref(false)
const showDetailDialog = ref(false)
const showAddDialog = ref(false)
const currentReport = ref(null)
const form = ref({
  plotId: 1,
  phValue: 6.5,
  moisture: 60,
  nitrogen: 50,
  phosphorus: 30,
  potassium: 40,
  soilTexture: '腐殖土',
  recommendation: '',
  remark: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const data = await getSoilReports()
    reports.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getPHType = (ph) => {
  if (!ph) return 'info'
  if (ph < 5.5) return 'danger'
  if (ph < 6.0) return 'warning'
  if (ph < 7.5) return 'success'
  if (ph < 8.0) return 'warning'
  return 'danger'
}

const viewDetail = (row) => {
  currentReport.value = row
  showDetailDialog.value = true
}

const addReport = async () => {
  try {
    await createSoilReport({
      ...form.value,
      gardenerId: userStore.currentUser.id
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
</style>
