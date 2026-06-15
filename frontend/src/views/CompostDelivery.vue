<template>
  <div class="compost-delivery-page">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="title">厨余桶列表</span>
          </template>

          <div class="bin-list">
            <div 
              v-for="bin in bins" 
              :key="bin.id" 
              class="bin-card"
              :class="{ selected: selectedBin?.id === bin.id }"
              @click="selectBin(bin)"
            >
              <div class="bin-header">
                <span class="bin-code">{{ bin.binCode }}</span>
                <el-tag size="small" :type="getBinStatusType(bin.status)">
                  {{ getBinStatusText(bin.status) }}
                </el-tag>
              </div>
              <div class="bin-info">
                <div class="bin-location">
                  <el-icon><Location /></el-icon>
                  {{ bin.location }}
                </div>
                <div class="bin-capacity">
                  容量：{{ bin.capacity }}kg
                </div>
              </div>
              <div class="bin-progress">
                <el-progress 
                  :percentage="getFillPercentage(bin)" 
                  :stroke-width="10"
                  :color="getProgressColor(bin)"
                />
                <span class="weight-text">{{ bin.currentWeight }} / {{ bin.capacity }} kg</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="title">投放厨余</span>
          </template>

          <el-form :model="deliveryForm" :rules="formRules" label-width="100px" ref="formRef">
            <el-form-item label="选择桶" prop="binId">
              <span v-if="selectedBin" class="selected-bin-text">{{ selectedBin.binCode }} - {{ selectedBin.location }}</span>
              <span v-else class="placeholder">请先在左侧选择厨余桶</span>
            </el-form-item>
            <el-form-item label="投放重量" prop="weight">
              <el-input-number 
                v-model="deliveryForm.weight" 
                :min="0.1" 
                :max="50" 
                :step="0.5"
                :precision="1"
                style="width: 200px"
              />
              <span class="unit">公斤</span>
            </el-form-item>
            <el-form-item label="厨余类型" prop="foodType">
              <el-select v-model="deliveryForm.foodType" multiple placeholder="请选择厨余类型" style="width: 100%">
                <el-option label="蔬菜叶" value="蔬菜叶" />
                <el-option label="果皮" value="果皮" />
                <el-option label="剩菜剩饭" value="剩菜剩饭" />
                <el-option label="咖啡渣" value="咖啡渣" />
                <el-option label="蛋壳" value="蛋壳" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
            <el-form-item label="污染情况" prop="contaminationLevel">
              <el-radio-group v-model="deliveryForm.contaminationLevel">
                <el-radio value="无">无污染</el-radio>
                <el-radio value="轻微">轻微</el-radio>
                <el-radio value="严重">严重</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="有塑料">
              <el-switch v-model="deliveryForm.hasPlastic" />
            </el-form-item>
            <el-form-item label="有液体">
              <el-switch v-model="deliveryForm.hasLiquid" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="deliveryForm.remark" type="textarea" :rows="3" maxlength="200" show-word-limit placeholder="选填" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitDelivery" :loading="submitting">
                <el-icon><Upload /></el-icon>
                确认投放
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>
            <span class="title">我的投放记录</span>
          </template>
          <el-table :data="myDeliveries" size="small" max-height="300">
            <el-table-column prop="binId" label="桶号" width="80" />
            <el-table-column prop="weight" label="重量(kg)" width="100" />
            <el-table-column prop="contaminationLevel" label="污染" width="80" />
            <el-table-column prop="deliveryTime" label="时间" width="160" />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag size="small" :type="row.collected ? 'success' : 'warning'">
                  {{ row.collected ? '已收' : '待收' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getBins, createDelivery, getDeliveriesByUserId } from '@/api/compost'
import { Location, Upload } from '@element-plus/icons-vue'

const userStore = useUserStore()

const bins = ref([])
const selectedBin = ref(null)
const myDeliveries = ref([])
const formRef = ref(null)
const submitting = ref(false)
const deliveryForm = ref({
  binId: null,
  weight: 1,
  foodType: [],
  contaminationLevel: '无',
  hasPlastic: false,
  hasLiquid: false,
  remark: ''
})

const validateBinId = (_rule, value, callback) => {
  if (!selectedBin.value) {
    callback(new Error('请先在左侧选择厨余桶'))
  } else {
    callback()
  }
}

const validateFoodType = (_rule, value, callback) => {
  if (!value || value.length === 0) {
    callback(new Error('请至少选择一种厨余类型'))
  } else {
    callback()
  }
}

const formRules = {
  binId: [{ validator: validateBinId, trigger: 'change' }],
  weight: [
    { required: true, message: '请输入投放重量', trigger: 'blur' },
    { type: 'number', min: 0.1, message: '投放重量必须大于 0', trigger: 'blur' }
  ],
  foodType: [{ validator: validateFoodType, trigger: 'change' }],
  contaminationLevel: [{ required: true, message: '请选择污染情况', trigger: 'change' }]
}

const loadBins = async () => {
  try {
    const data = await getBins()
    bins.value = data
    if (data.length > 0) {
      selectedBin.value = data[0]
    }
  } catch (e) {
    console.error(e)
  }
}

const loadMyDeliveries = async () => {
  try {
    const data = await getDeliveriesByUserId(userStore.currentUser.id)
    myDeliveries.value = data
  } catch (e) {
    console.error(e)
  }
}

const selectBin = (bin) => {
  selectedBin.value = bin
  deliveryForm.value.binId = bin.id
  if (formRef.value) {
    formRef.value.validateField('binId')
  }
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  deliveryForm.value = {
    binId: selectedBin.value?.id || null,
    weight: 1,
    foodType: [],
    contaminationLevel: '无',
    hasPlastic: false,
    hasLiquid: false,
    remark: ''
  }
}

const getBinStatusType = (status) => {
  const typeMap = {
    ACTIVE: 'success',
    FILLING: 'warning',
    FULL: 'danger',
    FERMENTING: 'info',
    MAINTENANCE: 'info',
    INACTIVE: 'info'
  }
  return typeMap[status] || 'info'
}

const getBinStatusText = (status) => {
  const textMap = {
    ACTIVE: '可用',
    FILLING: '投放中',
    FULL: '已满',
    FERMENTING: '发酵中',
    MAINTENANCE: '维护中',
    INACTIVE: '停用'
  }
  return textMap[status] || status
}

const getFillPercentage = (bin) => {
  if (!bin.capacity || bin.capacity === 0) return 0
  return Math.round((bin.currentWeight / bin.capacity) * 100)
}

const getProgressColor = (bin) => {
  const percent = getFillPercentage(bin)
  if (percent >= 90) return '#f56c6c'
  if (percent >= 60) return '#e6a23c'
  return '#67c23a'
}

const submitDelivery = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (_e) {
    ElMessage.warning('请完善表单必填项')
    return
  }

  submitting.value = true
  try {
    const payload = {
      binId: selectedBin.value.id,
      userId: userStore.currentUser.id,
      weight: deliveryForm.value.weight,
      foodType: Array.isArray(deliveryForm.value.foodType)
        ? deliveryForm.value.foodType.join('、')
        : deliveryForm.value.foodType || '',
      contaminationLevel: deliveryForm.value.contaminationLevel,
      hasPlastic: deliveryForm.value.hasPlastic,
      hasLiquid: deliveryForm.value.hasLiquid,
      remark: deliveryForm.value.remark
    }
    await createDelivery(payload)
    ElMessage({
      type: 'success',
      message: `投放成功！已登记 ${payload.weight} kg 厨余到 ${selectedBin.value.binCode}`,
      duration: 2500
    })
    resetForm()
    loadBins()
    loadMyDeliveries()
  } catch (e) {
    console.error(e)
    const msg = e?.response?.data?.message || e?.message || '投放失败，请检查表单后重试'
    ElMessageBox.alert(msg, '投放失败', {
      type: 'error',
      confirmButtonText: '我知道了'
    })
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadBins()
  loadMyDeliveries()
})
</script>

<style scoped>
.title {
  font-size: 16px;
  font-weight: 600;
}

.bin-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 600px;
  overflow-y: auto;
}

.bin-card {
  padding: 16px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
}

.bin-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.bin-card.selected {
  border-color: #409eff;
  background: #ecf5ff;
}

.bin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.bin-code {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.bin-info {
  margin-bottom: 12px;
  color: #606266;
  font-size: 13px;
}

.bin-location {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.bin-progress {
  display: flex;
  align-items: center;
  gap: 12px;
}

.weight-text {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.unit {
  margin-left: 8px;
  color: #909399;
}

.placeholder {
  color: #c0c4cc;
}
</style>
