import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import http from '@/api/http'

export const useAuthStore = defineStore('auth', () => {
    const token = ref(localStorage.getItem('token') || null)
    const username = ref(localStorage.getItem('username') || null)
    const role = ref(localStorage.getItem('role') || null)

    const isAuthenticated = computed(() => !!token.value)
    const isAdmin = computed(() => role.value === 'ROLE_ADMIN')

    async function login(user, password) {
        const response = await http.post('/auth/login', {
            username: user,
            password: password,
        })

        const { token: receivedToken, username: receivedUsername, role: receivedRole } = response.data

        // Сохраняем в реактивный state
        token.value = receivedToken
        username.value = receivedUsername
        role.value = receivedRole

        // И в localStorage, чтобы пережить перезагрузку страницы
        localStorage.setItem('token', receivedToken)
        localStorage.setItem('username', receivedUsername)
        localStorage.setItem('role', receivedRole)
    }

    function logout() {
        token.value = null
        username.value = null
        role.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        localStorage.removeItem('role')
    }

    return {
        token,
        username,
        role,
        isAuthenticated,
        isAdmin,
        login,
        logout,
    }
})