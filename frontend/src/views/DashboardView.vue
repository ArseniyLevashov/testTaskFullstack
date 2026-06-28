<template>
  <div>
    <h1 class="text-h4 mb-6">Dashboard</h1>

    <v-row>
      <!-- Карточка статуса -->
      <v-col cols="12" md="4">
        <v-card>
          <v-card-text class="text-center">
            <v-icon size="48" :color="statusColor">{{ statusIcon }}</v-icon>
            <div class="text-h6 mt-2">Статус сервиса</div>
            <div class="text-h5 mt-1" :class="`text-${statusColor}`">
              {{ statusText }}
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Карточка счётчика -->
      <v-col cols="12" md="4">
        <v-card>
          <v-card-text class="text-center">
            <v-icon size="48" color="primary">mdi-counter</v-icon>
            <div class="text-h6 mt-2">Записей загружено</div>
            <div class="text-h4 mt-1">{{ records.length }}</div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Карточка времени обновления -->
      <v-col cols="12" md="4">
        <v-card>
          <v-card-text class="text-center">
            <v-icon size="48" color="info">mdi-clock-outline</v-icon>
            <div class="text-h6 mt-2">Обновлено</div>
            <div class="text-body-1 mt-1">{{ lastUpdated }}</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Последние данные -->
    <v-card class="mt-6">
      <v-card-title>Последние полученные данные</v-card-title>
      <v-card-text>
        <v-alert v-if="errorMessage" type="error" variant="tonal" class="mb-4">
          {{ errorMessage }}
        </v-alert>

        <v-progress-linear v-if="loading" indeterminate color="primary" />

        <v-list v-else-if="records.length > 0">
          <v-list-item v-for="record in records" :key="record.id" :title="truncate(record.payload)"
              :subtitle="formatDate(record.createdAt)">
            <template #prepend>
              <v-icon :color="record.success ? 'success' : 'error'">
                {{ record.success ? 'mdi-check-circle' : 'mdi-alert-circle' }}
              </v-icon>
            </template>
          </v-list-item>
        </v-list>

        <div v-else class="text-center text-grey py-4">
          Нет данных
        </div>
      </v-card-text>

      <v-card-actions>
        <v-btn color="primary" variant="text" @click="loadData" :loading="loading">
          Обновить
        </v-btn>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { dataApi } from '@/api/dataApi'

const loading = ref(false)
const records = ref([])
const status = ref(null)
const errorMessage = ref('')
const lastUpdated = ref('—')

const statusText = computed(() => (status.value === 'UP' ? 'Работает' : 'Недоступен'))
const statusColor = computed(() => (status.value === 'UP' ? 'success' : 'error'))
const statusIcon = computed(() =>
    status.value === 'UP' ? 'mdi-check-circle' : 'mdi-alert-circle'
)

async function loadData() {
  loading.value = true
  errorMessage.value = ''

  try {
    // Параллельно грузим статус и данные
    const [statusRes, dataRes] = await Promise.all([
      dataApi.getStatus(),
      dataApi.getLast10(),
    ])

    status.value = statusRes.data.status
    records.value = dataRes.data
    lastUpdated.value = new Date().toLocaleTimeString('ru-RU')
  } catch (error) {
    if (error.code === 'ERR_NETWORK') {
      errorMessage.value = 'Бэкенд недоступен'
      status.value = 'DOWN'
    } else {
      errorMessage.value = 'Ошибка загрузки данных'
    }
  } finally {
    loading.value = false
  }
}

function truncate(text, max = 80) {
  if (!text) return '(пусто)'
  return text.length > max ? text.slice(0, max) + '...' : text
}

function formatDate(iso) {
  if (!iso) return ''
  return new Date(iso).toLocaleString('ru-RU')
}

onMounted(() => {
  loadData()
})
</script>