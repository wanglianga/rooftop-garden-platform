import request from '@/utils/request'

export function getPlots() {
  return request({
    url: '/plots',
    method: 'get'
  })
}

export function getPlotById(id) {
  return request({
    url: `/plots/${id}`,
    method: 'get'
  })
}

export function getPlotsByStatus(status) {
  return request({
    url: `/plots/status/${status}`,
    method: 'get'
  })
}

export function createPlot(data) {
  return request({
    url: '/plots',
    method: 'post',
    data
  })
}

export function updatePlot(id, data) {
  return request({
    url: `/plots/${id}`,
    method: 'put',
    data
  })
}

export function updatePlotStatus(id, status) {
  return request({
    url: `/plots/${id}/status`,
    method: 'patch',
    params: { status }
  })
}

export function getAvailablePlots() {
  return request({
    url: '/plots/available',
    method: 'get'
  })
}
