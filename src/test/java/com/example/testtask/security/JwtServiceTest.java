package com.example.testtask.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // секрет минимум 32 символа (256 бит) для HS256
        ReflectionTestUtils.setField(jwtService, "secret",
                "TestSecretKeyForJwtUnitTests1234567890ABC");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L); // 1 час
    }

    @Test
    void generateToken_thenExtractUsername_shouldRoundtrip() {
        String token = jwtService.generateToken("admin");

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("admin");
    }

    @Test
    void isTokenValid_freshToken_shouldBeTrue() {
        String token = jwtService.generateToken("user");
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_garbageToken_shouldBeFalse() {
        assertThat(jwtService.isTokenValid("not.a.valid.token")).isFalse();
    }

    @Test
    void isTokenValid_expiredToken_shouldBeFalse() {
        // выставляем отрицательное время жизни - токен "протух" сразу при создании
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        String expiredToken = jwtService.generateToken("admin");

        assertThat(jwtService.isTokenValid(expiredToken)).isFalse();
    }

    @Test
    void tokenSignedWithDifferentSecret_shouldBeInvalid() {
        String token = jwtService.generateToken("admin");

        // меняем секрет - подпись старого токена больше не проходит проверку
        ReflectionTestUtils.setField(jwtService, "secret",
                "CompletelyDifferentSecretKey0987654321XYZ");

        assertThat(jwtService.isTokenValid(token)).isFalse();
    }
}

