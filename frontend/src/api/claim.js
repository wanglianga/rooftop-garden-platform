import request from '@/utils/request'

export function getClaims() {
  return request({
    url: '/claims',
    method: 'get'
  })
}

export function getClaimById(id) {
  return request({
    url: `/claims/${id}`,
    method: 'get'
  })
}

export function getClaimsByUserId(userId) {
  return request({
    url: `/claims/user/${userId}`,
    method: 'get'
  })
}

export function getUserActiveClaims(userId) {
  return request({
    url: `/claims/user/${userId}/active`,
    method: 'get'
  })
}

export function getClaimsByPlotId(plotId) {
  return request({
    url: `/claims/plot/${plotId}`,
    method: 'get'
  })
}

export function createClaim(data) {
  return request({
    url: '/claims',
    method: 'post',
    data
  })
}

export function approveClaim(id) {
  return request({
    url: `/claims/${id}/approve`,
    method: 'post'
  })
}

export function rejectClaim(id, reason) {
  return request({
    url: `/claims/${id}/reject`,
    method: 'post',
    params: { reason }
  })
}

export function expireClaim(id) {
  return request({
    url: `/claims/${id}/expire`,
    method: 'post'
  })
}

export function updateClaim(id, data) {
  return request({
    url: `/claims/${id}`,
    method: 'put',
    data
  })
}
