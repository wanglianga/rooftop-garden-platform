import request from '@/utils/request'

export function getBatches() {
  return request({
    url: '/compost-batches',
    method: 'get'
  })
}

export function getBatchById(id) {
  return request({
    url: `/compost-batches/${id}`,
    method: 'get'
  })
}

export function getBatchesByStatus(status) {
  return request({
    url: `/compost-batches/status/${status}`,
    method: 'get'
  })
}

export function createBatch(data) {
  return request({
    url: '/compost-batches',
    method: 'post',
    data
  })
}

export function updateBatch(id, data) {
  return request({
    url: `/compost-batches/${id}`,
    method: 'put',
    data
  })
}

export function startFermentation(id) {
  return request({
    url: `/compost-batches/${id}/ferment`,
    method: 'post'
  })
}

export function turnCompost(id) {
  return request({
    url: `/compost-batches/${id}/turn`,
    method: 'post'
  })
}

export function markMature(id) {
  return request({
    url: `/compost-batches/${id}/mature`,
    method: 'post'
  })
}

export function applyToPlot(id, plotId, destination) {
  return request({
    url: `/compost-batches/${id}/apply`,
    method: 'post',
    params: { plotId, destination }
  })
}
