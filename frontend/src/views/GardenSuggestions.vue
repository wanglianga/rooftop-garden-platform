<template>
  <div class="garden-suggestions-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">巡园建议</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增建议
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="loadData">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待处理" name="unhandled" />
      </el-tabs>

      <el-table :data="suggestions" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" width="150" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="getTypeType(row.type)">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="plotId" label="菜畦ID" width="80" />
        <el-table-column prop="gardenerId" label="园艺师" width="100" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.handled ? 'success' : 'warning'">
              {{ row.handled ? '已处理' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">查看</el-button>
            <el-button 
              v-if="!row.handled"
              size="small" 
              type="primary"
              @click="handleSuggestion(row)"
            >
              处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDetailDialog" title="建议详情" width="500px">
      <div v-if="currentSuggestion">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="标题" :span="2">{{ currentSuggestion.title }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ getTypeText(currentSuggestion.type) }}</el-descriptions-item>
          <el-descriptions-item label="菜畦ID">{{ currentSuggestion.plotId || '无' }}</el-descriptions-item>
          <el-descriptions-item label="园艺师ID">{{ currentSuggestion.gardenerId }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentSuggestion.handled ? 'success' : 'warning'">
              {{ currentSuggestion.handled ? '已处理' : '待处理' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="内容" :span="2" class="content-desc">
            {{ currentSuggestion.content }}
          </el-descriptions-item>
          <el-descriptions-item label="反馈" :span="2" v-if="currentSuggestion.handled">
            {{ currentSuggestion.feedback }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ currentSuggestion.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <el-dialog v-model="showAddDialog" title="新增巡园建议" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="建议类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="种植建议" value="PLANTING_ADVICE" />
            <el-option label="虫害防治" value="PEST_CONTROL" />
            <el-option label="施肥建议" value="FERTILIZATION" />
            <el-option label="浇水建议" value="WATERING" />
            <el-option label="土壤改良" value="SOIL_IMPROVEMENT" />
            <el-option label="维护建议" value="MAINTENANCE" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="菜畦ID">
          <el-input-number v-model="form.plotId" :min="0" />
          <span style="margin-left: 8px; color: #909399">0表示全局建议</span>
        </el-form-item>
        <el-form-item label="建议内容">
          <el-input v-model="form.content" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="针对对象">
          <el-input v-model="form.suggestionTarget" placeholder="例如：3号楼居民" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addSuggestion">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getSuggestions, getUnhandledSuggestions, createSuggestion, handleSuggestion as apiHandle } from '@/api/gardener'
import { Plus } from '@element-plus/icons-vue'

const userStore = useUserStore()

const suggestions = ref([])
const loading = ref(false)
const activeTab = ref('all')
const showDetailDialog = ref(false)
const showAddDialog = ref(false)
const currentSuggestion = ref(null)
const form = ref({
  type: 'PLANTING_ADVICE',
  title: '',
  plotId: 0,
  content: '',
  suggestionTarget: ''
})

const loadData = async () => {
  loading.value = true
  try {
    let data
    if (activeTab.value === 'unhandled') {
      data = await getUnhandledSuggestions()
    } else {
      data = await getSuggestions()
    }
    suggestions.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getTypeType = (type) => {
  const typeMap = {
    PLANTING_ADVICE: 'success',
    PEST_CONTROL: 'danger',
    FERTILIZATION: 'warning',
    WATERING: 'primary',
    SOIL_IMPROVEMENT: 'info',
    MAINTENANCE: 'info',
    OTHER: 'info'
  }
  return typeMap[type] || 'info'
}

const getTypeText = (type) => {
  const textMap = {
    PLANTING_ADVICE: '种植建议',
    PEST_CONTROL: '虫害防治',
    FERTILIZATION: '施肥建议',
    WATERING: '浇水建议',
    SOIL_IMPROVEMENT: '土壤改良',
    MAINTENANCE: '维护建议',
    OTHER: '其他'
  }
  return textMap[type] || type
}

const viewDetail = (row) => {
  currentSuggestion.value = row
  showDetailDialog.value = true
}

const handleSuggestion = async (row) => {
  try {
    const { value: feedback } = await ElMessageBox.prompt('请输入处理反馈', '处理建议', {
      inputType: 'textarea'
    })
    await apiHandle(row.id, feedback, userStore.currentUser.id)
    ElMessage.success('处理成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const addSuggestion = async () => {
  try {
    await createSuggestion({
      ...form.value,
      gardenerId: userStore.currentUser.id
    })
    ElMessage.success('提交成功')
    showAddDialog.value = false
    form.value = {
      type: 'PLANTING_ADVICE',
      title: '',
      plotId: 0,
      content: '',
      suggestionTarget: ''
    }
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

.content-desc {
  max-height: 100px;
  overflow-y: auto;
}
</style>
