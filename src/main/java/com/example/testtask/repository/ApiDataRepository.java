package com.example.testtask.repository;

import com.example.testtask.entity.ApiDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApiDataRepository extends JpaRepository<ApiDataEntity, UUID> {
}
