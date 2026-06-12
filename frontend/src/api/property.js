import request from '@/utils/request'

export function getInspections() {
  return request({
    url: '/property/inspections',
    method: 'get'
  })
}

export function getInspectionById(id) {
  return request({
    url: `/property/inspections/${id}`,
    method: 'get'
  })
}

export function getUnhandledInspections() {
  return request({
    url: '/property/inspections/unhandled',
    method: 'get'
  })
}

export function createInspection(data) {
  return request({
    url: '/property/inspections',
    method: 'post',
    data
  })
}

export function handleInspection(id, handledBy, remark) {
  return request({
    url: `/property/inspections/${id}/handle`,
    method: 'post',
    params: { handledBy, remark }
  })
}

export function getToolKeys() {
  return request({
    url: '/property/tool-keys',
    method: 'get'
  })
}

export function getToolKeyById(id) {
  return request({
    url: `/property/tool-keys/${id}`,
    method: 'get'
  })
}

export function getAvailableToolKeys() {
  return request({
    url: '/property/tool-keys/available',
    method: 'get'
  })
}

export function createToolKey(data) {
  return request({
    url: '/property/tool-keys',
    method: 'post',
    data
  })
}

export function borrowKey(id, borrowerId, expectedHours) {
  return request({
    url: `/property/tool-keys/${id}/borrow`,
    method: 'post',
    params: { borrowerId, expectedHours }
  })
}

export function returnKey(id) {
  return request({
    url: `/property/tool-keys/${id}/return`,
    method: 'post'
  })
}

export function getVisitors() {
  return request({
    url: '/property/visitors',
    method: 'get'
  })
}

export function getVisitorById(id) {
  return request({
    url: `/property/visitors/${id}`,
    method: 'get'
  })
}

export function createVisitor(data) {
  return request({
    url: '/property/visitors',
    method: 'post',
    data
  })
}

export function checkInVisitor(id) {
  return request({
    url: `/property/visitors/${id}/checkin`,
    method: 'post'
  })
}

export function checkOutVisitor(id) {
  return request({
    url: `/property/visitors/${id}/checkout`,
    method: 'post'
  })
}

export function getSettings() {
  return request({
    url: '/property/settings',
    method: 'get'
  })
}

export function getSettingByKey(key) {
  return request({
    url: `/property/settings/${key}`,
    method: 'get'
  })
}

export function saveSetting(data) {
  return request({
    url: '/property/settings',
    method: 'post',
    data
  })
}
