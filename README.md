## Test Task — Spring Boot + Vue 3

Сервис периодического опроса внешнего API с публикацией результатов в Kafka, сохранением в PostgreSQL и REST-интерфейсом, защищённым JWT. Frontend на Vue 3 предоставляет дашборд и CRUD над сохранёнными данными.

## Поток данных опроса:
1. `@Scheduled`-задача раз в минуту опрашивает внешний API (catfact.ninja).
2. При сбое — `@Retryable` повторяет до 3 раз с экспоненциальной задержкой.
3. Успех → запись в PostgreSQL + публикация в Kafka-топик `api-data`.
4. Провал всех попыток → `@Recover` пишет запись с `success=false` + Kafka-топик `api-errors`.


## Поток аутентификации:
1. `POST /auth/login` с логином/паролем → проверка через BCrypt → выдача JWT.
2. Каждый защищённый запрос несёт `Authorization: Bearer <jwt>`.
3. JWT-фильтр валидирует токен, роль читается из БД, проверяется доступ.

## Технологии

## Backend


Java 21, Spring Boot 4.1
Spring Web — REST-контроллеры
Spring Data JPA + Hibernate — доступ к данным
Spring Security + JWT (jjwt) — аутентификация и авторизация
Spring Kafka — публикация сообщений
Spring Retry — повторные попытки опроса API
PostgreSQL — основная БД
Liquibase — версионирование схемы БД
springdoc-openapi — Swagger UI / OpenAPI 3
Testcontainers — Postgres для разработки и тестов
spring-boot-docker-compose — Kafka для разработки
Maven — сборка


## Frontend


Vue 3 (Composition API, `<script setup>`)
Vite — сборщик и dev-сервер
Vuetify — UI-компоненты (Material Design)
Pinia — стейт-менеджмент
Vue Router — маршрутизация и guards
Axios — HTTP-клиент


## Принятые технические решения

Postgres через Testcontainers, Kafka через Docker Compose - Для Postgres `@ServiceConnection` работает идеально. Для Kafka в связке Spring Boot 4.1 + Testcontainers 2.x была проблема с автоконфигурацией `KafkaConnectionDetails`, поэтому выбран `spring-boot-docker-compose` — оба официальные механизмы Spring Boot для dev-инфраструктуры.

JWT без роли внутри токена - Роль читается из БД при каждом запросе. Это исключает рассинхрон: если роль изменили в БД, токен не «застрянет» со старым значением. Цена — обращение к БД на каждый запрос.

Роль в ответе `/auth/login` - Только для UX фронта (показать/скрыть админские кнопки). Реальная авторизация всегда на бэкенде по JWT + БД.

Liquibase отдельными SQL-changeset'ами - Один changeset = одно атомарное изменение. Чёткая история миграций, точечный rollback, отсутствие конфликтов checksums.

`Instant` для timestamp - Хранение момента времени в UTC, конвертация в локальное время — на отображении. Стандарт для распределённых систем.

Constructor injection везде - Поля `final`, тестируемость без Spring-контекста, явные зависимости.

CORS на бэкенде - Production-подход (в отличие от dev-прокси Vite). Конкретные origins (не `*`), т.к. `allowCredentials=true`.




## Запуск: Backend

Предварительные требования

JDK 21
Docker Desktop (запущен) — нужен для Postgres (Testcontainers) и Kafka (Docker Compose)
Maven (или встроенный `mvnw`)


## Настройка БД

Отдельная настройка не требуется. При запуске:

PostgreSQL автоматически поднимается в контейнере через Testcontainers (образ `postgres:16-alpine`).
Схема БД создаётся автоматически через Liquibase (4 changeset'а: таблицы `users`, `api_data`, индексы, seed-пользователи).
Подключение настраивается через `@ServiceConnection` — порт и host контейнера подставляются автоматически.


## Настройка Kafka

Отдельная настройка не требуется. При запуске spring-boot-docker-compose автоматически выполняет `docker compose up` из `compose.yaml` и поднимает Kafka в KRaft-режиме (без ZooKeeper) на порту 9092. Топики `api-data` и `api-errors` создаются автоматически при старте приложения.

Запуск приложения

Для локальной разработки запускается класс `TestTaskApplication` из `src/test` (он поднимает Postgres через Testcontainers)

## Через IDE: запустить TestTaskApplication (src/test/java/.../TestTaskApplication.java)

## Или через Maven:

./mvnw spring-boot:test-run


## Запуск: Frontend

Предварительные требования


Node.js 20+ LTS (рекомендуется 24 LTS)


## Установка и запуск

```bash
cd frontend
npm install
npm run dev
```

Приложение откроется на http://localhost:3000.

## Настройка API URL

Базовый URL бэкенда задаётся в `frontend/src/api/http.js`:

```javascript
const http = axios.create({
baseURL: 'http://localhost:8080',
// ...
})
```

Для смены адреса бэкенда (например, при деплое) меняется значение `baseURL`. CORS на бэкенде настроен на origins `http://localhost:3000\` и `http://127.0.0.1:3000\` — при смене адреса фронта нужно добавить новый origin в `SecurityConfig.corsConfigurationSource()`.


## API и документация (Swagger)

После запуска бэкенда документация доступна по адресам:


Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs


## Учётные данные

Логин     Пароль       Роль

`admin`  `admin123`    ROLE_ADMIN

`user`  `user123`      ROLE_USER
