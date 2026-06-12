import request from '@/utils/request'

export function getHarvests() {
  return request({
    url: '/harvests',
    method: 'get'
  })
}

export function getHarvestById(id) {
  return request({
    url: `/harvests/${id}`,
    method: 'get'
  })
}

export function getHarvestsByPlotId(plotId) {
  return request({
    url: `/harvests/plot/${plotId}`,
    method: 'get'
  })
}

export function getHarvestsByUserId(userId) {
  return request({
    url: `/harvests/user/${userId}`,
    method: 'get'
  })
}

export function getHarvestsByDistributionType(type) {
  return request({
    url: `/harvests/distribution/${type}`,
    method: 'get'
  })
}

export function createHarvest(data) {
  return request({
    url: '/harvests',
    method: 'post',
    data
  })
}

export function updateHarvest(id, data) {
  return request({
    url: `/harvests/${id}`,
    method: 'put',
    data
  })
}

export function deleteHarvest(id) {
  return request({
    url: `/harvests/${id}`,
    method: 'delete'
  })
}
