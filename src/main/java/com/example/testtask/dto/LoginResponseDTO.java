package com.example.testtask.dto;

public record LoginResponseDTO(
        String token,
        String username,
        String role) {}

