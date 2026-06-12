<template>
  <div class="pest-records-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">虫害记录</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增记录
          </el-button>
        </div>
      </template>

      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="plotId" label="菜畦ID" width="80" />
        <el-table-column prop="plantingId" label="种植ID" width="80" />
        <el-table-column prop="pestName" label="害虫名称" width="120" />
        <el-table-column prop="pestType" label="虫害类型" width="100" />
        <el-table-column label="严重程度" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="getSeverityType(row.severity)">
              {{ getSeverityText(row.severity) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="treatmentMethod" label="处理方法" show-overflow-tooltip />
        <el-table-column prop="effect" label="效果" width="80" />
        <el-table-column prop="discoveryTime" label="发现时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showAddDialog" title="新增虫害记录" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="菜畦ID">
          <el-input-number v-model="form.plotId" :min="1" />
        </el-form-item>
        <el-form-item label="种植ID">
          <el-input-number v-model="form.plantingId" :min="0" />
        </el-form-item>
        <el-form-item label="害虫名称">
          <el-input v-model="form.pestName" />
        </el-form-item>
        <el-form-item label="虫害类型">
          <el-select v-model="form.pestType" style="width: 100%">
            <el-option label="蚜虫" value="蚜虫" />
            <el-option label="白粉虱" value="白粉虱" />
            <el-option label="红蜘蛛" value="红蜘蛛" />
            <el-option label="菜青虫" value="菜青虫" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="严重程度">
          <el-radio-group v-model="form.severity">
            <el-radio value="LOW">轻微</el-radio>
            <el-radio value="MEDIUM">中等</el-radio>
            <el-radio value="HIGH">严重</el-radio>
            <el-radio value="SEVERE">极严重</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="处理方法">
          <el-input v-model="form.treatmentMethod" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="用药">
          <el-input v-model="form.treatmentAgent" />
        </el-form-item>
        <el-form-item label="效果">
          <el-select v-model="form.effect" style="width: 100%">
            <el-option label="良好" value="良好" />
            <el-option label="一般" value="一般" />
            <el-option label="不佳" value="不佳" />
            <el-option label="待观察" value="待观察" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addRecord">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getPestRecords, createPestRecord } from '@/api/gardener'
import { Plus } from '@element-plus/icons-vue'

const userStore = useUserStore()

const records = ref([])
const loading = ref(false)
const showAddDialog = ref(false)
const form = ref({
  plotId: 1,
  plantingId: 0,
  pestName: '',
  pestType: '蚜虫',
  severity: 'LOW',
  description: '',
  treatmentMethod: '',
  treatmentAgent: '',
  effect: '待观察'
})

const loadData = async () => {
  loading.value = true
  try {
    const data = await getPestRecords()
    records.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getSeverityType = (severity) => {
  const typeMap = {
    LOW: 'success',
    MEDIUM: 'warning',
    HIGH: 'danger',
    SEVERE: 'danger'
  }
  return typeMap[severity] || 'info'
}

const getSeverityText = (severity) => {
  const textMap = {
    LOW: '轻微',
    MEDIUM: '中等',
    HIGH: '严重',
    SEVERE: '极严重'
  }
  return textMap[severity] || severity
}

const viewDetail = (row) => {
  ElMessage.info('查看详情：' + row.id)
}

const addRecord = async () => {
  try {
    await createPestRecord({
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
