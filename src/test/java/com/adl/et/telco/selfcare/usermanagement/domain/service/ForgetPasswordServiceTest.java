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

import com.adl.et.telco.selfcare.usermanagement.application.dto.RequestHeader;
import com.adl.et.telco.selfcare.usermanagement.application.dto.forgetPasswordGenerateEmail.ForgetPasswordGenerateEmailRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.forgetPasswordGenerateEmail.ForgetPasswordGenerateEmailRequestBody;
import com.adl.et.telco.selfcare.usermanagement.application.dto.forgetPasswordValidateUser.ForgetPasswordValidateUserRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.forgetPasswordValidateUser.ForgetPasswordValidateUserRequestBody;
import com.adl.et.telco.selfcare.usermanagement.application.dto.resetPassword.ResetPasswordRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.resetPassword.ResetPasswordRequestBody;
import com.adl.et.telco.selfcare.usermanagement.application.util.PasswordPolicyValidator;
import com.adl.et.telco.selfcare.usermanagement.application.util.PasswordUtil;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.EnterpriseUsersRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.PasswordHistoryRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.SessionTokenRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.UserTokenRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.forgetPasswordGenerateEmail.ForgetPasswordGenerateEmailResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.forgetPasswordValidateUser.ForgetPasswordValidateUserResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.resetPassword.ResetPasswordResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.*;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.external.dto.sendemail.SendEmailRequest;
import com.adl.et.telco.selfcare.usermanagement.external.service.SendEmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * This is the Unit test class for the Forget Password Service
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/03/04
 */
@ExtendWith(MockitoExtension.class)
class ForgetPasswordServiceTest {

    @Mock
    private EnterpriseUsersRepository repository;

    @Mock
    private SendEmailService sendEmailService;

    @Mock
    private UserTokenRepository userTokenRepository;

    @Mock
    private SessionTokenRepository sessionTokenRepository;

    @Mock
    private PasswordHistoryRepository passwordHistoryRepository;

    @Mock
    private PasswordUtil passwordUtil;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordPolicyValidator passwordPolicyValidator;

    @InjectMocks
    private ForgetPasswordService forgetPasswordService;

    private static final String VALID_EMAIL = "hasini.hatharasinghe@axiatadigitallabs.com";
    private static final String INVALID_EMAIL = "hasini.hatharasinghe@invalid";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(forgetPasswordService, "forgotPasswordTokenMillisecond", 600000);
        ReflectionTestUtils.setField(forgetPasswordService, "forgotPasswordResetLinkUrl", "https://resetlink.com/");
        ReflectionTestUtils.setField(forgetPasswordService, "validationRegexEmail", "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        ReflectionTestUtils.setField(forgetPasswordService, "forgotPasswordSessionTokenExpiryTimeInMinutes", 10);
        ReflectionTestUtils.setField(forgetPasswordService, "passwordExpirationPeriod", "P3M");
        ReflectionTestUtils.setField(forgetPasswordService, "maximumNumberOfPasswordHistoryRecords", 3);
        ReflectionTestUtils.setField(forgetPasswordService, "forgotPasswordEmailSubject", "Reset Password");
        ReflectionTestUtils.setField(forgetPasswordService, "forgotPasswordSuccessEmailSubject", "Reset Password Success");
        ReflectionTestUtils.setField(forgetPasswordService, "forgotPasswordEmailFromName", "Test");
    }

    /**
     * This method is used to test Forget Password Generate Email Success Scenario
     *
     * @throws DomainException
     * @throws JsonProcessingException
     */
    @Test
    void testForgetPasswordGenerateEmail_Success() throws DomainException {
        ForgetPasswordGenerateEmailRequest request = createValidRequest(VALID_EMAIL);
        EnterpriseUsers mockUser = createMockUser(VALID_EMAIL);

        when(repository.findUserByTheEmail(anyString(), any(LocalDateTime.class))).thenReturn(mockUser);
        when(jwtService.createTokenForForgotPassword(any(EnterpriseUsers.class), anyString())).thenReturn("mockToken");

        // mock async method
        when(sendEmailService.sendEmail(any(SendEmailRequest.class))).thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok("Email sent successfully")));

        when(userTokenRepository.saveAndFlush(any(UserTokenEntity.class))).thenReturn(new UserTokenEntity());

        ForgetPasswordGenerateEmailResponse response = forgetPasswordService.forgetPasswordGenerateEmail(request);

        assertNotNull(response);
        assertEquals("UMS1000", response.getResponseHeader().getCode());

        // Verify interactions
        verify(sendEmailService, times(1)).sendEmail(any(SendEmailRequest.class));
        verify(userTokenRepository, times(1)).saveAndFlush(any(UserTokenEntity.class));
    }

    /**
     * This method is used to test Forget Password Generate Email - Invalid Email
     */
    @Test
    void testForgetPasswordGenerateEmail_InvalidEmail() {
        ForgetPasswordGenerateEmailRequest request = createValidRequest(INVALID_EMAIL);
        DomainException exception = assertThrows(DomainException.class, () -> forgetPasswordService.forgetPasswordGenerateEmail(request));

        assertEquals("Invalid Email Format", exception.getMessage());
        verifyNoInteractions(repository, jwtService, sendEmailService);
    }

    /**
     * This method is used to test Forget Password Generate Email - User Not Found Scenario
     *
     * @throws DomainException
     * @throws JsonProcessingException
     */
    @Test
    void testForgetPasswordGenerateEmail_UserNotFound() throws DomainException {
        ForgetPasswordGenerateEmailRequest request = createValidRequest(VALID_EMAIL);
        when(repository.findUserByTheEmail(anyString(), any(LocalDateTime.class))).thenReturn(null);

        ForgetPasswordGenerateEmailResponse response = forgetPasswordService.forgetPasswordGenerateEmail(request);
        assertNotNull(response);
        assertEquals("UMS1000", response.getResponseHeader().getCode());
        verifyNoInteractions(jwtService, sendEmailService, userTokenRepository);
    }

    /**
     * This method is used to test Forget Password Generate Email - When Exception Occurred
     */
    @Test
    void testForgetPasswordGenerateEmail_ExceptionHandling() {
        ForgetPasswordGenerateEmailRequest request = createValidRequest(VALID_EMAIL);
        when(repository.findUserByTheEmail(anyString(), any(LocalDateTime.class))).thenThrow(new RuntimeException("Database error"));

        DomainException exception = assertThrows(DomainException.class, () -> forgetPasswordService.forgetPasswordGenerateEmail(request));
        assertEquals("Error Occurred While Generating Forget Password Email", exception.getMessage());
    }

    /**
     * This method is used to test forget password user validation - success scenario
     *
     * @throws DomainException
     */
    @Test
    void forgetPasswordUserValidation_Success() throws DomainException {
        // Mock JWT validation
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.get("tokenId", String.class)).thenReturn("token123");
        when(mockClaims.get("userId", String.class)).thenReturn("user1");
        when(mockClaims.get("email", String.class)).thenReturn("test@example.com");
        when(jwtService.isAccessTokenExpired("validToken")).thenReturn(mockClaims);
        when(jwtService.generateSessionToken("user1")).thenReturn("newSessionToken");

        // Mock user token validation
        UserTokenEntity userToken = new UserTokenEntity();
        userToken.setStatus(TokenStatus.ACTIVE);
        when(userTokenRepository.validateTheUserToken("user1", "test@example.com", "token123", TokenStatus.ACTIVE))
                .thenReturn(userToken);

        // Mock session token repository save
        when(sessionTokenRepository.saveAndFlush(any(SessionTokenEntity.class))).thenAnswer(invocation -> null);

        ForgetPasswordValidateUserRequestBody requestBody = new ForgetPasswordValidateUserRequestBody();
        RequestHeader requestHeader = new RequestHeader("Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test");
        requestBody.setToken("validToken");
        ForgetPasswordValidateUserRequest request = new ForgetPasswordValidateUserRequest(requestBody, requestHeader);
        ForgetPasswordValidateUserResponse response = forgetPasswordService.forgetPasswordUserValidation(request);

        assertNotNull(response);
        assertEquals("user1", response.getResponseBody().getUserId());
        assertEquals("newSessionToken", response.getResponseBody().getSessionToken());
    }

    /**
     * This method is used to test forget password user validation - With Expired Token
     *
     * @throws DomainException
     */
    @Test
    void forgetPasswordUserValidation_ExpiredToken_ThrowsException() throws DomainException {
        when(jwtService.isAccessTokenExpired("validToken")).thenThrow(new DomainException("Token expired", "ERR_TOKEN_EXPIRED"));

        ForgetPasswordValidateUserRequest request = new ForgetPasswordValidateUserRequest();
        ForgetPasswordValidateUserRequestBody requestBody = new ForgetPasswordValidateUserRequestBody();
        requestBody.setToken("validToken");
        request.setRequestBody(requestBody);

        DomainException exception = assertThrows(DomainException.class, () -> forgetPasswordService.forgetPasswordUserValidation(request));
        assertEquals("Token expired", exception.getMessage());
    }

    /**
     * This method is used to test forget password user validation - With Invalid User Token
     *
     * @throws DomainException
     */
    @Test
    void forgetPasswordUserValidation_InvalidUserToken_ThrowsException() throws DomainException {
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.get("tokenId", String.class)).thenReturn("token123");
        when(mockClaims.get("userId", String.class)).thenReturn("user1");
        when(mockClaims.get("email", String.class)).thenReturn("test@example.com");
        lenient().when(jwtService.isAccessTokenExpired("validToken")).thenReturn(mockClaims);
        lenient().when(jwtService.generateSessionToken("user1")).thenReturn("newSessionToken");

        lenient().when(userTokenRepository.validateTheUserToken("user1", "test@example.com", "token123", TokenStatus.ACTIVE))
                .thenReturn(null);

        ForgetPasswordValidateUserRequest request = new ForgetPasswordValidateUserRequest();
        ForgetPasswordValidateUserRequestBody requestBody = new ForgetPasswordValidateUserRequestBody();
        requestBody.setToken("validToken");
        request.setRequestBody(requestBody);
        DomainException exception = assertThrows(DomainException.class, () -> forgetPasswordService.forgetPasswordUserValidation(request));
        assertEquals("Invalid User Token", exception.getMessage());
    }

    /**
     * This method is used to test forget password user validation - Failure to save token
     *
     * @throws DomainException
     */
    @Test
    void forgetPasswordUserValidation_FailureToSaveSessionToken_ThrowsException() throws DomainException {
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.get("tokenId", String.class)).thenReturn("token123");
        when(mockClaims.get("userId", String.class)).thenReturn("user1");
        when(mockClaims.get("email", String.class)).thenReturn("test@example.com");
        when(jwtService.isAccessTokenExpired("validToken")).thenReturn(mockClaims);
        when(jwtService.generateSessionToken("user1")).thenReturn("newSessionToken");

        UserTokenEntity userToken = new UserTokenEntity();
        userToken.setStatus(TokenStatus.ACTIVE);
        when(userTokenRepository.validateTheUserToken("user1", "test@example.com", "token123", TokenStatus.ACTIVE))
                .thenReturn(userToken);

        doThrow(new RuntimeException("DB error")).when(sessionTokenRepository).saveAndFlush(any(SessionTokenEntity.class));

        ForgetPasswordValidateUserRequest request = new ForgetPasswordValidateUserRequest();
        ForgetPasswordValidateUserRequestBody requestBody = new ForgetPasswordValidateUserRequestBody();
        requestBody.setToken("validToken");
        request.setRequestBody(requestBody);
        DomainException exception = assertThrows(DomainException.class, () -> forgetPasswordService.forgetPasswordUserValidation(request));
        assertEquals("Error Occurred While Validating the User in Forget Password", exception.getMessage());
    }

    /**
     * This method is used to test reset password - success scenario
     *
     * @throws DomainException
     */
    @Test
    void testResetPassword_Success() throws DomainException {
        // Arrange
        ResetPasswordRequest request = createResetPasswordRequest("VALID_USER_ID", "VALID_PASSWORD");

        SessionTokenEntity sessionTokenEntity = new SessionTokenEntity(1L, "Test", "Test", "Test",
                LocalDateTime.now().plusMinutes(15), TokenStatus.ACTIVE);

        lenient().when(sessionTokenRepository.validateSessionToken(eq("VALID_USER_ID"), eq("validToken"), eq(TokenStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(sessionTokenEntity);

        lenient().when(passwordUtil.hashPassword("VALID_PASSWORD")).thenReturn("hashedPassword");
        lenient().when(passwordUtil.verifyPassword("hashedPassword", "VALID_PASSWORD")).thenReturn(true);

        lenient().when(passwordHistoryRepository.findByUserIdOrderByCreatedDateDesc("VALID_USER_ID")).thenReturn(new ArrayList<>());
        lenient().when(repository.findUserByUserId("VALID_USER_ID")).thenReturn(new EnterpriseUsers());
        lenient().when(passwordHistoryRepository.save(any(PasswordHistoryEntity.class))).thenReturn(new PasswordHistoryEntity());

        // Act
        ResetPasswordResponse response = forgetPasswordService.resetPassword(request);

        // Assert
        assertNotNull(response);
    }

    /**
     * This method is used to test reset password - invalid session token
     */
    @Test
    void testResetPassword_InvalidSessionToken() {
        // Mock the inputs
        ResetPasswordRequest request = createResetPasswordRequest("VALID_USER_ID", "VALID_PASSWORD");

        // Mock invalid session token
        lenient().when(sessionTokenRepository.validateSessionToken("VALID_USER_ID", "invalidToken", TokenStatus.ACTIVE, LocalDateTime.now()))
                .thenReturn(null);

        // Call the method and expect an exception
        DomainException exception = assertThrows(DomainException.class, () -> forgetPasswordService.resetPassword(request));

        // Assertions
        assertEquals("Invalid session token", exception.getMessage());
        verifyNoInteractions(repository, passwordHistoryRepository);
    }

    /**
     * This method is used to test reset password - password history check fail
     */
    @Test
    void testResetPassword_PasswordHistoryCheck_Fail() {
        // Mock the inputs
        ResetPasswordRequest request = createResetPasswordRequest("VALID_USER_ID", "VALID_PASSWORD");

        // Mock session token validation
        SessionTokenEntity sessionTokenEntity = new SessionTokenEntity(1L, "Test", "Test", "Test",
                LocalDateTime.now().plusMinutes(15), TokenStatus.ACTIVE);

        lenient().when(sessionTokenRepository.validateSessionToken(eq("VALID_USER_ID"), eq("validToken"), eq(TokenStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(sessionTokenEntity);

        // Mock password hashing and encoding
        lenient().when(passwordUtil.hashPassword("VALID_PASSWORD")).thenReturn("hashedPassword");
        lenient().when(passwordUtil.verifyPassword("hashedPassword", "VALID_PASSWORD")).thenReturn(true);

        // Mock password history check where the new password matches a previous password
        PasswordHistoryEntity passwordHistoryEntity = new PasswordHistoryEntity();
        passwordHistoryEntity.setPassword(Base64.getEncoder().encodeToString("hashedPassword".getBytes()));
        lenient().when(passwordHistoryRepository.findByUserIdOrderByCreatedDateDesc("VALID_USER_ID")).thenReturn(Collections.singletonList(passwordHistoryEntity));

        // Call the method and expect an exception
        DomainException exception = assertThrows(DomainException.class, () -> forgetPasswordService.resetPassword(request));

        // Assertions
        assertEquals("New password cannot match last 3 used passwords", exception.getMessage());
        verifyNoInteractions(repository);
    }

    /**
     * Helper method to create a reset password request
     *
     * @param userId
     * @param password
     * @return
     */
    private ResetPasswordRequest createResetPasswordRequest(String userId, String password) {
        ResetPasswordRequestBody requestBody = new ResetPasswordRequestBody("validToken", userId, password);
        RequestHeader requestHeader = new RequestHeader("Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test");
        return new ResetPasswordRequest(requestBody, requestHeader);
    }

    /**
     * Helper method to create a valid request
     *
     * @param email
     * @return
     */
    private ForgetPasswordGenerateEmailRequest createValidRequest(String email) {
        ForgetPasswordGenerateEmailRequestBody requestBody = new ForgetPasswordGenerateEmailRequestBody();
        requestBody.setEmail(email);
        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setRequestId("12345");
        return new ForgetPasswordGenerateEmailRequest(requestBody, requestHeader);
    }

    /**
     * Helper method to create a mock user
     *
     * @param email
     * @return
     */
    private EnterpriseUsers createMockUser(String email) {
        EnterpriseUsers user = new EnterpriseUsers();
        user.setUserId(1L);
        user.setEmail(email);
        user.setFirstName("Hasini");
        user.setLastName("Hatharasinghe");
        user.setStatus(UserStatus.APPROVED);
        return user;
    }
}