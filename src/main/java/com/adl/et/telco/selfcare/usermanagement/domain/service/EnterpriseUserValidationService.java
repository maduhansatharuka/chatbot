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

import com.adl.et.telco.selfcare.usermanagement.application.dto.userValidation.EnterpriseUserValidationRequest;
import com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode;
import com.adl.et.telco.selfcare.usermanagement.application.util.PasswordUtil;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.EnterpriseUsersRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.ResponseHeader;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.userValidation.EnterpriseUserValidationResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.userValidation.EnterpriseUserValidationResponseBody;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.EnterpriseUsers;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;

import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.ACCOUNT_LOCKED_ERROR;
import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.ENTERPRISE_USER_VALIDATION_ERROR;
import static com.adl.et.telco.selfcare.usermanagement.application.util.HandleErrorLogs.logError;
import static com.adl.et.telco.selfcare.usermanagement.domain.entities.UserStatus.APPROVED;

/**
 * This service class is used to implement logic related to enterprise user validation using userName and the Password
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/04
 */
@Service
public class EnterpriseUserValidationService {

    private final EnterpriseUsersRepository repository;
    private final PasswordUtil passwordUtil;

    @Value("${security.max-login-attempts}")
    private int maxLoginAttempts;

    @Value("${security.lock-duration-hours}")
    private int lockDurationHours;

    public EnterpriseUserValidationService(EnterpriseUsersRepository repository, PasswordUtil passwordUtil) {
        this.repository = repository;
        this.passwordUtil = passwordUtil;
    }

    /**
     * This method is used to implement logic related to user validation using userName and the Password.
     *
     * @param request The request containing the criteria for the enterprise user validation
     * @return A response containing the new validated user details
     */
    public EnterpriseUserValidationResponse validateUserByUserNameAndPassword(EnterpriseUserValidationRequest request) throws DomainException {
        try {
            String userName = request.getRequestBody().getUserName();
            String password = request.getRequestBody().getPassword();
            LocalDateTime now = LocalDateTime.now();

            EnterpriseUsers enterpriseUser = repository.validateEnterpriseUserByGivenUserName(userName, APPROVED, LocalDateTime.now());

            if (enterpriseUser == null) {
                throw new DomainException(ENTERPRISE_USER_VALIDATION_ERROR.getDescription(), ENTERPRISE_USER_VALIDATION_ERROR.getCode());
            }

            // Check if the account is locked
            if (enterpriseUser.getLoginBlockedUntil() != null && enterpriseUser.getLoginBlockedUntil().isAfter(now)) {
                throw new DomainException(ACCOUNT_LOCKED_ERROR.getDescription(), ACCOUNT_LOCKED_ERROR.getCode());
            }

            // Decode the Base64-encoded password hash
            String encodedHash = enterpriseUser.getPassword();
            byte[] decodedHashBytes = Base64.getDecoder().decode(encodedHash);
            String decodedHash = new String(decodedHashBytes);

            // Verify the decoded hash with the plain password
            if (!passwordUtil.verifyPassword(decodedHash, password)) {
                enterpriseUser.setAttempts(enterpriseUser.getAttempts() + 1);

                if (enterpriseUser.getAttempts() >= maxLoginAttempts) {
                    enterpriseUser.setLoginBlockedUntil(now.plusHours(lockDurationHours)); // Lock for 1 hour
                }
                repository.save(enterpriseUser);
                throw new DomainException(ENTERPRISE_USER_VALIDATION_ERROR.getDescription(), ENTERPRISE_USER_VALIDATION_ERROR.getCode());
            }

            // Reset login attempts if successful
            enterpriseUser.setAttempts(0);
            enterpriseUser.setLoginBlockedUntil(null);
            repository.save(enterpriseUser);

            EnterpriseUserValidationResponseBody responseBody = new EnterpriseUserValidationResponseBody(enterpriseUser.getUserId(), enterpriseUser.getEmail(), enterpriseUser.getFirstName(), enterpriseUser.getMobileNumber(), enterpriseUser.getUserName(), enterpriseUser.getPassword());
            ResponseHeader responseHeader = new ResponseHeader(request.getRequestHeader().getRequestId(), LocalDateTime.now().toString(), DomainErrorCode.SUCCESS.getCode(), DomainErrorCode.SUCCESS.getDescription(), "200");

            return new EnterpriseUserValidationResponse(responseBody, responseHeader);
        } catch (Exception e) {
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            if (e instanceof DomainException) {
                throw e;
            }

            throw new DomainException(ENTERPRISE_USER_VALIDATION_ERROR.getDescription(), ENTERPRISE_USER_VALIDATION_ERROR.getCode());
        }
    }
}