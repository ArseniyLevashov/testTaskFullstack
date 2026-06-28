package com.example.testtask.controller;

import com.example.testtask.dto.LoginRequestDTO;
import com.example.testtask.dto.LoginResponseDTO;
import com.example.testtask.entity.AppUser;
import com.example.testtask.repository.AppUserRepository;
import com.example.testtask.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppUserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        // Достаём юзера из БД, чтобы вернуть его роль фронту
        AppUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.username()));

        String token = jwtService.generateToken(request.username());

        return ResponseEntity.ok(new LoginResponseDTO(
                token,
                user.getUsername(),
                user.getRole().name()
        ));
    }
}