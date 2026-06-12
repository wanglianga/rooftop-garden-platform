import request from '@/utils/request'

export function getPlantings() {
  return request({
    url: '/plantings',
    method: 'get'
  })
}

export function getPlantingById(id) {
  return request({
    url: `/plantings/${id}`,
    method: 'get'
  })
}

export function getPlantingsByPlotId(plotId) {
  return request({
    url: `/plantings/plot/${plotId}`,
    method: 'get'
  })
}

export function getPlantingsByUserId(userId) {
  return request({
    url: `/plantings/user/${userId}`,
    method: 'get'
  })
}

export function createPlanting(data) {
  return request({
    url: '/plantings',
    method: 'post',
    data
  })
}

export function updatePlanting(id, data) {
  return request({
    url: `/plantings/${id}`,
    method: 'put',
    data
  })
}

export function updatePlantingStatus(id, status) {
  return request({
    url: `/plantings/${id}/status`,
    method: 'patch',
    params: { status }
  })
}

export function completePlanting(id) {
  return request({
    url: `/plantings/${id}/complete`,
    method: 'post'
  })
}

export function getCareLogs() {
  return request({
    url: '/care-logs',
    method: 'get'
  })
}

export function getCareLogsByPlotId(plotId) {
  return request({
    url: `/care-logs/plot/${plotId}`,
    method: 'get'
  })
}

export function getCareLogsByPlantingId(plantingId) {
  return request({
    url: `/care-logs/planting/${plantingId}`,
    method: 'get'
  })
}

export function createCareLog(data) {
  return request({
    url: '/care-logs',
    method: 'post',
    data
  })
}
