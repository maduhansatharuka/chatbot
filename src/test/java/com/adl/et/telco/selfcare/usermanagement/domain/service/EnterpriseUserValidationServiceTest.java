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

import com.adl.et.telco.selfcare.usermanagement.application.dto.RequestHeader;
import com.adl.et.telco.selfcare.usermanagement.application.dto.userValidation.EnterpriseUserValidationRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.userValidation.EnterpriseUserValidationRequestBody;
import com.adl.et.telco.selfcare.usermanagement.application.util.PasswordUtil;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.EnterpriseUsersRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.userValidation.EnterpriseUserValidationResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.EnterpriseUsers;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.UserStatus;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Base64;

import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.ACCOUNT_LOCKED_ERROR;
import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.ENTERPRISE_USER_VALIDATION_ERROR;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * This is the Unit test class for the Enterprise User Validation Service
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/07
 */
@ExtendWith(MockitoExtension.class)
class EnterpriseUserValidationServiceTest {

    @Mock
    private EnterpriseUsersRepository mockRepository;

    @Mock
    private PasswordUtil mockPasswordUtil;

    private EnterpriseUserValidationService enterpriseUserValidationServiceUnderTest;

    @BeforeEach
    void setUp() {
        enterpriseUserValidationServiceUnderTest = new EnterpriseUserValidationService(mockRepository, mockPasswordUtil);
    }

    /**
     * This method is used to test Validate User By UserName And Password Enterprise Users Repository Returns Null
     */
    @Test
    void testValidateUserByUserNameAndPassword_EnterpriseUsersRepositoryReturnsNull() {
        final EnterpriseUserValidationRequest request = new EnterpriseUserValidationRequest();
        final EnterpriseUserValidationRequestBody requestBody = new EnterpriseUserValidationRequestBody();
        requestBody.setUserName("userName");
        requestBody.setPassword("password");
        request.setRequestBody(requestBody);
        final RequestHeader requestHeader = new RequestHeader();
        requestHeader.setRequestId("requestId");
        request.setRequestHeader(requestHeader);

        when(mockRepository.validateEnterpriseUserByGivenUserName(eq("userName"), eq(UserStatus.APPROVED), any(LocalDateTime.class))).thenReturn(null);

        assertThatThrownBy(() -> enterpriseUserValidationServiceUnderTest.validateUserByUserNameAndPassword(request)).isInstanceOf(DomainException.class).hasMessageContaining(ENTERPRISE_USER_VALIDATION_ERROR.getDescription());
    }

    /**
     * This method is used to test Validate User By UserName And Password Exception Thrown
     */
    @Test
    void testValidateUserByUserNameAndPassword_ExceptionThrown() {
        // Setup
        final EnterpriseUserValidationRequest request = new EnterpriseUserValidationRequest();
        final EnterpriseUserValidationRequestBody requestBody = new EnterpriseUserValidationRequestBody();
        requestBody.setUserName("userName");
        requestBody.setPassword("password");
        request.setRequestBody(requestBody);
        final RequestHeader requestHeader = new RequestHeader();
        requestHeader.setRequestId("requestId");
        request.setRequestHeader(requestHeader);

        // Simulate an unexpected exception (non-DomainException) thrown in the method
        when(mockRepository.validateEnterpriseUserByGivenUserName(eq("userName"), eq(UserStatus.APPROVED), any(LocalDateTime.class))).thenThrow(new RuntimeException("Unexpected exception"));

        // Run the test and assert that a DomainException is thrown
        assertThatThrownBy(() -> enterpriseUserValidationServiceUnderTest.validateUserByUserNameAndPassword(request)).isInstanceOf(DomainException.class).hasMessageContaining(ENTERPRISE_USER_VALIDATION_ERROR.getDescription());
    }

    /**
     * This method is used to test the success scenario of Validate User By UserName And Password
     *
     * @throws DomainException
     */
    @Test
    void testValidateUserByUserNameAndPassword_Success() throws DomainException {
        // Setup the request
        final EnterpriseUserValidationRequest request = new EnterpriseUserValidationRequest();
        final EnterpriseUserValidationRequestBody requestBody = new EnterpriseUserValidationRequestBody();
        requestBody.setUserName("userName");
        requestBody.setPassword("password");
        request.setRequestBody(requestBody);
        final RequestHeader requestHeader = new RequestHeader();
        requestHeader.setRequestId("requestId");
        request.setRequestHeader(requestHeader);

        // Mock the repository to return a valid user with a hashed password
        final EnterpriseUsers validatedUserDetails = new EnterpriseUsers();
        String hashedPassword = Base64.getEncoder().encodeToString("hashedPassword".getBytes());
        validatedUserDetails.setPassword(hashedPassword);

        when(mockRepository.validateEnterpriseUserByGivenUserName(eq("userName"), eq(UserStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(validatedUserDetails);

        // Mock password verification
        when(mockPasswordUtil.verifyPassword(eq("hashedPassword"), eq("password"))).thenReturn(true);

        // Run the method
        final EnterpriseUserValidationResponse response = enterpriseUserValidationServiceUnderTest.validateUserByUserNameAndPassword(request);

        // Verify the response
        assertNotNull(response);
        assertNotNull(response.getResponseHeader());
        assertEquals("requestId", response.getResponseHeader().getRequestId());
    }

    /**
     * Test when the password verification fails, the user’s attempt count increases,
     * and after 5 failed attempts, the account is locked for 1 hour.
     */
    @Test
    void testValidateUserByUserNameAndPassword_PasswordIncorrect_AttemptsIncrease() {
        // Setup the request
        final EnterpriseUserValidationRequest request = new EnterpriseUserValidationRequest();
        final EnterpriseUserValidationRequestBody requestBody = new EnterpriseUserValidationRequestBody();
        requestBody.setUserName("userName");
        requestBody.setPassword("wrongPassword"); // Incorrect password
        request.setRequestBody(requestBody);
        final RequestHeader requestHeader = new RequestHeader();
        requestHeader.setRequestId("requestId");
        request.setRequestHeader(requestHeader);

        // Mock user details with a stored password hash
        final EnterpriseUsers validatedUserDetails = new EnterpriseUsers();
        validatedUserDetails.setUserName("userName");
        validatedUserDetails.setAttempts(4); // Already failed 4 times
        validatedUserDetails.setPassword(Base64.getEncoder().encodeToString("hashedPassword".getBytes()));

        when(mockRepository.validateEnterpriseUserByGivenUserName(eq("userName"), eq(UserStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(validatedUserDetails);

        // Mock password verification to return false
        when(mockPasswordUtil.verifyPassword(eq("hashedPassword"), eq("wrongPassword"))).thenReturn(false);

        // Run the method and assert DomainException is thrown
        assertThatThrownBy(() -> enterpriseUserValidationServiceUnderTest.validateUserByUserNameAndPassword(request))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(ENTERPRISE_USER_VALIDATION_ERROR.getDescription());

        // Verify that the attempt count increased to 5 and the account was locked
        assertEquals(5, validatedUserDetails.getAttempts());
        assertNotNull(validatedUserDetails.getLoginBlockedUntil()); // Locked for 1 hour

        // Ensure repository.save() was called to persist the updated attempts and lock status
        verify(mockRepository).save(validatedUserDetails);
    }

    /**
     * Test when the account is already locked (loginBlockedAt is in the future),
     * a DomainException should be thrown immediately.
     */
    @Test
    void testValidateUserByUserNameAndPassword_AccountLocked() {
        // Setup the request
        final EnterpriseUserValidationRequest request = new EnterpriseUserValidationRequest();
        final EnterpriseUserValidationRequestBody requestBody = new EnterpriseUserValidationRequestBody();
        requestBody.setUserName("userName");
        requestBody.setPassword("password");
        request.setRequestBody(requestBody);
        final RequestHeader requestHeader = new RequestHeader();
        requestHeader.setRequestId("requestId");
        request.setRequestHeader(requestHeader);

        // Mock user details with an account locked for 30 more minutes
        final EnterpriseUsers validatedUserDetails = new EnterpriseUsers();
        validatedUserDetails.setUserName("userName");
        validatedUserDetails.setAttempts(5);
        validatedUserDetails.setLoginBlockedUntil(LocalDateTime.now().plusMinutes(30)); // Locked

        when(mockRepository.validateEnterpriseUserByGivenUserName(eq("userName"), eq(UserStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(validatedUserDetails);

        // Run the method and assert DomainException is thrown
        assertThatThrownBy(() -> enterpriseUserValidationServiceUnderTest.validateUserByUserNameAndPassword(request))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(ACCOUNT_LOCKED_ERROR.getDescription());

        // Ensure repository.save() was NOT called, as account is already locked
        verify(mockRepository, never()).save(any(EnterpriseUsers.class));
    }
}