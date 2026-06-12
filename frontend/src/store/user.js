import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const role = ref('RESIDENT')

  const currentUser = computed(() => user.value)
  const currentRole = computed(() => role.value)

  const setUser = (userData) => {
    user.value = userData
  }

  const setRole = (newRole) => {
    role.value = newRole
  }

  const clearUser = () => {
    user.value = null
  }

  return {
    user,
    role,
    currentUser,
    currentRole,
    setUser,
    setRole,
    clearUser
  }
})
