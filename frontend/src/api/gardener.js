import request from '@/utils/request'

export function getSoilReports() {
  return request({
    url: '/gardener/soil-reports',
    method: 'get'
  })
}

export function getSoilReportById(id) {
  return request({
    url: `/gardener/soil-reports/${id}`,
    method: 'get'
  })
}

export function getSoilReportsByPlotId(plotId) {
  return request({
    url: `/gardener/soil-reports/plot/${plotId}`,
    method: 'get'
  })
}

export function createSoilReport(data) {
  return request({
    url: '/gardener/soil-reports',
    method: 'post',
    data
  })
}

export function updateSoilReport(id, data) {
  return request({
    url: `/gardener/soil-reports/${id}`,
    method: 'put',
    data
  })
}

export function getPestRecords() {
  return request({
    url: '/gardener/pest-records',
    method: 'get'
  })
}

export function getPestRecordById(id) {
  return request({
    url: `/gardener/pest-records/${id}`,
    method: 'get'
  })
}

export function getPestRecordsByPlotId(plotId) {
  return request({
    url: `/gardener/pest-records/plot/${plotId}`,
    method: 'get'
  })
}

export function createPestRecord(data) {
  return request({
    url: '/gardener/pest-records',
    method: 'post',
    data
  })
}

export function updatePestRecord(id, data) {
  return request({
    url: `/gardener/pest-records/${id}`,
    method: 'put',
    data
  })
}

export function getSuggestions() {
  return request({
    url: '/gardener/suggestions',
    method: 'get'
  })
}

export function getSuggestionById(id) {
  return request({
    url: `/gardener/suggestions/${id}`,
    method: 'get'
  })
}

export function getUnhandledSuggestions() {
  return request({
    url: '/gardener/suggestions/unhandled',
    method: 'get'
  })
}

export function createSuggestion(data) {
  return request({
    url: '/gardener/suggestions',
    method: 'post',
    data
  })
}

export function handleSuggestion(id, feedback, feedbackBy) {
  return request({
    url: `/gardener/suggestions/${id}/handle`,
    method: 'post',
    params: { feedback, feedbackBy }
  })
}

export function getSeedlingRecords() {
  return request({
    url: '/gardener/seedling-records',
    method: 'get'
  })
}

export function getSeedlingRecordById(id) {
  return request({
    url: `/gardener/seedling-records/${id}`,
    method: 'get'
  })
}

export function getSeedlingRecordsByPlotId(plotId) {
  return request({
    url: `/gardener/seedling-records/plot/${plotId}`,
    method: 'get'
  })
}

export function createSeedlingRecord(data) {
  return request({
    url: '/gardener/seedling-records',
    method: 'post',
    data
  })
}

export function updateSeedlingRecord(id, data) {
  return request({
    url: `/gardener/seedling-records/${id}`,
    method: 'put',
    data
  })
}
