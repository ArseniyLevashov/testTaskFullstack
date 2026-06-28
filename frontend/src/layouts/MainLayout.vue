<template>
  <v-app-bar color="primary" density="comfortable">
    <v-app-bar-nav-icon @click="drawer = !drawer" />
    <v-app-bar-title>Test Task</v-app-bar-title>

    <v-spacer />

    <!-- Имя текущего юзера и его роль -->
    <span class="mr-4">
      {{ authStore.username }}
      <v-chip size="small" class="ml-2" :color="authStore.isAdmin ? 'amber' : 'grey-lighten-1'">
        {{ authStore.isAdmin ? 'ADMIN' : 'USER' }}
      </v-chip>
    </span>

    <v-btn icon="mdi-logout" @click="handleLogout" />
  </v-app-bar>

  <v-navigation-drawer v-model="drawer">
    <v-list nav>
      <v-list-item prepend-icon="mdi-view-dashboard" title="Dashboard" :to="{ name: 'dashboard' }"/>
      <v-list-item v-if="authStore.isAdmin" prepend-icon="mdi-database" title="Данные" :to="{ name: 'data' }"/>
    </v-list>
  </v-navigation-drawer>

  <v-main>
    <v-container fluid>
      <router-view />
    </v-container>
  </v-main>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const drawer = ref(true)

function handleLogout() {
  authStore.logout()
  router.push({ name: 'login' })
}
</script>