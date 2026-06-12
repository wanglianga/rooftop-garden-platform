import request from '@/utils/request'

export function getBins() {
  return request({
    url: '/compost/bins',
    method: 'get'
  })
}

export function getBinById(id) {
  return request({
    url: `/compost/bins/${id}`,
    method: 'get'
  })
}

export function createBin(data) {
  return request({
    url: '/compost/bins',
    method: 'post',
    data
  })
}

export function updateBin(id, data) {
  return request({
    url: `/compost/bins/${id}`,
    method: 'put',
    data
  })
}

export function emptyBin(id) {
  return request({
    url: `/compost/bins/${id}/empty`,
    method: 'post'
  })
}

export function getDeliveries() {
  return request({
    url: '/compost/deliveries',
    method: 'get'
  })
}

export function getDeliveriesByBinId(binId) {
  return request({
    url: `/compost/deliveries/bin/${binId}`,
    method: 'get'
  })
}

export function getDeliveriesByUserId(userId) {
  return request({
    url: `/compost/deliveries/user/${userId}`,
    method: 'get'
  })
}

export function getUncollectedDeliveries() {
  return request({
    url: '/compost/deliveries/uncollected',
    method: 'get'
  })
}

export function createDelivery(data) {
  return request({
    url: '/compost/deliveries',
    method: 'post',
    data
  })
}

export function collectDelivery(id, collectorId) {
  return request({
    url: `/compost/deliveries/${id}/collect`,
    method: 'post',
    params: { collectorId }
  })
}
