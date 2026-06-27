package com.example.testtask.service;

import com.example.testtask.entity.ApiDataEntity;
import com.example.testtask.repository.ApiDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalApiPollingService {

    private static final String SUCCESS_TOPIC = "api-data";
    private static final String ERROR_TOPIC = "api-errors";

    private final RestClient restClient;
    private final ApiDataRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${external-api.url}")
    private String apiUrl;

    @Scheduled(fixedRateString = "${external-api.poll-rate-ms:60000}")
    public void pollApi() {
        String response = fetchWithRetry();
        if (response != null) {
            saveAndPublish(response, SUCCESS_TOPIC, true);
        }
    }

    @Retryable(
            retryFor = RestClientException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public String fetchWithRetry() {
        log.info("Опрашиваю внешний API: {}", apiUrl);
        return restClient.get()
                .uri(apiUrl)
                .retrieve()
                .body(String.class);
    }

    @Recover
    public String recover(RestClientException e) {
        log.error("Все попытки опроса API провалились: {}", e.getMessage());
        saveAndPublish("Error after retries: " + e.getMessage(), ERROR_TOPIC, false);
        return null;
    }

    @Transactional
    public void saveAndPublish(String payload, String topic, boolean success) {
        ApiDataEntity entity = ApiDataEntity.builder()
                .createdAt(Instant.now())
                .success(success)
                .payload(payload)
                .build();
        ApiDataEntity saved = repository.save(entity);
        kafkaTemplate.send(topic, saved.getId().toString(), payload);
        log.info("Сохранил запись {} и опубликовал в топик '{}'", saved.getId(), topic);
    }
}