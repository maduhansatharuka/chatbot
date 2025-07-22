/*
 *
 *  Copyrights 2024 Axiata Digital Labs Pvt Ltd.
 *  All Rights Reserved.
 *
 *  These material are unpublished, proprietary, confidential source
 *  code of Axiata Digital Labs Pvt Ltd (ADL) and constitute a TRADE
 *  SECRET of ADL.
 *
 *  ADL retains all title to and intellectual property rights in these
 *  materials.
 *
 */

package com.adl.et.telco.selfcare.usermanagement.domain.service;

import com.adl.et.telco.selfcare.usermanagement.domain.boundry.UserTokenRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.EnterpriseUsers;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

import static com.adl.et.telco.selfcare.usermanagement.application.util.Constant.*;
import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.*;
import static com.adl.et.telco.selfcare.usermanagement.application.util.HandleErrorLogs.logError;

/**
 * This service class is used to implement logic related to Jwt Token
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/02/26
 */
@Service
public class JwtService {

    private final UserTokenRepository userTokenRepository;

    @Value("${security.jwt.expiretime.forgot.password.token.millisecond}")
    private Integer forgotPasswordTokenMillisecond;

    @Value("${security.jwt.sign.key}")
    private String jwtSignKey;

    private static final String JWT_HEADER_TYPE_KEY = "typ";
    private static final String JWT_HEADER_TYPE_VALUE = "JWT";

    public JwtService(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }

    public String createTokenForForgotPassword(EnterpriseUsers enterpriseUsers, String uniqueId) throws DomainException {

        Instant currentDateTime = Instant.now();
        Instant expireDateTime = currentDateTime.plusMillis(forgotPasswordTokenMillisecond);

        try {
            // Create claims
            Map<String, Object> claims = new HashMap<>();
            claims.put(USER_ID, String.valueOf(enterpriseUsers.getUserId()));
            claims.put(EMAIL, enterpriseUsers.getEmail());
            claims.put(TOKEN_ID, uniqueId);

            // Build JWT token
            return Jwts.builder().setIssuedAt(Date.from(currentDateTime)).setExpiration(Date.from(expireDateTime)).addClaims(claims).setHeaderParam(JWT_HEADER_TYPE_KEY, JWT_HEADER_TYPE_VALUE).signWith(SignatureAlgorithm.HS512, jwtSignKey.getBytes()).compact();

        } catch (Exception e) {
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            throw new DomainException(TOKEN_CREATE_ERROR.getDescription(), TOKEN_CREATE_ERROR.getCode());
        }
    }

    /**
     * This method is to check if the access token is expired or not
     *
     * @param token
     */
    public Claims isAccessTokenExpired(String token) throws DomainException {
        try {
            return Jwts.parser().setSigningKey(jwtSignKey.getBytes()).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            throw new DomainException(USER_TOKEN_EXPIRED_ERROR.getDescription(), USER_TOKEN_EXPIRED_ERROR.getCode());
        } catch (Exception e) {
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            throw new DomainException(INVALID_USER_TOKEN_ERROR.getDescription(), INVALID_USER_TOKEN_ERROR.getCode());
        }
    }

    /**
     * This method is used to generate One Time Session Token
     *
     * @return
     */
    public String generateSessionToken(String userId) {
        String uuid = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();

        // Generate a secure random string
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[16]; // 128-bit randomness
        random.nextBytes(randomBytes);
        String randomString = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        String rawToken = userId + "-" + uuid + "-" + timestamp + "-" + randomString;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawToken.getBytes(StandardCharsets.UTF_8));
    }
}