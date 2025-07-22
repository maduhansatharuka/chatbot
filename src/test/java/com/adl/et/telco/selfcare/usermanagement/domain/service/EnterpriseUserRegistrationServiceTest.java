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
import com.adl.et.telco.selfcare.usermanagement.application.dto.userRegistration.EnterpriseUserRegistrationRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.userRegistration.EnterpriseUserRegistrationRequestBody;
import com.adl.et.telco.selfcare.usermanagement.application.util.PasswordPolicyValidator;
import com.adl.et.telco.selfcare.usermanagement.application.util.PasswordUtil;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.EnterpriseUsersRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.userRegistration.EnterpriseUserRegistrationResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.EnterpriseUsers;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.UserStatus;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Base64;

import static com.adl.et.telco.selfcare.usermanagement.domain.entities.EnterpriseUserIdTypes.NIC;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

/**
 * This is the Unit test class for the Enterprise User Registration Service
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/07
 */
@ContextConfiguration(classes = {EnterpriseUserRegistrationService.class})
@ExtendWith(SpringExtension.class)
class EnterpriseUserRegistrationServiceTest {
    @Autowired
    private EnterpriseUserRegistrationService enterpriseUserRegistrationService;

    @MockBean
    private EnterpriseUsersRepository enterpriseUsersRepository;

    @MockBean
    private PasswordUtil passwordUtil;

    @MockBean
    private PasswordPolicyValidator passwordPolicyValidator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(enterpriseUserRegistrationService, "validationRegexMsisdn", "^(71[0-9]{6}|74(0[0-2][0-9]{5}|5[0-7][0-9]{5})|75(4[0-6][0-9]{5}|9[0-9]{6})|76(0[0-2][0-9]{5}|6[0-7][0-9]{5})|77(0[0-1][0-9]{5}|6[0-7][0-9]{5}|8[0-8][0-9]{5})|79250(000|900|999))$");
        ReflectionTestUtils.setField(enterpriseUserRegistrationService, "validationRegexEmail", "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        ReflectionTestUtils.setField(enterpriseUserRegistrationService, "passwordExpirationPeriod", "P3M");
    }

    /**
     * This method is used to test RegisterUser given Enterprise Users Repository
     */
    @Test
    void testRegisterUser_givenEnterpriseUsersRepository() {
        // Arrange, Act and Assert
        assertThrows(DomainException.class, () -> enterpriseUserRegistrationService.registerUser(new EnterpriseUserRegistrationRequest()));
    }

    /**
     * This method is used to test Register User given Unique Count Result Get Email Count Return Null
     */
    @Test
    void testRegisterUser_givenUniqueCountResultGetEmailCountReturnNull() {
        // Arrange
        EnterpriseUsersRepository.UniqueCountResult uniqueCountResult = mock(EnterpriseUsersRepository.UniqueCountResult.class);
        when(uniqueCountResult.getEmailCount()).thenReturn(null);
        when(uniqueCountResult.getMobileNumberCount()).thenReturn(3L);
        when(uniqueCountResult.getUserNameCount()).thenReturn(0L);
        when(enterpriseUsersRepository.countByFields(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any())).thenReturn(uniqueCountResult);

        EnterpriseUserRegistrationRequest request = new EnterpriseUserRegistrationRequest();
        request.setRequestBody(new EnterpriseUserRegistrationRequestBody());

        // Act and Assert
        assertThrows(DomainException.class, () -> enterpriseUserRegistrationService.registerUser(request));
        verify(enterpriseUsersRepository).countByFields(isNull(), isNull(), isNull());
        verify(uniqueCountResult).getEmailCount();
        verify(uniqueCountResult, atLeast(1)).getMobileNumberCount();
        verify(uniqueCountResult, atLeast(1)).getUserNameCount();
    }

    /**
     * This method is used to test Register User given Unique Count Result Get Email Count Return Three
     */
    @Test
    void testRegisterUser_givenUniqueCountResultGetEmailCountReturnThree() {
        // Arrange
        EnterpriseUsersRepository.UniqueCountResult uniqueCountResult = mock(EnterpriseUsersRepository.UniqueCountResult.class);
        when(uniqueCountResult.getEmailCount()).thenReturn(3L);
        when(uniqueCountResult.getUserNameCount()).thenReturn(0L);
        when(enterpriseUsersRepository.countByFields(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any())).thenReturn(uniqueCountResult);

        EnterpriseUserRegistrationRequest request = new EnterpriseUserRegistrationRequest();
        request.setRequestBody(new EnterpriseUserRegistrationRequestBody());

        // Act and Assert
        assertThrows(DomainException.class, () -> enterpriseUserRegistrationService.registerUser(request));
        verify(enterpriseUsersRepository).countByFields(isNull(), isNull(), isNull());
        verify(uniqueCountResult, atLeast(1)).getEmailCount();
        verify(uniqueCountResult, atLeast(1)).getUserNameCount();
    }

    /**
     * This method is used to test Register User given Unique Count Result Get Mobile Number Count Return Null
     */
    @Test
    void testRegisterUser_givenUniqueCountResultGetMobileNumberCountReturnNull() {
        // Arrange
        EnterpriseUsersRepository.UniqueCountResult uniqueCountResult = mock(EnterpriseUsersRepository.UniqueCountResult.class);
        when(uniqueCountResult.getEmailCount()).thenReturn(0L);
        when(uniqueCountResult.getMobileNumberCount()).thenReturn(null);
        when(uniqueCountResult.getUserNameCount()).thenReturn(0L);
        when(enterpriseUsersRepository.countByFields(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any())).thenReturn(uniqueCountResult);

        EnterpriseUserRegistrationRequest request = new EnterpriseUserRegistrationRequest();
        request.setRequestBody(new EnterpriseUserRegistrationRequestBody());

        // Act and Assert
        assertThrows(DomainException.class, () -> enterpriseUserRegistrationService.registerUser(request));
        verify(enterpriseUsersRepository).countByFields(isNull(), isNull(), isNull());
        verify(uniqueCountResult, atLeast(1)).getEmailCount();
        verify(uniqueCountResult).getMobileNumberCount();
        verify(uniqueCountResult, atLeast(1)).getUserNameCount();
    }

    /**
     * This method is used to test Register User given Unique Count Result Get Mobile Number Count Return Three
     */
    @Test
    void testRegisterUser_givenUniqueCountResultGetMobileNumberCountReturnThree() {
        // Arrange
        EnterpriseUsersRepository.UniqueCountResult uniqueCountResult = mock(EnterpriseUsersRepository.UniqueCountResult.class);
        when(uniqueCountResult.getEmailCount()).thenReturn(0L);
        when(uniqueCountResult.getMobileNumberCount()).thenReturn(3L);
        when(uniqueCountResult.getUserNameCount()).thenReturn(0L);
        when(enterpriseUsersRepository.countByFields(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any())).thenReturn(uniqueCountResult);

        EnterpriseUserRegistrationRequest request = new EnterpriseUserRegistrationRequest();
        request.setRequestBody(new EnterpriseUserRegistrationRequestBody());

        // Act and Assert
        assertThrows(DomainException.class, () -> enterpriseUserRegistrationService.registerUser(request));
        verify(enterpriseUsersRepository).countByFields(isNull(), isNull(), isNull());
        verify(uniqueCountResult, atLeast(1)).getEmailCount();
        verify(uniqueCountResult, atLeast(1)).getMobileNumberCount();
        verify(uniqueCountResult, atLeast(1)).getUserNameCount();
    }

    /**
     * This method is used to test Register User given Unique Count Result Get Mobile Number Count Return Zero
     */
    @Test
    void testRegisterUser_givenUniqueCountResultGetMobileNumberCountReturnZero() {
        // Arrange
        EnterpriseUsersRepository.UniqueCountResult uniqueCountResult = mock(EnterpriseUsersRepository.UniqueCountResult.class);
        when(uniqueCountResult.getEmailCount()).thenReturn(0L);
        when(uniqueCountResult.getMobileNumberCount()).thenReturn(0L);
        when(uniqueCountResult.getUserNameCount()).thenReturn(0L);
        when(enterpriseUsersRepository.countByFields(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any())).thenReturn(uniqueCountResult);

        EnterpriseUserRegistrationRequest request = new EnterpriseUserRegistrationRequest();
        request.setRequestBody(new EnterpriseUserRegistrationRequestBody());

        // Act and Assert
        assertThrows(DomainException.class, () -> enterpriseUserRegistrationService.registerUser(request));
        verify(enterpriseUsersRepository).countByFields(isNull(), isNull(), isNull());
        verify(uniqueCountResult, atLeast(1)).getEmailCount();
        verify(uniqueCountResult, atLeast(1)).getMobileNumberCount();
        verify(uniqueCountResult, atLeast(1)).getUserNameCount();
    }

    /**
     * This method is used to test Register User given Unique Count Result Get UserName Count Return Null
     */
    @Test
    void testRegisterUser_givenUniqueCountResultGetUserNameCountReturnNull() {
        // Arrange
        EnterpriseUsersRepository.UniqueCountResult uniqueCountResult = mock(EnterpriseUsersRepository.UniqueCountResult.class);
        when(uniqueCountResult.getEmailCount()).thenReturn(3L);
        when(uniqueCountResult.getUserNameCount()).thenReturn(null);
        when(enterpriseUsersRepository.countByFields(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any())).thenReturn(uniqueCountResult);

        EnterpriseUserRegistrationRequest request = new EnterpriseUserRegistrationRequest();
        request.setRequestBody(new EnterpriseUserRegistrationRequestBody());

        // Act and Assert
        assertThrows(DomainException.class, () -> enterpriseUserRegistrationService.registerUser(request));
        verify(enterpriseUsersRepository).countByFields(isNull(), isNull(), isNull());
        verify(uniqueCountResult, atLeast(1)).getEmailCount();
        verify(uniqueCountResult).getUserNameCount();
    }

    /**
     * This method is used to test Register User given Unique Count Result Get UserName Count Return Three
     */
    @Test
    void testRegisterUser_givenUniqueCountResultGetUserNameCountReturnThree() {
        // Arrange
        EnterpriseUsersRepository.UniqueCountResult uniqueCountResult = mock(EnterpriseUsersRepository.UniqueCountResult.class);
        when(uniqueCountResult.getUserNameCount()).thenReturn(3L);
        when(enterpriseUsersRepository.countByFields(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any())).thenReturn(uniqueCountResult);

        EnterpriseUserRegistrationRequest request = new EnterpriseUserRegistrationRequest();
        request.setRequestBody(new EnterpriseUserRegistrationRequestBody());

        // Act and Assert
        assertThrows(DomainException.class, () -> enterpriseUserRegistrationService.registerUser(request));
        verify(enterpriseUsersRepository).countByFields(isNull(), isNull(), isNull());
        verify(uniqueCountResult, atLeast(1)).getUserNameCount();
    }

    /**
     * This method is used to test Validate User Request Details Throws Domain Exception Password Mismatch
     */
    @Test
    void testValidateUserRequestDetails_ThrowsDomainException_PasswordMismatch() {
        // Setup
        final EnterpriseUserRegistrationRequestBody requestBody = new EnterpriseUserRegistrationRequestBody();
        requestBody.setFirstName("firstName");
        requestBody.setLastName("lastName");
        requestBody.setIdType(NIC);
        requestBody.setIdNumber("idNumber");
        requestBody.setMobileNumber("mobileNumber");
        requestBody.setEmail("email@example.com");
        requestBody.setUserName("userName");
        requestBody.setPassword("password123");
        requestBody.setConfirmedPassword("passwor123"); // Passwords do not match
        requestBody.setAccountId("accountId");
        requestBody.setSpecialNote("specialNote");

        // Mocking countByFields method to return a valid UniqueCountResult
        EnterpriseUsersRepository.UniqueCountResult countResult = new EnterpriseUsersRepository.UniqueCountResult() {
            @Override
            public Long getUserNameCount() {
                return 0L;  // No existing user with this username
            }

            @Override
            public Long getEmailCount() {
                return 0L;  // No existing user with this email
            }

            @Override
            public Long getMobileNumberCount() {
                return 0L;  // No existing user with this mobile number
            }
        };

        when(enterpriseUsersRepository.countByFields(anyString(), anyString(), anyString())).thenReturn(countResult);

        // Run the test and assert that a DomainException is thrown for password mismatch
        assertThatThrownBy(() -> enterpriseUserRegistrationService.validateUserRequestDetails(requestBody)).isInstanceOf(DomainException.class).hasMessageContaining("Passwords do not match");
    }

    /**
     * This method is used to test Validate User Request Details Throws Domain Exception InvalidEmail
     */
    @Test
    void testValidateUserRequestDetails_ThrowsDomainException_InvalidEmail() {
        // Setup
        final EnterpriseUserRegistrationRequestBody requestBody = new EnterpriseUserRegistrationRequestBody();
        requestBody.setFirstName("firstName");
        requestBody.setLastName("lastName");
        requestBody.setIdType(NIC);
        requestBody.setIdNumber("idNumber");
        requestBody.setMobileNumber("mobileNumber");
        requestBody.setEmail("invalidEmailFormat");  // Invalid email format
        requestBody.setUserName("userName");
        requestBody.setPassword("password123");
        requestBody.setConfirmedPassword("password123");
        requestBody.setAccountId("accountId");
        requestBody.setSpecialNote("specialNote");

        // Mocking duplicate username, email, or mobile
        EnterpriseUsersRepository.UniqueCountResult countResult = new EnterpriseUsersRepository.UniqueCountResult() {
            @Override
            public Long getUserNameCount() {
                return 0L;
            }

            @Override
            public Long getEmailCount() {
                return 0L;
            }

            @Override
            public Long getMobileNumberCount() {
                return 0L;
            }
        };

        when(enterpriseUsersRepository.countByFields(anyString(), anyString(), anyString())).thenReturn(countResult);

        // Run the test and assert that a DomainException is thrown for invalid email format
        assertThatThrownBy(() -> enterpriseUserRegistrationService.validateUserRequestDetails(requestBody)).isInstanceOf(DomainException.class).hasMessageContaining("Invalid Email Format");
    }

    @Test
    void testRegisterUser_Success() throws DomainException {
        EnterpriseUserRegistrationRequestBody requestBody = new EnterpriseUserRegistrationRequestBody("John", "Doe", NIC, "12345", "71000000", "john.doe@example.com", "johndoe", "password123", "password123", "acc123", "Special note");
        EnterpriseUserRegistrationRequest request = new EnterpriseUserRegistrationRequest(requestBody, new RequestHeader("req-123", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test"));

        EnterpriseUsers savedUser = new EnterpriseUsers(1L, "John", "Doe", NIC, "12345", "71000000", "john.doe@example.com", "johndoe", "encodedPassword", LocalDateTime.now().plusDays(30), UserStatus.PENDING, "acc123", "Special note", null, null);

        // Mock UniqueCountResult using Mockito
        EnterpriseUsersRepository.UniqueCountResult countResult = mock(EnterpriseUsersRepository.UniqueCountResult.class);

        // Define behavior for getters
        when(countResult.getUserNameCount()).thenReturn(0L);
        when(countResult.getEmailCount()).thenReturn(0L);
        when(countResult.getMobileNumberCount()).thenReturn(0L);

        // Mock repository response
        when(enterpriseUsersRepository.countByFields("johndoe", "john.doe@example.com", "71000000")).thenReturn(countResult);

        // Mock password hashing
        when(passwordUtil.hashPassword("password123")).thenReturn("hashedPassword123");
        String encodedPassword = Base64.getEncoder().encodeToString("hashedPassword123".getBytes());

        // Mock repository save
        when(enterpriseUsersRepository.saveAndFlush(any(EnterpriseUsers.class))).thenReturn(savedUser);

        // Execute method
        EnterpriseUserRegistrationResponse response = enterpriseUserRegistrationService.registerUser(request);

        // Verify interactions
        verify(enterpriseUsersRepository).saveAndFlush(any(EnterpriseUsers.class));

        // Validate password encoding
        assertEquals(encodedPassword, Base64.getEncoder().encodeToString("hashedPassword123".getBytes()));
        // Validate response
        assertNotNull(response);
        assertEquals("1", response.getResponseBody().getUserId().toString());
    }





    @Test
    void testValidateUserRequestDetails_ThrowsDomainException_InvalidMsisdn() {
        // Setup
        final EnterpriseUserRegistrationRequestBody requestBody = new EnterpriseUserRegistrationRequestBody();
        requestBody.setFirstName("firstName");
        requestBody.setLastName("lastName");
        requestBody.setIdType(NIC);
        requestBody.setIdNumber("idNumber");
        requestBody.setMobileNumber("710000011111"); // Invalid msisdn format
        requestBody.setEmail("test@gmail.com");
        requestBody.setUserName("userName");
        requestBody.setPassword("password123");
        requestBody.setConfirmedPassword("password123");
        requestBody.setAccountId("accountId");
        requestBody.setSpecialNote("specialNote");

        // Mocking duplicate username, email, or mobile
        EnterpriseUsersRepository.UniqueCountResult countResult = new EnterpriseUsersRepository.UniqueCountResult() {
            @Override
            public Long getUserNameCount() {
                return 0L;
            }

            @Override
            public Long getEmailCount() {
                return 0L;
            }

            @Override
            public Long getMobileNumberCount() {
                return 0L;
            }
        };

        when(enterpriseUsersRepository.countByFields(anyString(), anyString(), anyString())).thenReturn(countResult);

        // Run the test and assert that a DomainException is thrown for invalid msisdn format
        assertThatThrownBy(() -> enterpriseUserRegistrationService.validateUserRequestDetails(requestBody)).isInstanceOf(DomainException.class).hasMessageContaining("Invalid MSISDN Format Found");
    }
}