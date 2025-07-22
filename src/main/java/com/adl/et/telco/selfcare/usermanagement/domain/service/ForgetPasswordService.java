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

import com.adl.et.telco.selfcare.usermanagement.application.dto.forgetPasswordGenerateEmail.ForgetPasswordGenerateEmailRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.forgetPasswordValidateUser.ForgetPasswordValidateUserRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.resetPassword.ResetPasswordRequest;
import com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode;
import com.adl.et.telco.selfcare.usermanagement.application.util.PasswordPolicyValidator;
import com.adl.et.telco.selfcare.usermanagement.application.util.PasswordUtil;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.EnterpriseUsersRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.PasswordHistoryRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.SessionTokenRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.UserTokenRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.ResponseHeader;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.forgetPasswordGenerateEmail.ForgetPasswordGenerateEmailResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.forgetPasswordValidateUser.ForgetPasswordValidateUserResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.forgetPasswordValidateUser.ForgetPasswordValidateUserResponseBody;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.resetPassword.ResetPasswordResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.resetPassword.ResetPasswordResponseBody;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.*;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.external.dto.RequestHeader;
import com.adl.et.telco.selfcare.usermanagement.external.dto.sendemail.PlaceholderList;
import com.adl.et.telco.selfcare.usermanagement.external.dto.sendemail.SendEmailRequest;
import com.adl.et.telco.selfcare.usermanagement.external.dto.sendemail.SendEmailRequestBody;
import com.adl.et.telco.selfcare.usermanagement.external.service.SendEmailService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.regex.Pattern;

import static com.adl.et.telco.selfcare.usermanagement.application.util.Constant.*;
import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.*;
import static com.adl.et.telco.selfcare.usermanagement.application.util.HandleErrorLogs.logError;
import static com.adl.et.telco.selfcare.usermanagement.application.util.HandleErrorLogs.logInfo;

/**
 * This service class is used to implement logic related to Forget Password
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/02/26
 */
@Service
public class ForgetPasswordService {

    private final EnterpriseUsersRepository enterpriseUsersRepository;
    private final SendEmailService sendEmailService;
    private final UserTokenRepository userTokenRepository;
    private final SessionTokenRepository sessionTokenRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final JwtService jwtService;
    private final PasswordUtil passwordUtil;
    private final PasswordPolicyValidator passwordPolicyValidator;

    @Value("${security.jwt.expiretime.forgot.password.token.millisecond}")
    private Integer forgotPasswordTokenMillisecond;

    @Value("${forgot.password.reset.link.url}")
    private String forgotPasswordResetLinkUrl;

    @Value("${validation.regex.email}")
    private String validationRegexEmail;

    @Value("${forgot.password.session.token.expiry.time.in.minutes}")
    private Integer forgotPasswordSessionTokenExpiryTimeInMinutes;

    @Value("${password.expiration-period}")
    private String passwordExpirationPeriod;

    @Value("${maximum.number.of.password.history.records}")
    private Integer maximumNumberOfPasswordHistoryRecords;

    @Value("${forgot.password.email.subject}")
    private String forgotPasswordEmailSubject;

    @Value("${forgot.password.success.email.subject}")
    private String forgotPasswordSuccessEmailSubject;

    @Value("${forgot.password.email.from.name}")
    private String forgotPasswordEmailFromName;

    public ForgetPasswordService(EnterpriseUsersRepository repository, SendEmailService sendEmailService, UserTokenRepository userTokenRepository, JwtService jwtService, SessionTokenRepository sessionTokenRepository, PasswordHistoryRepository passwordHistoryRepository, PasswordUtil passwordUtil, PasswordPolicyValidator passwordPolicyValidator) {
        this.enterpriseUsersRepository = repository;
        this.sendEmailService = sendEmailService;
        this.userTokenRepository = userTokenRepository;
        this.jwtService = jwtService;
        this.sessionTokenRepository = sessionTokenRepository;
        this.passwordHistoryRepository = passwordHistoryRepository;
        this.passwordUtil = passwordUtil;
        this.passwordPolicyValidator = passwordPolicyValidator;
    }

    /**
     * This method is used to implement logic related to Forget Password - Generate Email
     *
     * @param request The request containing the criteria for the forget password-generate email
     * @return The response
     */
    public ForgetPasswordGenerateEmailResponse forgetPasswordGenerateEmail(ForgetPasswordGenerateEmailRequest request) throws DomainException {
        try {
            // Validate email format
            Pattern pattern = Pattern.compile(validationRegexEmail);
            if (!pattern.matcher(request.getRequestBody().getEmail()).matches()) {
                throw new DomainException(INVALID_EMAIL_FORMAT_ERROR.getDescription(), INVALID_EMAIL_FORMAT_ERROR.getCode());
            }

            EnterpriseUsers enterpriseUsers = enterpriseUsersRepository.findUserByTheEmail(request.getRequestBody().getEmail(), LocalDateTime.now());

            if (enterpriseUsers != null) {
                if (enterpriseUsers.getStatus() == UserStatus.APPROVED) {
                    String uniqueId = request.getRequestHeader().getMsisdn() + System.currentTimeMillis() + UUID.randomUUID();

                    // Create the token
                    String forgotPasswordToken = jwtService.createTokenForForgotPassword(enterpriseUsers, uniqueId);
                    System.out.println(forgotPasswordToken);

                    // Save Token Details In the Database
                    saveUserTokenDetailsInTheDatabase(enterpriseUsers, uniqueId);

                    // Send the email
                    sendEmailService.sendEmail(mapToPasswordRestSendEmailRequest(forgotPasswordToken, enterpriseUsers, request));
                } else {
                    throw new DomainException(USER_HAS_NOT_BEEN_APPROVED_ERROR.getDescription(), USER_HAS_NOT_BEEN_APPROVED_ERROR.getCode());
                }
            } else {
                logInfo(ENTERPRISE_USER_NOT_EXISTS_ERROR.getDescription());
            }

            ResponseHeader responseHeader = new ResponseHeader(request.getRequestHeader().getRequestId(), LocalDateTime.now().toString(), DomainErrorCode.SUCCESS.getCode(), DomainErrorCode.SUCCESS.getDescription(), "200");
            return new ForgetPasswordGenerateEmailResponse(responseHeader);
        } catch (Exception e) {
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            if (e instanceof DomainException) {
                throw e;
            }
            throw new DomainException(FORGET_PASSWORD_GENERATE_EMAIL_ERROR.getDescription(), FORGET_PASSWORD_GENERATE_EMAIL_ERROR.getCode());
        }
    }

    /**
     * This method is used to map the request to send email External Request
     *
     * @param forgotPasswordToken
     * @param enterpriseUsers
     * @param forgetPasswordGenerateEmailRequest
     * @return
     */
    private SendEmailRequest mapToPasswordRestSendEmailRequest(String forgotPasswordToken, EnterpriseUsers enterpriseUsers, ForgetPasswordGenerateEmailRequest forgetPasswordGenerateEmailRequest) {
        SendEmailRequestBody requestBody = new SendEmailRequestBody();
        requestBody.setSubject(forgotPasswordEmailSubject);
        requestBody.setFromName(forgotPasswordEmailFromName);
        requestBody.setToList(Collections.singletonList(enterpriseUsers.getEmail()));
        requestBody.setTemplateId(FORGOT_PASSWORD_TEMPLATE_ID);

        // Placeholder values for email template
        List<PlaceholderList> placeholderList = new ArrayList<>();
        placeholderList.add(new PlaceholderList(FIRST_NAME, enterpriseUsers.getFirstName()));
        placeholderList.add(new PlaceholderList(LAST_NAME, enterpriseUsers.getLastName()));
        placeholderList.add(new PlaceholderList(RESET_LINK, forgotPasswordResetLinkUrl + forgotPasswordToken));
        placeholderList.add(new PlaceholderList(EXPIRATION_TIME, forgotPasswordTokenMillisecond / 60000 + MINUTES));

        requestBody.setPlaceholderList(placeholderList);

        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setRequestId(forgetPasswordGenerateEmailRequest.getRequestHeader().getRequestId());
        requestHeader.setTimestamp(LocalDateTime.now().toString());
        requestHeader.setChannel(forgetPasswordGenerateEmailRequest.getRequestHeader().getChannel());

        return new SendEmailRequest(requestBody, requestHeader);
    }

    /**
     * This is used to save/update supplementary number to the DB
     *
     * @param enterpriseUsers
     * @param uniqueId
     */
    private void saveUserTokenDetailsInTheDatabase(EnterpriseUsers enterpriseUsers, String uniqueId) {

        UserTokenEntity userTokenEntity = new UserTokenEntity();
        userTokenEntity.setUserId(enterpriseUsers.getUserId());
        userTokenEntity.setEmail(enterpriseUsers.getEmail());
        userTokenEntity.setUniqueId(uniqueId);
        userTokenEntity.setStatus(TokenStatus.ACTIVE);

        userTokenRepository.saveAndFlush(userTokenEntity);
    }

    /**
     * This method is used to implement logic related to Forget Password - Generate Email
     *
     * @param request The request containing the criteria for the forget password-generate email
     * @return The response
     */
    public ForgetPasswordValidateUserResponse forgetPasswordUserValidation(ForgetPasswordValidateUserRequest request) throws DomainException {
        try {
            String token = request.getRequestBody().getToken();

            // Validate User Token
            Claims claims = jwtService.isAccessTokenExpired(token);

            // Extract required details
            String receivedTokenId = claims.get(TOKEN_ID, String.class);
            String receivedUserId = claims.get(USER_ID, String.class);
            String receivedEmail = claims.get(EMAIL, String.class);

            //Validate the User Token with the DB
            validateUserToken(receivedTokenId, receivedUserId, receivedEmail);

            //Generate One time Session Token
            String sessionToken = jwtService.generateSessionToken(receivedUserId);

            // Save Session Token Details In the Database
            saveSessionTokenDetailsInTheDatabase(receivedUserId, receivedEmail, sessionToken);

            ForgetPasswordValidateUserResponseBody responseBody = new ForgetPasswordValidateUserResponseBody(receivedUserId, sessionToken);
            ResponseHeader responseHeader = new ResponseHeader(request.getRequestHeader().getRequestId(), LocalDateTime.now().toString(), DomainErrorCode.SUCCESS.getCode(), DomainErrorCode.SUCCESS.getDescription(), "200");

            return new ForgetPasswordValidateUserResponse(responseBody, responseHeader);

        } catch (Exception e) {
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            if (e instanceof DomainException) {
                throw e;
            }
            throw new DomainException(FORGET_PASSWORD_USER_VALIDATION_ERROR.getDescription(), FORGET_PASSWORD_USER_VALIDATION_ERROR.getCode());
        }
    }

    /**
     * This method is for Validating the user token
     *
     * @param receivedTokenId
     * @param receivedUserId
     * @param receivedEmail
     * @throws DomainException
     */
    private void validateUserToken(String receivedTokenId, String receivedUserId, String receivedEmail) throws DomainException {

        UserTokenEntity userToken = userTokenRepository.validateTheUserToken(receivedUserId, receivedEmail, receivedTokenId, TokenStatus.ACTIVE);

        if (userToken == null) {
            throw new DomainException(INVALID_USER_TOKEN_ERROR.getDescription(), INVALID_USER_TOKEN_ERROR.getCode());
        } else {
            userToken.setStatus(TokenStatus.EXPIRED);
            userTokenRepository.save(userToken);
        }
    }

    /**
     * This method is used save Session Token Details to the DB
     *
     * @param receivedUserId
     * @param receivedEmail
     * @param sessionToken
     */
    private void saveSessionTokenDetailsInTheDatabase(String receivedUserId, String receivedEmail, String sessionToken) {

        SessionTokenEntity sessionTokenEntity = new SessionTokenEntity();
        sessionTokenEntity.setUserId(receivedUserId);
        sessionTokenEntity.setEmail(receivedEmail);
        sessionTokenEntity.setSessionToken(sessionToken);
        sessionTokenEntity.setExpiryTime(LocalDateTime.now().plusMinutes(forgotPasswordSessionTokenExpiryTimeInMinutes));
        sessionTokenEntity.setStatus(TokenStatus.ACTIVE);

        sessionTokenRepository.saveAndFlush(sessionTokenEntity);
    }

    /**
     * This method is used to implement logic related to Reset Password
     *
     * @param request The request containing the criteria for the reset password
     * @return The response
     */
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) throws DomainException {
        try {
            String userId = request.getRequestBody().getUserId();
            String newPassword = request.getRequestBody().getPassword();

            // Validate Session Token
            SessionTokenEntity sessionTokenEntity = validateSessionToken(request.getRequestBody().getSessionToken(), userId);

            // Hash new password
            String hashedPassword = passwordUtil.hashPassword(newPassword);
            String encodedPassword = Base64.getEncoder().encodeToString(hashedPassword.getBytes());

            // Validate Password History
            handlePasswordHistory(userId, newPassword, encodedPassword, request);

            EnterpriseUsers enterpriseUsers = enterpriseUsersRepository.findUserByUserId(userId);

            // Validate Password according to the password policy
            passwordPolicyValidator.validate(newPassword, enterpriseUsers.getUserName(), enterpriseUsers.getFirstName(), enterpriseUsers.getLastName(), enterpriseUsers.getEmail(), request.getRequestHeader());

            // Update Enterprise User Entity
            updateRelevantEnterpriseUserWithTheNewPassword(enterpriseUsers, encodedPassword);

            // Invalidate session token after password update
            invalidateSessionToken(sessionTokenEntity);

            // Send the email
            sendEmailService.sendEmail(mapToPasswordRestSuccessSendEmailRequest(enterpriseUsers, request));

            ResetPasswordResponseBody responseBody = new ResetPasswordResponseBody(request.getRequestBody().getUserId());
            ResponseHeader responseHeader = new ResponseHeader(request.getRequestHeader().getRequestId(), LocalDateTime.now().toString(), DomainErrorCode.SUCCESS.getCode(), DomainErrorCode.SUCCESS.getDescription(), "200");

            return new ResetPasswordResponse(responseBody, responseHeader);

        } catch (Exception e) {
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            if (e instanceof DomainException) {
                throw e;
            }
            throw new DomainException(PASSWORD_RESET_ERROR.getDescription(), PASSWORD_RESET_ERROR.getCode());
        }
    }

    /**
     * This method is used  to Validate session Token
     *
     * @param token
     * @param userId
     * @return
     * @throws DomainException
     */
    private SessionTokenEntity validateSessionToken(String token, String userId) throws DomainException {
        SessionTokenEntity sessionToken = sessionTokenRepository.validateSessionToken(userId, token, TokenStatus.ACTIVE, LocalDateTime.now());
        if (sessionToken == null) {
            throw new DomainException(INVALID_SESSION_TOKEN_ERROR.getDescription(), INVALID_SESSION_TOKEN_ERROR.getCode());
        }
        return sessionToken;
    }

    /**
     * This method is used to handle Password History Related Logics
     *
     * @param userId
     * @param newPassword
     * @param encodedPassword
     * @param request
     * @throws DomainException
     */
    private void handlePasswordHistory(String userId, String newPassword, String encodedPassword, ResetPasswordRequest request) throws DomainException {
        // Fetch password history
        List<PasswordHistoryEntity> passwordHistory = passwordHistoryRepository.findByUserIdOrderByCreatedDateDesc(userId);

        // Check if new password matches last 3 passwords
        for (PasswordHistoryEntity history : passwordHistory) {
            byte[] decodedHashBytes = Base64.getDecoder().decode(history.getPassword());
            String decodedHash = new String(decodedHashBytes);

            if (passwordUtil.verifyPassword(decodedHash, newPassword)) {
                throw new DomainException(PASSWORD_HISTORY_ERROR.getDescription(), PASSWORD_HISTORY_ERROR.getCode());
            }
        }

        // Save new password to history
        PasswordHistoryEntity newHistory = new PasswordHistoryEntity(null, userId, encodedPassword, LocalDateTime.now(), request.getRequestHeader().getUserId());
        passwordHistoryRepository.save(newHistory);

        // If more than 3 passwords exist, delete the oldest
        if (passwordHistory.size() >= maximumNumberOfPasswordHistoryRecords) {
            passwordHistoryRepository.deleteOldestPassword(userId);
        }
    }

    /**
     * This method is used to invalidate Session Token
     *
     * @param sessionTokenEntity
     */
    private void invalidateSessionToken(SessionTokenEntity sessionTokenEntity) {

        sessionTokenEntity.setStatus(TokenStatus.EXPIRED);
        sessionTokenRepository.saveAndFlush(sessionTokenEntity);
    }

    /**
     * This method is used to update Relevant Enterprise User with the New Password
     *
     * @param enterpriseUsers
     * @param newPassword
     */
    private void updateRelevantEnterpriseUserWithTheNewPassword(EnterpriseUsers enterpriseUsers, String newPassword) {

        Period expirationPeriodValue = Period.parse(passwordExpirationPeriod);
        LocalDateTime passwordExpirationDate = LocalDateTime.now().plus(expirationPeriodValue);

        if (enterpriseUsers != null) {
            enterpriseUsers.setPassword(newPassword);
            enterpriseUsers.setPasswordExpiryDate(passwordExpirationDate);

            enterpriseUsersRepository.saveAndFlush(enterpriseUsers);
        }
    }

    /**
     * This method is used to map the request to send email External Request
     *
     * @param enterpriseUsers
     * @param resetPasswordRequest
     * @return
     */
    private SendEmailRequest mapToPasswordRestSuccessSendEmailRequest(EnterpriseUsers enterpriseUsers, ResetPasswordRequest resetPasswordRequest) {
        SendEmailRequestBody requestBody = new SendEmailRequestBody();
        requestBody.setSubject(forgotPasswordSuccessEmailSubject);
        requestBody.setFromName(forgotPasswordEmailFromName);
        requestBody.setToList(Collections.singletonList(enterpriseUsers.getEmail()));
        requestBody.setTemplateId(FORGOT_PASSWORD_SUCCESS_EMAIL_TEMPLATE_ID);

        // Placeholder values for email template
        List<PlaceholderList> placeholderList = new ArrayList<>();
        placeholderList.add(new PlaceholderList(FIRST_NAME, enterpriseUsers.getFirstName()));
        placeholderList.add(new PlaceholderList(LAST_NAME, enterpriseUsers.getLastName()));

        requestBody.setPlaceholderList(placeholderList);

        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setRequestId(resetPasswordRequest.getRequestHeader().getRequestId());
        requestHeader.setTimestamp(LocalDateTime.now().toString());
        requestHeader.setChannel(resetPasswordRequest.getRequestHeader().getChannel());

        return new SendEmailRequest(requestBody, requestHeader);
    }
}