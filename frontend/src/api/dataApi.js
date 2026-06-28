import http from './http'

export const dataApi = {
    // Статус сервиса
    getStatus() {
        return http.get('/status')
    },

    // Последние 10 записей
    getLast10() {
        return http.get('/data')
    },

    // Получить запись по id
    getById(id) {
        return http.get(`/data/${id}`)
    },

    // Создать
    create(payload) {
        return http.post('/data', payload)
    },

    // Обновить
    update(id, payload) {
        return http.put(`/data/${id}`, payload)
    },

    // Удалить
    remove(id) {
        return http.delete(`/data/${id}`)
    },
}