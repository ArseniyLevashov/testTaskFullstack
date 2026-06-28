<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card class="pa-4" elevation="8">
          <v-card-title class="text-h5 text-center mb-4">
            Вход в систему
          </v-card-title>

          <v-card-text>
            <!-- Сообщение об ошибке -->
            <v-alert v-if="errorMessage" type="error" variant="tonal" class="mb-4" closable
                @click:close="errorMessage = ''">
              {{ errorMessage }}
            </v-alert>

            <v-form @submit.prevent="handleLogin">
              <v-text-field v-model="username" label="Имя пользователя" prepend-inner-icon="mdi-account"
                  variant="outlined" :disabled="loading" required />

              <v-text-field v-model="password" label="Пароль" type="password" prepend-inner-icon="mdi-lock"
                  variant="outlined" :disabled="loading" required/>

              <v-btn type="submit" color="primary" size="large" block :loading="loading"
                  class="mt-2">
                Войти
              </v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const loading = ref(false)
const errorMessage = ref('')

async function handleLogin() {
  errorMessage.value = ''
  loading.value = true

  try {
    await authStore.login(username.value, password.value)

    router.push({ name: 'dashboard' })
  } catch (error) {
    if (error.response?.status === 401) {
      errorMessage.value = 'Неверное имя пользователя или пароль'
    } else if (error.code === 'ERR_NETWORK') {
      errorMessage.value = 'Сервер недоступен. Проверьте, запущен ли бэкенд.'
    } else {
      errorMessage.value = 'Ошибка входа. Попробуйте ещё раз.'
    }
  } finally {
    loading.value = false
  }
}
</script>