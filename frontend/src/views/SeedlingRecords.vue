<template>
  <div class="seedling-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">补苗记录</span>
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
        <el-table-column prop="cropName" label="作物名称" width="120" />
        <el-table-column prop="seedlingCount" label="原有苗数" width="100" />
        <el-table-column prop="deadCount" label="死亡苗数" width="100" />
        <el-table-column prop="replantCount" label="补苗数量" width="100" />
        <el-table-column prop="reason" label="原因" show-overflow-tooltip />
        <el-table-column prop="source" label="来源" width="100" />
        <el-table-column prop="replantTime" label="补苗时间" width="180" />
        <el-table-column prop="gardenerId" label="园艺师ID" width="100" />
      </el-table>
    </el-card>

    <el-dialog v-model="showAddDialog" title="新增补苗记录" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="菜畦ID">
          <el-input-number v-model="form.plotId" :min="1" />
        </el-form-item>
        <el-form-item label="种植ID">
          <el-input-number v-model="form.plantingId" :min="0" />
        </el-form-item>
        <el-form-item label="作物名称">
          <el-input v-model="form.cropName" />
        </el-form-item>
        <el-form-item label="原有苗数">
          <el-input-number v-model="form.seedlingCount" :min="0" />
        </el-form-item>
        <el-form-item label="死亡苗数">
          <el-input-number v-model="form.deadCount" :min="0" />
        </el-form-item>
        <el-form-item label="补苗数量">
          <el-input-number v-model="form.replantCount" :min="0" />
        </el-form-item>
        <el-form-item label="原因">
          <el-select v-model="form.reason" style="width: 100%">
            <el-option label="虫害" value="虫害" />
            <el-option label="病害" value="病害" />
            <el-option label="干旱" value="干旱" />
            <el-option label="涝害" value="涝害" />
            <el-option label="自然死亡" value="自然死亡" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源">
          <el-input v-model="form.source" placeholder="例如：社区园艺站" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
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
import { getSeedlingRecords, createSeedlingRecord } from '@/api/gardener'
import { Plus } from '@element-plus/icons-vue'

const userStore = useUserStore()

const records = ref([])
const loading = ref(false)
const showAddDialog = ref(false)
const form = ref({
  plotId: 1,
  plantingId: 0,
  cropName: '',
  seedlingCount: 0,
  deadCount: 0,
  replantCount: 0,
  reason: '自然死亡',
  source: '',
  remark: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const data = await getSeedlingRecords()
    records.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const addRecord = async () => {
  try {
    await createSeedlingRecord({
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
