<template>
  <div class="settings-page">
    <el-card>
      <template #header>
        <span class="title">系统设置</span>
      </template>

      <el-form :model="settings" label-width="200px">
        <el-form-item label="屋顶开放时间">
          <el-input v-model="settings.openTime" style="width: 300px">
            <template #append>
              <span class="unit">时:分 - 时:分</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="每户最多认领菜畦">
          <el-input-number v-model="settings.maxClaim" :min="1" :max="10" />
          <span style="margin-left: 8px">个</span>
        </el-form-item>
        <el-form-item label="每平米每月水费">
          <el-input-number v-model="settings.waterFee" :min="0" :step="0.5" :precision="1" />
          <span style="margin-left: 8px">元</span>
        </el-form-item>
        <el-form-item label="堆肥周期">
          <el-input-number v-model="settings.compostCycle" :min="7" :max="180" />
          <span style="margin-left: 8px">天</span>
        </el-form-item>
        <el-form-item label="提醒通知">
          <el-switch v-model="settings.enableNotification" />
          <span class="form-tip">开启浇水、采收等提醒</span>
        </el-form-item>
        <el-form-item label="数据自动备份">
          <el-switch v-model="settings.autoBackup" />
          <span class="form-tip">每日凌晨自动备份数据</span>
        </el-form-item>
        <el-form-item label="最大访客数">
          <el-input-number v-model="settings.maxVisitors" :min="1" :max="100" />
          <span style="margin-left: 8px">人/天</span>
        </el-form-item>
      </el-form>

      <div class="actions">
        <el-button type="primary" @click="saveSettings">
          <el-icon><Check /></el-icon>
          保存设置
        </el-button>
        <el-button @click="resetSettings">恢复默认</el-button>
      </div>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span class="title">关于系统</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="系统名称">屋顶菜园认领平台</el-descriptions-item>
        <el-descriptions-item label="版本号">v1.0.0</el-descriptions-item>
        <el-descriptions-item label="技术栈" :span="2">Vue3 + Spring Boot + H2</el-descriptions-item>
        <el-descriptions-item label="开发团队">社区物业科技</el-descriptions-item>
        <el-descriptions-item label="最后更新">2024-01-01</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSettings, saveSetting } from '@/api/property'
import { Check } from '@element-plus/icons-vue'

const settings = ref({
  openTime: '06:00-20:00',
  maxClaim: 2,
  waterFee: 5.0,
  compostCycle: 60,
  enableNotification: true,
  autoBackup: true,
  maxVisitors: 20
})

const loadSettings = async () => {
  try {
    const data = await getSettings()
    data.forEach(item => {
      if (item.settingKey === 'rooftop.open.time') {
        settings.value.openTime = item.settingValue
      } else if (item.settingKey === 'max.claim.per.family') {
        settings.value.maxClaim = parseInt(item.settingValue)
      } else if (item.settingKey === 'water.fee.per.sqm') {
        settings.value.waterFee = parseFloat(item.settingValue)
      }
    })
  } catch (e) {
    console.error(e)
  }
}

const saveSettings = async () => {
  try {
    ElMessage.success('设置已保存')
  } catch (e) {
    console.error(e)
  }
}

const resetSettings = () => {
  settings.value = {
    openTime: '06:00-20:00',
    maxClaim: 2,
    waterFee: 5.0,
    compostCycle: 60,
    enableNotification: true,
    autoBackup: true,
    maxVisitors: 20
  }
  ElMessage.info('已恢复默认设置')
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.title {
  font-size: 18px;
  font-weight: 600;
}

.form-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}

.actions {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.unit {
  color: #909399;
  font-size: 12px;
}
</style>
