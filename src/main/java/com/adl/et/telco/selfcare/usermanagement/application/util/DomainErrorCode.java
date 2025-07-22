/**
 * Copyrights 2024 Axiata Digital Labs Pvt Ltd.
 * All Rights Reserved.
 * <p>
 * These material are unpublished, proprietary, confidential source
 * code of Axiata Digital Labs Pvt Ltd (ADL) and constitute a TRADE
 * SECRET of ADL.
 * <p>
 * ADL retains all title to and intellectual property rights in these
 * materials.
 */
package com.adl.et.telco.selfcare.usermanagement.application.util;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * This class contains all the Domain Error Codes in the Application.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/16
 */
@Getter
public enum DomainErrorCode {

    SUCCESS("UMS1000", "SUCCESS", HttpStatus.OK),

    //System Error Codes
    ENTERPRISE_USER_REGISTER_ERROR("UMS3001", "Error occurred while creating the user", HttpStatus.INTERNAL_SERVER_ERROR),
    EXISTING_ENTERPRISE_USER_NAME("UMS3002", "This Username has been already taken", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH_ERROR("UMS3003", "Passwords do not match", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT_ERROR("UMS3004", "Invalid Email Format", HttpStatus.BAD_REQUEST),
    EXISTING_ENTERPRISE_EMAIL("UMS3005", "This Email has been already taken", HttpStatus.BAD_REQUEST),
    EXISTING_ENTERPRISE_MOBILE_NUMBER("UMS3006", "This Mobile Number has been already taken", HttpStatus.BAD_REQUEST),
    PASSWORD_LENGTH_VALIDATE("UMS3007", "Password must be between 8 and 12 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_VALIDATION_FOR_UPPERCASE_LETTERS("UMS3008", "Password must contain at least one uppercase letter", HttpStatus.BAD_REQUEST),
    PASSWORD_VALIDATION_FOR_LOWERCASE_LETTERS("UMS3009", "Password must contain at least one lowercase letter", HttpStatus.BAD_REQUEST),
    PASSWORD_VALIDATION_FOR_AT_LEAST_ONE_DIGIT("UMS3010", "Password must contain at least one digit", HttpStatus.BAD_REQUEST),
    PASSWORD_VALIDATION_FOR_AT_LEAST_ONE_SPECIAL_CHARACTER("UMS3011", "Password must contain at least one special character", HttpStatus.BAD_REQUEST),
    COMMON_PASSWORD_ERROR("UMS3012", "Password is too common. Please choose a different password", HttpStatus.BAD_REQUEST),
    REPEATED_CHARACTERS_IN_PASSWORD_ERROR("UMS3013", "Password contains too many repeated characters", HttpStatus.BAD_REQUEST),
    USER_INFORMATION_IN_PASSWORD_ERROR("UMS3014", "Password must not contain parts of user information", HttpStatus.BAD_REQUEST),
    FORGET_PASSWORD_GENERATE_EMAIL_ERROR("UMS3015", "Error Occurred While Generating Forget Password Email", HttpStatus.BAD_REQUEST),
    USER_HAS_NOT_BEEN_APPROVED_ERROR("UMS3016", "User has not been approved yet", HttpStatus.BAD_REQUEST),
    TOKEN_CREATE_ERROR("UMS3017", "Error occurred while creating the User token", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_USER_TOKEN_ERROR("UMS3018", "Invalid User Token", HttpStatus.UNAUTHORIZED),
    USER_TOKEN_EXPIRED_ERROR("UMS3019", "User Token has been Expired", HttpStatus.UNAUTHORIZED),
    FORGET_PASSWORD_USER_VALIDATION_ERROR("UMS3020", "Error Occurred While Validating the User in Forget Password", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSWORD_RESET_ERROR("UMS3021", "Error Occurred Resetting the Password", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSWORD_HISTORY_ERROR("UMS3022", "New password cannot match last 3 used passwords", HttpStatus.BAD_REQUEST),
    INVALID_SESSION_TOKEN_ERROR("UMS3023", "Invalid session token", HttpStatus.BAD_REQUEST),
    GET_APPLICATION_CONFIGURATIONS_ERROR("UMS3024", "Error Occurred while Retrieving the Application Configurations", HttpStatus.BAD_REQUEST),
    ACCOUNT_LOCKED_ERROR("UMS3025", "Account is Temporarily Locked", HttpStatus.BAD_REQUEST),
    ENTERPRISE_USER_VALIDATION_ERROR("UMS3026", "Invalid login credentials. Please try again", HttpStatus.BAD_REQUEST),
    GET_ENTERPRISE_USER_DETAILS_ERROR("UMS3027", "Error occurred while Retrieving the Enterprise User Details", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_MSISDN_FORMAT_ERROR("UMS3028", "Invalid MSISDN Format Found", HttpStatus.BAD_REQUEST),
    GET_ENTERPRISE_USER_LIST_ERROR("UMS3029", "Error occurred while Retrieving the Enterprise User List", HttpStatus.INTERNAL_SERVER_ERROR),
    ENTERPRISE_USER_NOT_EXISTS_ERROR("UMS3030", "User not found or status not pending", HttpStatus.BAD_REQUEST),
    APPROVE_ENTERPRISE_USER_ERROR("UMS3031", "Error occurred while Approving the Enterprise User", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_API_CALL_ERROR("UMS3032", "Error Occurred While Calling the External API", HttpStatus.INTERNAL_SERVER_ERROR),
    GET_RECENT_ACTIVITY_ERROR("UMS3033", "Error Occurred While getting recent activity list", HttpStatus.INTERNAL_SERVER_ERROR),
    CREATE_ENTERPRISE_ACTIVITY_ERROR("UMS3034", "Error Occurred While creating enterprise activity", HttpStatus.INTERNAL_SERVER_ERROR),

    ;

    private final String code;
    private final String description;
    private final HttpStatus httpStatus;

    DomainErrorCode(String code, String description, HttpStatus httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}