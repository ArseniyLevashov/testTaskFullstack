<template>
  <div>
    <div class="d-flex align-center mb-6">
      <h1 class="text-h4">Управление данными</h1>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreateDialog">
        Создать
      </v-btn>
    </div>

    <!-- Уведомление -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="3000">
      {{ snackbar.text }}
    </v-snackbar>

    <!-- Таблица -->
    <v-card>
      <v-data-table :headers="headers" :items="records" :loading="loading" loading-text="Загрузка..."
                    no-data-text="Нет записей">
        <!-- Кастомная колонка статуса -->
        <template #item.success="{ item }">
          <v-chip :color="item.success ? 'success' : 'error'" size="small">
            {{ item.success ? 'Успех' : 'Ошибка' }}
          </v-chip>
        </template>

        <!-- Кастомная колонка payload (обрезаем длинный текст) -->
        <template #item.payload="{ item }">
          {{ truncate(item.payload) }}
        </template>

        <!-- Кастомная колонка даты -->
        <template #item.createdAt="{ item }">
          {{ formatDate(item.createdAt) }}
        </template>

        <!-- Кастомная колонка действий -->
        <template #item.actions="{ item }">
          <v-icon size="small" class="mr-2" @click="openEditDialog(item)">
            mdi-pencil
          </v-icon>
          <v-icon size="small" color="error" @click="openDeleteDialog(item)">
            mdi-delete
          </v-icon>
        </template>
      </v-data-table>
    </v-card>

    <!-- Диалог создания/редактирования -->
    <v-dialog v-model="editDialog" max-width="500">
      <v-card>
        <v-card-title>
          {{ isEditing ? 'Редактировать запись' : 'Новая запись' }}
        </v-card-title>
        <v-card-text>
          <v-text-field v-model="form.payload" label="Payload" variant="outlined" :disabled="saving"/>
          <v-switch v-model="form.success" label="Успешно" color="success" :disabled="saving"/>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" :disabled="saving" @click="editDialog = false">
            Отмена
          </v-btn>
          <v-btn color="primary" :loading="saving" @click="save">
            Сохранить
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Диалог подтверждения удаления -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title>Подтверждение</v-card-title>
        <v-card-text>
          Удалить запись? Это действие нельзя отменить.
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" :disabled="deleting" @click="deleteDialog = false">
            Отмена
          </v-btn>
          <v-btn color="error" :loading="deleting" @click="confirmDelete">
            Удалить
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { dataApi } from '@/api/dataApi'

const records = ref([])
const loading = ref(false)

const headers = [
  { title: 'Статус', key: 'success' },
  { title: 'Payload', key: 'payload' },
  { title: 'Создано', key: 'createdAt' },
  { title: 'Действия', key: 'actions', sortable: false },
]

const editDialog = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const form = reactive({
  id: null,
  payload: '',
  success: true,
})

const deleteDialog = ref(false)
const deleting = ref(false)
const itemToDelete = ref(null)

const snackbar = reactive({
  show: false,
  text: '',
  color: 'success',
})

function notify(text, color = 'success') {
  snackbar.text = text
  snackbar.color = color
  snackbar.show = true
}

async function loadData() {
  loading.value = true
  try {
    const res = await dataApi.getLast10()
    records.value = res.data
  } catch (error) {
    notify('Ошибка загрузки данных', 'error')
  } finally {
    loading.value = false
  }
}

function openCreateDialog() {
  isEditing.value = false
  form.id = null
  form.payload = ''
  form.success = true
  editDialog.value = true
}

function openEditDialog(item) {
  isEditing.value = true
  form.id = item.id
  form.payload = item.payload
  form.success = item.success
  editDialog.value = true
}

async function save() {
  saving.value = true
  try {
    const payload = { payload: form.payload, success: form.success }

    if (isEditing.value) {
      await dataApi.update(form.id, payload)
      notify('Запись обновлена')
    } else {
      await dataApi.create(payload)
      notify('Запись создана')
    }

    editDialog.value = false
    await loadData()
  } catch (error) {
    notify('Ошибка сохранения', 'error')
  } finally {
    saving.value = false
  }
}

function openDeleteDialog(item) {
  itemToDelete.value = item
  deleteDialog.value = true
}

async function confirmDelete() {
  deleting.value = true
  try {
    await dataApi.remove(itemToDelete.value.id)
    notify('Запись удалена')
    deleteDialog.value = false
    await loadData()
  } catch (error) {
    notify('Ошибка удаления', 'error')
  } finally {
    deleting.value = false
  }
}

function truncate(text, max = 50) {
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