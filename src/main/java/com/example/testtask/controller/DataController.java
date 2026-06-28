package com.example.testtask.controller;

import com.example.testtask.dto.ApiDataRequestDTO;
import com.example.testtask.entity.ApiDataEntity;
import com.example.testtask.service.ApiDataService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class DataController {

    private final ApiDataService apiDataService;

    @Operation(summary = "Получить последние 10 записей")
    @GetMapping
    public List<ApiDataEntity> getLast10() {
        return apiDataService.getLast10();
    }

    @Operation(summary = "Получить запись по id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiDataEntity> getById(@PathVariable UUID id) {
        return apiDataService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать новую запись")
    @PostMapping
    public ResponseEntity<ApiDataEntity> create(@RequestBody ApiDataRequestDTO request) {
        ApiDataEntity created = apiDataService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Изменить запись")
    @PutMapping("/{id}")
    public ResponseEntity<ApiDataEntity> update(@PathVariable UUID id,
                                                @RequestBody ApiDataRequestDTO request) {
        return apiDataService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Ну тут все понятно")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return apiDataService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
