/*
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

/**
 * This class contains all the constant fields which will be used in application layer implementations.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/16
 */
public class Constant {

    public static final String REQ_ID = "requestId";
    public static final String RES_CODE = "code";
    public static final String RES_HTTP_CODE = "200";
    public static final String RES_DESC = "desc";
    public static final String TIMESTAMP = "timeStamp";
    public static final String VALIDATION_ERROR = "validation error : ";
    public static final String VALIDATION_ERROR_TEXT = "UMS2000";
    public static final String SYSTEM_ERROR_TEXT = "UMS3001";
    public static final String RES_HEADER = "responseHeader";
    public static final String SYSTEM_USER = "SystemUser";
    public static final String USER_ID = "userId";
    public static final String EMAIL = "email";
    public static final String TOKEN_ID = "tokenId";
    public static final String FORGOT_PASSWORD_TEMPLATE_ID = "forgot-password";
    public static final String FORGOT_PASSWORD_SUCCESS_EMAIL_TEMPLATE_ID = "forgot-password-success";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String RESET_LINK = "resetLink";
    public static final String EXPIRATION_TIME = "expirationTime";
    public static final String MINUTES = " Minutes";
    public static final String COMMON_PASSWORDS = "COMMON_PASSWORDS";


    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    public static final String SAVE_ADMIN_ACTIVITY_ERROR_IN_SERVER_EXCEPTION_OCCURRED = "Exception occurred while save admin recent activity in the Server";
    public static final String CAPTURE_ADMIN_ACTIVITY_ERROR_IN_SERVER_EXCEPTION_OCCURRED = "Exception occurred while capture admin recent activity in the Server";
    public static final String SAVE_ENTERPRISE_ACTIVITY_ERROR_IN_SERVER_EXCEPTION_OCCURRED = "Exception occurred while save enterprise recent activity in the Server";
    private Constant() {
    }
}
