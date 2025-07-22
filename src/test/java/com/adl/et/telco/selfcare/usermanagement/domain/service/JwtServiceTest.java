/*
 * Copyrights 2025 Axiata Digital Labs Pvt Ltd.
 * All Rights Reserved.
 *
 * These material are unpublished, proprietary, confidential source
 * code of Axiata Digital Labs Pvt Ltd (ADL) and constitute a TRADE
 * SECRET of ADL.
 *
 *  ADL retains all title to and intellectual property rights in these
 *  materials.
 */

package com.adl.et.telco.selfcare.usermanagement.domain.service;

import com.adl.et.telco.selfcare.usermanagement.domain.entities.EnterpriseUsers;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the Unit test class for the JWT Service
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/03/04
 */
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private static final String TEST_SIGNING_KEY = "abcdefghijklmnopqrstuvwxyz123456";
    private static final int TOKEN_EXPIRY = 60000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "jwtSignKey", TEST_SIGNING_KEY);
        ReflectionTestUtils.setField(jwtService, "forgotPasswordTokenMillisecond", TOKEN_EXPIRY);
    }

    /**
     * This method test the create token for Forgot Password Success Scenario
     *
     * @throws DomainException
     */
    @Test
    void createTokenForForgotPassword_Success() throws DomainException {
        EnterpriseUsers mockUser = new EnterpriseUsers();
        mockUser.setUserId(1L);
        mockUser.setEmail("test@example.com");
        String uniqueId = UUID.randomUUID().toString();

        String token = jwtService.createTokenForForgotPassword(mockUser, uniqueId);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = Jwts.parser()
                .setSigningKey(TEST_SIGNING_KEY.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();

        assertEquals(uniqueId, claims.get("tokenId"));
        assertEquals("1", claims.get("userId"));
        assertEquals("test@example.com", claims.get("email"));
    }

    /**
     * This method test the create token for Forgot Password Failure when Invalid Key
     */
    @Test
    void createTokenForForgotPassword_Failure_InvalidKey() throws DomainException {
        // Given
        EnterpriseUsers mockUser = new EnterpriseUsers();
        mockUser.setUserId(1L);
        mockUser.setEmail("test@example.com");
        String uniqueId = UUID.randomUUID().toString();

        // Set an invalid signing key
        ReflectionTestUtils.setField(jwtService, "jwtSignKey", "shortkey");

        // Generate the token (this might still work)
        String token = jwtService.createTokenForForgotPassword(mockUser, uniqueId);
        assertNotNull(token);  // Ensure a token is generated

        // Now try to verify the token, expecting failure
        ReflectionTestUtils.setField(jwtService, "jwtSignKey", "correct-key"); // Reset to correct key

        DomainException thrown = assertThrows(DomainException.class, () ->
                jwtService.isAccessTokenExpired(token)  // Should fail during verification
        );

        assertEquals(INVALID_USER_TOKEN_ERROR.getDescription(), thrown.getMessage());
    }

    @Test
    void isAccessTokenExpired_ValidToken() throws DomainException {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(TOKEN_EXPIRY);

        String token = Jwts.builder()
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(SignatureAlgorithm.HS512, TEST_SIGNING_KEY.getBytes())
                .compact();

        Claims claims = jwtService.isAccessTokenExpired(token);

        assertNotNull(claims);
    }

    @Test
    void isAccessTokenExpired_ExpiredToken() {
        Instant now = Instant.now();
        Instant expiry = now.minusSeconds(10);

        String token = Jwts.builder()
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(SignatureAlgorithm.HS512, TEST_SIGNING_KEY.getBytes())
                .compact();

        DomainException thrown = assertThrows(DomainException.class, () ->
                jwtService.isAccessTokenExpired(token)
        );
        assertEquals(USER_TOKEN_EXPIRED_ERROR.getDescription(), thrown.getMessage());
    }

    @Test
    void generateSessionToken_Success() {
        String sessionToken = jwtService.generateSessionToken("12345");

        assertNotNull(sessionToken);
        assertFalse(sessionToken.isEmpty());
    }
}