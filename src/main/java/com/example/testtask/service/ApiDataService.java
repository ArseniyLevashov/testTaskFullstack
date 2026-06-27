package com.example.testtask.service;

import com.example.testtask.dto.ApiDataRequestDTO;
import com.example.testtask.entity.ApiDataEntity;
import com.example.testtask.repository.ApiDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiDataService {

    private final ApiDataRepository repository;

    public List<ApiDataEntity> getLast10() {
        return repository.findAll(
                PageRequest.of(0, 10, Sort.by("createdAt").descending())
        ).getContent();
    }

    public Optional<ApiDataEntity> getById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public ApiDataEntity create(ApiDataRequestDTO request) {
        ApiDataEntity entity = ApiDataEntity.builder()
                .createdAt(Instant.now())
                .success(request.success())
                .payload(request.payload())
                .build();
        return repository.save(entity);
    }

    @Transactional
    public Optional<ApiDataEntity> update(UUID id, ApiDataRequestDTO request) {
        return repository.findById(id).map(existing -> {
            existing.setSuccess(request.success());
            existing.setPayload(request.payload());
            return existing;
        });
    }

    @Transactional
    public boolean delete(UUID id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}