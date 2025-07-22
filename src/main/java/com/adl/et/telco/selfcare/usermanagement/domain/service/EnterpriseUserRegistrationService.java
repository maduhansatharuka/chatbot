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

import com.adl.et.telco.selfcare.usermanagement.application.dto.userRegistration.EnterpriseUserRegistrationRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.userRegistration.EnterpriseUserRegistrationRequestBody;
import com.adl.et.telco.selfcare.usermanagement.application.util.Constant;
import com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode;
import com.adl.et.telco.selfcare.usermanagement.application.util.PasswordPolicyValidator;
import com.adl.et.telco.selfcare.usermanagement.application.util.PasswordUtil;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.EnterpriseUsersRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.ResponseHeader;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.userRegistration.EnterpriseUserRegistrationResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.userRegistration.EnterpriseUserRegistrationResponseBody;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.EnterpriseUsers;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Base64;
import java.util.regex.Pattern;

import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.*;
import static com.adl.et.telco.selfcare.usermanagement.application.util.HandleErrorLogs.logError;
import static com.adl.et.telco.selfcare.usermanagement.domain.entities.UserStatus.PENDING;

/**
 * This service class is used to implement logic related to enterprise user registration
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/16
 */
@Service
public class EnterpriseUserRegistrationService {

    private final EnterpriseUsersRepository repository;
    private final PasswordUtil passwordUtil;
    private final PasswordPolicyValidator passwordPolicyValidator;

    @Value("${password.expiration-period}")
    private String passwordExpirationPeriod;

    @Value("${validation.regex.msisdn}")
    private String validationRegexMsisdn;

    @Value("${validation.regex.email}")
    private String validationRegexEmail;

    public EnterpriseUserRegistrationService(EnterpriseUsersRepository repository, PasswordUtil passwordUtil, PasswordPolicyValidator passwordPolicyValidator) {
        this.repository = repository;
        this.passwordUtil = passwordUtil;
        this.passwordPolicyValidator = passwordPolicyValidator;
    }

    /**
     * This method is used to implement logic related to online user registration
     *
     * @param request The request containing the criteria for the enterprise user registration
     * @return A response containing the new registered user details
     */
    public EnterpriseUserRegistrationResponse registerUser(EnterpriseUserRegistrationRequest request) throws DomainException {
        try {
            EnterpriseUserRegistrationRequestBody requestBody = request.getRequestBody();

            // Validate Password according to the password policy
            passwordPolicyValidator.validate(requestBody.getPassword(), requestBody.getUserName(), requestBody.getFirstName(), requestBody.getLastName(), requestBody.getEmail(), request.getRequestHeader());

            validateUserRequestDetails(requestBody);

            // Hash the password
            String hashedPassword = passwordUtil.hashPassword(requestBody.getPassword());
            String encodedPassword = Base64.getEncoder().encodeToString(hashedPassword.getBytes());

            Period expirationPeriodValue = Period.parse(passwordExpirationPeriod);
            LocalDateTime passwordExpirationDate = LocalDateTime.now().plus(expirationPeriodValue);

            EnterpriseUsers enterpriseUsers = new EnterpriseUsers(null, requestBody.getFirstName(), requestBody.getLastName(), requestBody.getIdType(), requestBody.getIdNumber(), requestBody.getMobileNumber(), requestBody.getEmail(), requestBody.getUserName(), encodedPassword, passwordExpirationDate, PENDING, requestBody.getAccountId(), requestBody.getSpecialNote(), null, null);
            EnterpriseUsers response = repository.saveAndFlush(enterpriseUsers);

            EnterpriseUserRegistrationResponseBody responseBody = new EnterpriseUserRegistrationResponseBody(response.getUserId());

            ResponseHeader responseHeader = new ResponseHeader(request.getRequestHeader().getRequestId(), LocalDateTime.now().toString(), DomainErrorCode.SUCCESS.getCode(), DomainErrorCode.SUCCESS.getDescription(), "200");
            return new EnterpriseUserRegistrationResponse(responseBody, responseHeader);
        } catch (Exception e) {
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            if (e instanceof DomainException) {
                throw e;
            }
            throw new DomainException(ENTERPRISE_USER_REGISTER_ERROR.getDescription(), ENTERPRISE_USER_REGISTER_ERROR.getCode());
        }
    }

    /**
     * Validates the user registration request.
     *
     * @param requestBody The request body containing user details.
     * @throws DomainException If any validation fails.
     */
    public void validateUserRequestDetails(EnterpriseUserRegistrationRequestBody requestBody) throws DomainException {
        // Validate username for duplicates
        EnterpriseUsersRepository.UniqueCountResult countResult = repository.countByFields(requestBody.getUserName(), requestBody.getEmail(), requestBody.getMobileNumber());

        if (countResult.getUserNameCount() != null && countResult.getUserNameCount() > 0) {
            throw new DomainException(EXISTING_ENTERPRISE_USER_NAME.getDescription(), EXISTING_ENTERPRISE_USER_NAME.getCode());
        }
        if (countResult.getEmailCount() != null && countResult.getEmailCount() > 0) {
            throw new DomainException(EXISTING_ENTERPRISE_EMAIL.getDescription(), EXISTING_ENTERPRISE_EMAIL.getCode());
        }
        if (countResult.getMobileNumberCount() != null && countResult.getMobileNumberCount() > 0) {
            throw new DomainException(EXISTING_ENTERPRISE_MOBILE_NUMBER.getDescription(), EXISTING_ENTERPRISE_MOBILE_NUMBER.getCode());
        }

        // Validate password and confirmed password match
        if (!requestBody.getPassword().equals(requestBody.getConfirmedPassword())) {
            throw new DomainException(PASSWORD_MISMATCH_ERROR.getDescription(), PASSWORD_MISMATCH_ERROR.getCode());
        }

        // Validate email format
        Pattern pattern = Pattern.compile(validationRegexEmail);
        if (!pattern.matcher(requestBody.getEmail()).matches()) {
            throw new DomainException(INVALID_EMAIL_FORMAT_ERROR.getDescription(), INVALID_EMAIL_FORMAT_ERROR.getCode());
        }

        // Validate MSISDN format
        Pattern msisdnPattern = Pattern.compile(validationRegexMsisdn);
        if (requestBody.getMobileNumber() == null || !msisdnPattern.matcher(requestBody.getMobileNumber()).matches()) {
            throw new DomainException(INVALID_MSISDN_FORMAT_ERROR.getDescription(), INVALID_MSISDN_FORMAT_ERROR.getCode());
        }
    }
}