package com.example.testtask.service;

import com.example.testtask.dto.ApiDataRequestDTO;
import com.example.testtask.entity.ApiDataEntity;
import com.example.testtask.repository.ApiDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ApiDataServiceTest {

    @Mock
    private ApiDataRepository repository;

    @InjectMocks
    private ApiDataService service;

    @Test
    void create_shouldBuildEntityAndSave() {
        ApiDataRequestDTO request = new ApiDataRequestDTO(true, "test payload");

        when(repository.save(any(ApiDataEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ApiDataEntity result = service.create(request);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getPayload()).isEqualTo("test payload");
        assertThat(result.getCreatedAt()).isNotNull();

        verify(repository).save(any(ApiDataEntity.class));
    }

    @Test
    void getById_whenExists_shouldReturnEntity() {
        UUID id = UUID.randomUUID();
        ApiDataEntity entity = ApiDataEntity.builder()
                .id(id)
                .createdAt(Instant.now())
                .success(true)
                .payload("found")
                .build();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        Optional<ApiDataEntity> result = service.getById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getPayload()).isEqualTo("found");
    }

    @Test
    void getById_whenNotExists_shouldReturnEmpty() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        Optional<ApiDataEntity> result = service.getById(id);
        assertThat(result).isEmpty();
    }

    @Test
    void update_whenNotExists_shouldReturnEmptyAndNotSave() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<ApiDataEntity> result = service.update(id, new ApiDataRequestDTO(false, "x"));

        assertThat(result).isEmpty();
        verify(repository, never()).save(any());
    }

    @Test
    void delete_whenExists_shouldReturnTrue() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        boolean result = service.delete(id);

        assertThat(result).isTrue();
        verify(repository).deleteById(id);
    }

    @Test
    void delete_whenNotExists_shouldReturnFalseAndNotDelete() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        boolean result = service.delete(id);

        assertThat(result).isFalse();
        verify(repository, never()).deleteById(any());
    }
}

