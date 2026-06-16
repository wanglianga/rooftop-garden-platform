import request from '@/utils/request'

export function getAllContaminationRecords() {
  return request({
    url: '/contamination',
    method: 'get'
  })
}

export function getContaminationRecordById(id) {
  return request({
    url: `/contamination/${id}`,
    method: 'get'
  })
}

export function getContaminationRecordsByUserId(userId) {
  return request({
    url: `/contamination/user/${userId}`,
    method: 'get'
  })
}

export function getContaminationRecordsByBinId(binId) {
  return request({
    url: `/contamination/bin/${binId}`,
    method: 'get'
  })
}

export function getContaminationRecordsByBatchId(batchId) {
  return request({
    url: `/contamination/batch/${batchId}`,
    method: 'get'
  })
}

export function getContaminationRecordsByStatus(status) {
  return request({
    url: `/contamination/status/${status}`,
    method: 'get'
  })
}

export function getPendingContaminationRecords() {
  return request({
    url: '/contamination/pending',
    method: 'get'
  })
}

export function reportContamination(data) {
  return request({
    url: '/contamination/report',
    method: 'post',
    data
  })
}

export function assignGardenerToContamination(id, gardenerId) {
  return request({
    url: `/contamination/${id}/assign-gardener`,
    method: 'post',
    params: { gardenerId }
  })
}

export function gardenerDisposeContamination(id, data) {
  return request({
    url: `/contamination/${id}/gardener-dispose`,
    method: 'post',
    data
  })
}

export function pauseUserPoints(id, data) {
  return request({
    url: `/contamination/${id}/pause-points`,
    method: 'post',
    data
  })
}

export function recoverUserPoints(id, propertyApproverId) {
  return request({
    url: `/contamination/${id}/recover-points`,
    method: 'post',
    params: { propertyApproverId }
  })
}

export function updateContaminationFinalResult(id, finalResult) {
  return request({
    url: `/contamination/${id}/update-final-result`,
    method: 'post',
    data: { finalResult }
  })
}

export function updateContaminationRecord(id, data) {
  return request({
    url: `/contamination/${id}`,
    method: 'put',
    data
  })
}
